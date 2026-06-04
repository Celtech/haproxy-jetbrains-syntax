package com.timhinz.haproxy.lexer

import com.intellij.psi.tree.IElementType
import com.timhinz.haproxy.HaproxyLanguage

class HaproxyTokenType(debugName: String) : IElementType(debugName, HaproxyLanguage)

object HaproxyTokenTypes {
    @JvmField val COMMENT        = HaproxyTokenType("COMMENT")
    @JvmField val SECTION_KEYWORD = HaproxyTokenType("SECTION_KEYWORD")  // global, frontend, backend …
    @JvmField val SECTION_NAME   = HaproxyTokenType("SECTION_NAME")      // name after section keyword
    @JvmField val KEYWORD        = HaproxyTokenType("KEYWORD")           // directives: bind, server, acl …
    @JvmField val OPTION_KEYWORD = HaproxyTokenType("OPTION_KEYWORD")    // values after `option`
    @JvmField val PARAM_KEYWORD  = HaproxyTokenType("PARAM_KEYWORD")     // balance algos, mode values
    @JvmField val NUMBER         = HaproxyTokenType("NUMBER")
    @JvmField val STRING         = HaproxyTokenType("STRING")
    @JvmField val SUB_KEYWORD    = HaproxyTokenType("SUB_KEYWORD")   // set-header, hdr_end, accept …
    @JvmField val IDENTIFIER     = HaproxyTokenType("IDENTIFIER")
}
