package com.timhinz.haproxy.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiFile
import com.intellij.util.ProcessingContext
import com.timhinz.haproxy.HaproxyLanguage
import com.timhinz.haproxy.lexer.HaproxyKeywords

class HaproxyCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            HaproxyLineCompletionProvider()
        )
    }
}

private class HaproxyLineCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        if (parameters.position.language != HaproxyLanguage) return

        val document = parameters.editor.document
        val offset = parameters.offset
        val lineStart = document.getLineStartOffset(document.getLineNumber(offset))
        val lineText = document.getText(TextRange(lineStart, offset))

        // Use everything after the last whitespace as the prefix for filtering
        val prefix = lineText.substringAfterLast(' ').substringAfterLast('\t')
        val activeResult = result.withPrefixMatcher(prefix)

        // Words fully typed before what's currently being typed
        val hasTrailingSpace = lineText.isNotEmpty() && lineText.last().isWhitespace()
        val allTokens = lineText.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        val completedTokens = if (hasTrailingSpace) allTokens else allTokens.dropLast(1)

        for ((text, typeText) in getCompletions(completedTokens, parameters.originalFile)) {
            activeResult.addElement(
                LookupElementBuilder.create(text).withTypeText(typeText, true)
            )
        }
    }

    private fun getCompletions(completedTokens: List<String>, file: PsiFile): List<Pair<String, String>> {
        if (completedTokens.isEmpty()) {
            return HaproxyKeywords.SECTION_KEYWORDS.map { it to "section" } +
                   HaproxyKeywords.DIRECTIVE_KEYWORDS.map { it to "directive" }
        }

        val directive = completedTokens[0]

        return when {
            completedTokens.size == 1 -> when (directive) {
                "option" ->
                    HaproxyKeywords.OPTION_KEYWORDS.map { it to "option" }
                "balance" ->
                    HaproxyKeywords.BALANCE_ALGORITHMS.map { it to "algorithm" }
                "mode" ->
                    HaproxyKeywords.MODE_VALUES.map { it to "mode" }
                "timeout" ->
                    HaproxyKeywords.TIMEOUT_TYPES.map { it to "timeout type" }
                "http-request", "http-response", "tcp-request", "tcp-response" ->
                    HaproxyKeywords.ACTION_KEYWORDS.map { it to "action" }
                "stats" ->
                    HaproxyKeywords.STATS_LINE_KEYWORDS.map { it to "stats" }
                "log" ->
                    HaproxyKeywords.LOG_KEYWORDS.map { it to "log" }
                "redirect" ->
                    HaproxyKeywords.REDIRECT_KEYWORDS.map { it to "redirect" }
                "capture" ->
                    HaproxyKeywords.CAPTURE_KEYWORDS.map { it to "capture" }
                "filter" ->
                    HaproxyKeywords.FILTER_TYPES.map { it to "filter type" }
                "hold" ->
                    HaproxyKeywords.HOLD_TYPES.map { it to "hold type" }
                "stick" ->
                    HaproxyKeywords.STICK_TYPES.map { it to "stick" }
                "stick-table" ->
                    HaproxyKeywords.STICK_TABLE_KEYWORDS.map { it to "stick-table" }
                "http-check" ->
                    HaproxyKeywords.HTTP_CHECK_KEYWORDS.map { it to "http-check" }
                "tcp-check" ->
                    HaproxyKeywords.TCP_CHECK_KEYWORDS.map { it to "tcp-check" }
                "declare" ->
                    HaproxyKeywords.DECLARE_TYPES.map { it to "declare" }
                "ssl-default-bind-options", "ssl-default-server-options" ->
                    HaproxyKeywords.SSL_OPTIONS_KEYWORDS.map { it to "ssl option" }
                "cookie" ->
                    HaproxyKeywords.COOKIE_KEYWORDS.map { it to "cookie" }
                "use_backend", "default_backend" ->
                    collectDefinedNames(file, setOf("backend", "listen")).map { it to "backend" }
                // bind <addr>, server <name>, acl <name>: next word is user-defined
                else -> emptyList()
            }

            else -> when {
                // bind <addr> [qualifiers…]
                directive == "bind" && completedTokens.size >= 2 ->
                    HaproxyKeywords.BIND_QUALIFIERS.map { it to "bind qualifier" }

                // server <name> <addr> [qualifiers…]
                directive == "server" && completedTokens.size >= 3 ->
                    HaproxyKeywords.SERVER_QUALIFIERS.map { it to "server qualifier" }

                // default-server [qualifiers…]
                directive == "default-server" ->
                    HaproxyKeywords.SERVER_QUALIFIERS.map { it to "server qualifier" }

                // acl <name> <fetch-method…>
                directive == "acl" && completedTokens.size == 2 ->
                    HaproxyKeywords.ACL_FETCH_METHODS.map { it to "fetch method" }

                // Persistent line states — keep offering their keywords regardless of position
                directive == "log" ->
                    HaproxyKeywords.LOG_KEYWORDS.map { it to "log" }
                directive == "redirect" ->
                    HaproxyKeywords.REDIRECT_KEYWORDS.map { it to "redirect" }
                directive == "capture" ->
                    HaproxyKeywords.CAPTURE_KEYWORDS.map { it to "capture" }
                directive == "stats" ->
                    HaproxyKeywords.STATS_LINE_KEYWORDS.map { it to "stats" }
                directive in listOf("ssl-default-bind-options", "ssl-default-server-options") ->
                    HaproxyKeywords.SSL_OPTIONS_KEYWORDS.map { it to "ssl option" }
                directive == "cookie" ->
                    HaproxyKeywords.COOKIE_KEYWORDS.map { it to "cookie" }
                directive == "http-check" ->
                    HaproxyKeywords.HTTP_CHECK_KEYWORDS.map { it to "http-check" }
                directive == "tcp-check" ->
                    HaproxyKeywords.TCP_CHECK_KEYWORDS.map { it to "tcp-check" }
                directive == "stick-table" ->
                    HaproxyKeywords.STICK_TABLE_KEYWORDS.map { it to "stick-table" }

                // use_backend <name> if/unless <acl…>
                directive in listOf("use_backend", "default_backend") ->
                    HaproxyKeywords.INLINE_KEYWORDS.map { it to "condition" } +
                    collectDefinedNames(file, setOf("acl")).map { it to "ACL" }

                // Any line with if/unless: offer ACL names and condition keywords
                completedTokens.any { it == "if" || it == "unless" } ->
                    collectDefinedNames(file, setOf("acl")).map { it to "ACL" } +
                    HaproxyKeywords.INLINE_KEYWORDS.map { it to "condition" }

                else -> emptyList()
            }
        }
    }

    private fun collectDefinedNames(file: PsiFile, types: Set<String>): List<String> {
        val text = file.text
        val names = mutableListOf<String>()

        val sectionTypes = types.intersect(
            setOf("backend", "frontend", "listen", "resolvers", "peers",
                  "userlist", "mailers", "ring", "program", "cache", "fcgi-app")
        )
        if (sectionTypes.isNotEmpty()) {
            Regex("""^(${sectionTypes.joinToString("|")})\s+(\S+)""", setOf(RegexOption.MULTILINE))
                .findAll(text)
                .forEach { names.add(it.groupValues[2]) }
        }

        if ("acl" in types) {
            Regex("""^\s*acl\s+(\S+)""", setOf(RegexOption.MULTILINE))
                .findAll(text)
                .forEach { names.add(it.groupValues[1]) }
        }

        if ("server" in types) {
            Regex("""^\s*server\s+(\S+)""", setOf(RegexOption.MULTILINE))
                .findAll(text)
                .forEach { names.add(it.groupValues[1]) }
        }

        return names.distinct()
    }
}
