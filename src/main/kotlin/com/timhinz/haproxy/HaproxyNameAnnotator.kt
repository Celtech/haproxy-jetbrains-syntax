package com.timhinz.haproxy

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.timhinz.haproxy.highlighting.HaproxyHighlightingColors

/**
 * Highlights every occurrence of a user-defined name (section names, ACL names,
 * server names) at both the definition site and at known reference positions.
 *
 * "Known reference positions" means we only highlight names that appear after
 * use_backend, default_backend, use-server, or inside if/unless conditions —
 * so a word like `https` in `redirect scheme https` is never touched, while
 * `https` in `frontend https` or `use_backend https` is correctly highlighted.
 */
class HaproxyNameAnnotator : ExternalAnnotator<HaproxyNameAnnotator.Info, List<TextRange>>() {

    data class Info(
        val text: String,
        val definedNames: Set<String>,
        val skipRanges: List<TextRange>   // comments + strings — never annotate inside these
    )

    // ── Patterns used for both collection and annotation ──────────────────────

    private val DEFINITION_PATTERNS = listOf(
        // section-level block names
        Regex("""^(?:backend|frontend|listen|resolvers|peers|userlist|mailers|ring|program)\s+(\S+)""", RegexOption.MULTILINE),
        // ACL names
        Regex("""^[ \t]+acl\s+(\S+)""",    RegexOption.MULTILINE),
        // server names
        Regex("""^[ \t]+server\s+(\S+)""", RegexOption.MULTILINE),
        // peer names (inside peers section)
        Regex("""^[ \t]+peer\s+(\S+)""",   RegexOption.MULTILINE),
    )

    // ── Step 1: collect defined names (EDT) ───────────────────────────────────

    override fun collectInformation(file: PsiFile): Info? {
        if (file.language != HaproxyLanguage) return null
        val text  = file.text
        val names = mutableSetOf<String>()
        DEFINITION_PATTERNS.forEach { pat ->
            pat.findAll(text).forEach { m ->
                m.groups[1]?.value?.takeIf { it.isNotBlank() }?.let { names += it }
            }
        }
        return Info(text, names, buildSkipRanges(text))
    }

    // ── Step 2: find occurrences in appropriate positions (background) ────────

    override fun doAnnotate(info: Info): List<TextRange> {
        if (info.definedNames.isEmpty()) return emptyList()
        val text  = info.text
        val names = info.definedNames
        val skip  = info.skipRanges
        val result = mutableListOf<TextRange>()

        fun addIfKnown(g: MatchGroup?) {
            g ?: return
            // \S+ can include trailing punctuation; strip it for the name comparison
            val raw  = g.value
            val name = raw.trimEnd { !it.isLetterOrDigit() && it != '_' && it != '-' }
            if (name !in names) return
            val range = TextRange(g.range.first, g.range.first + name.length)
            if (skip.none { it.contains(range) }) result += range
        }

        // ── Definition sites ──────────────────────────────────────────────────
        DEFINITION_PATTERNS.forEach { pat ->
            pat.findAll(text).forEach { m -> addIfKnown(m.groups[1]) }
        }

        // ── Backend references ────────────────────────────────────────────────
        Regex("""(?:use_backend|default_backend)\s+(\S+)""", RegexOption.MULTILINE)
            .findAll(text).forEach { m -> addIfKnown(m.groups[1]) }

        // ── ACL condition references ──────────────────────────────────────────
        // Capture everything after `if` / `unless` to end of line, then pick out
        // every word that is a defined name.  This handles:
        //   …if is_api
        //   …if host_www is_ssl        (implicit AND)
        //   …if { ssl_fc } or bad_bot  (mixed inline + named)
        //   …if !is_admin
        Regex("""(?:if|unless)\s+(.+)$""", RegexOption.MULTILINE).findAll(text).forEach { m ->
            val condStr   = m.groups[1]?.value ?: return@forEach
            val condStart = m.groups[1]!!.range.first
            Regex("""(\w[\w_-]*)""").findAll(condStr).forEach { wm ->
                val name = wm.value
                if (name !in names) return@forEach
                val range = TextRange(condStart + wm.range.first, condStart + wm.range.last + 1)
                if (skip.none { it.contains(range) }) result += range
            }
        }

        // ── Server references ─────────────────────────────────────────────────
        Regex("""use-server\s+(\S+)""", RegexOption.MULTILINE)
            .findAll(text).forEach { m -> addIfKnown(m.groups[1]) }

        return result
    }

    // ── Step 3: apply (EDT) ──────────────────────────────────────────────────

    override fun apply(file: PsiFile, refs: List<TextRange>, holder: AnnotationHolder) {
        for (range in refs) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(range)
                .textAttributes(HaproxyHighlightingColors.USER_DEFINED_REF)
                .create()
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Pre-compute comment and quoted-string ranges so we never annotate inside them. */
    private fun buildSkipRanges(text: String): List<TextRange> {
        val ranges = mutableListOf<TextRange>()
        var i = 0
        while (i < text.length) {
            when (val ch = text[i]) {
                '#' -> {
                    val start = i
                    while (i < text.length && text[i] != '\n' && text[i] != '\r') i++
                    ranges += TextRange(start, i)
                }
                '"', '\'' -> {
                    val start = i++
                    while (i < text.length && text[i] != ch && text[i] != '\n' && text[i] != '\r') {
                        if (text[i] == '\\') i++
                        i++
                    }
                    if (i < text.length) i++
                    ranges += TextRange(start, i)
                }
                else -> i++
            }
        }
        return ranges
    }
}
