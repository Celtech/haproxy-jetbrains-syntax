package com.timhinz.haproxy.highlighting

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.timhinz.haproxy.lexer.HaproxyLexer
import com.timhinz.haproxy.lexer.HaproxyTokenTypes

class HaproxySyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = HaproxyLexer()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> = when (tokenType) {
        HaproxyTokenTypes.COMMENT         -> pack(HaproxyHighlightingColors.COMMENT)
        HaproxyTokenTypes.SECTION_KEYWORD -> pack(HaproxyHighlightingColors.SECTION_KEYWORD)
        HaproxyTokenTypes.SECTION_NAME    -> pack(HaproxyHighlightingColors.SECTION_NAME)
        HaproxyTokenTypes.KEYWORD         -> pack(HaproxyHighlightingColors.KEYWORD)
        HaproxyTokenTypes.OPTION_KEYWORD  -> pack(HaproxyHighlightingColors.OPTION_KEYWORD)
        HaproxyTokenTypes.PARAM_KEYWORD   -> pack(HaproxyHighlightingColors.PARAM_KEYWORD)
        HaproxyTokenTypes.SUB_KEYWORD     -> pack(HaproxyHighlightingColors.SUB_KEYWORD)
        HaproxyTokenTypes.NUMBER          -> pack(HaproxyHighlightingColors.NUMBER)
        HaproxyTokenTypes.STRING          -> pack(HaproxyHighlightingColors.STRING)
        HaproxyTokenTypes.IDENTIFIER      -> pack(HaproxyHighlightingColors.IDENTIFIER)
        TokenType.BAD_CHARACTER           -> pack(HaproxyHighlightingColors.BAD_CHARACTER)
        else                              -> EMPTY
    }

    companion object {
        private val EMPTY = emptyArray<TextAttributesKey>()
        private fun pack(key: TextAttributesKey) = arrayOf(key)
    }
}
