package com.timhinz.haproxy

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.lang.PsiBuilder
import com.timhinz.haproxy.lexer.HaproxyLexer
import com.timhinz.haproxy.lexer.HaproxyTokenTypes

class HaproxyParserDefinition : ParserDefinition {

    companion object {
        val FILE_NODE = IFileElementType(HaproxyLanguage)
    }

    override fun createLexer(project: Project): Lexer = HaproxyLexer()

    override fun createParser(project: Project): PsiParser = PsiParser { root, builder ->
        // Flat parser: consume every token directly under the root.
        // Replacing this with a Grammar-Kit parser later is a drop-in swap.
        val marker = builder.mark()
        while (!builder.eof()) builder.advanceLexer()
        marker.done(root)
        builder.treeBuilt
    }

    override fun getFileNodeType(): IFileElementType = FILE_NODE

    override fun getWhitespaceTokens(): TokenSet =
        TokenSet.create(TokenType.WHITE_SPACE)

    override fun getCommentTokens(): TokenSet =
        TokenSet.create(HaproxyTokenTypes.COMMENT)

    override fun getStringLiteralElements(): TokenSet =
        TokenSet.create(HaproxyTokenTypes.STRING)

    override fun createElement(node: ASTNode): PsiElement =
        throw UnsupportedOperationException("No composite elements yet")

    override fun createFile(viewProvider: FileViewProvider): PsiFile =
        HaproxyFile(viewProvider)
}

class HaproxyFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, HaproxyLanguage) {
    override fun getFileType(): com.intellij.openapi.fileTypes.FileType = HaproxyFileType
}
