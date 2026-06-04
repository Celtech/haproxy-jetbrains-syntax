package com.timhinz.haproxy.highlighting

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey

object HaproxyHighlightingColors {
    @JvmField val COMMENT         = createTextAttributesKey("HAPROXY_COMMENT",         DefaultLanguageHighlighterColors.LINE_COMMENT)
    @JvmField val SECTION_KEYWORD = createTextAttributesKey("HAPROXY_SECTION_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
    @JvmField val SECTION_NAME    = createTextAttributesKey("HAPROXY_SECTION_NAME",    DefaultLanguageHighlighterColors.CLASS_NAME)
    @JvmField val KEYWORD         = createTextAttributesKey("HAPROXY_KEYWORD",         DefaultLanguageHighlighterColors.KEYWORD)
    @JvmField val OPTION_KEYWORD  = createTextAttributesKey("HAPROXY_OPTION_KEYWORD",  DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
    @JvmField val PARAM_KEYWORD   = createTextAttributesKey("HAPROXY_PARAM_KEYWORD",   DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
    @JvmField val SUB_KEYWORD     = createTextAttributesKey("HAPROXY_SUB_KEYWORD",     DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
    @JvmField val NUMBER          = createTextAttributesKey("HAPROXY_NUMBER",          DefaultLanguageHighlighterColors.NUMBER)
    @JvmField val STRING          = createTextAttributesKey("HAPROXY_STRING",          DefaultLanguageHighlighterColors.STRING)
    @JvmField val IDENTIFIER      = createTextAttributesKey("HAPROXY_IDENTIFIER",      DefaultLanguageHighlighterColors.IDENTIFIER)
    @JvmField val USER_DEFINED_REF = createTextAttributesKey("HAPROXY_USER_DEFINED_REF", DefaultLanguageHighlighterColors.INSTANCE_FIELD)
    @JvmField val BAD_CHARACTER   = createTextAttributesKey("HAPROXY_BAD_CHARACTER",   HighlighterColors.BAD_CHARACTER)
}
