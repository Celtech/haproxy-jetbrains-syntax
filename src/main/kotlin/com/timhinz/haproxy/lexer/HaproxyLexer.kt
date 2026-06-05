package com.timhinz.haproxy.lexer

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class HaproxyLexer : LexerBase() {

    companion object {
        // ── States ────────────────────────────────────────────────────────────
        const val STATE_BOL                    = 0
        const val STATE_AFTER_SECTION          = 1
        const val STATE_AFTER_OPTION           = 2
        const val STATE_AFTER_BALANCE          = 3
        const val STATE_AFTER_MODE             = 4
        const val STATE_IN_LINE                = 5
        const val STATE_AFTER_ACL              = 6
        const val STATE_AFTER_ACL_NAME         = 7
        const val STATE_AFTER_ACTION_DIRECTIVE = 8
        const val STATE_AFTER_STATS            = 9
        const val STATE_AFTER_TIMEOUT          = 10
        // Persistent line states (active until newline resets to BOL)
        const val STATE_LOG_LINE               = 11
        const val STATE_REDIRECT_LINE          = 12
        const val STATE_CAPTURE_LINE           = 13
        const val STATE_AFTER_BIND             = 14
        const val STATE_BIND_LINE              = 15
        const val STATE_AFTER_SERVER           = 16
        const val STATE_AFTER_SERVER_NAME      = 17
        const val STATE_SERVER_LINE            = 18
        const val STATE_AFTER_STICK            = 19
        const val STATE_HTTP_CHECK_LINE        = 20
        const val STATE_TCP_CHECK_LINE         = 21
        const val STATE_AFTER_FILTER           = 22
        const val STATE_AFTER_HOLD             = 23
        const val STATE_STICK_TABLE_LINE       = 24
        const val STATE_AFTER_DECLARE          = 25
        const val STATE_STATS_LINE             = 26  // persistent: all stats params
        const val STATE_SSL_OPTIONS_LINE       = 27  // persistent: ssl-default-*-options values
        const val STATE_COOKIE_LINE            = 28  // persistent: cookie directive params
    }

    private var buffer: CharSequence = ""
    private var bufferEnd: Int = 0
    private var tokenStart: Int = 0
    private var tokenEnd: Int = 0
    private var tokenType: IElementType? = null
    private var state: Int = STATE_BOL

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer    = buffer
        this.bufferEnd = endOffset
        this.tokenEnd  = startOffset
        this.state     = initialState
        advance()
    }

    override fun advance() {
        tokenStart = tokenEnd
        tokenType = if (tokenStart < bufferEnd) readNextToken() else null
    }

    override fun getTokenType(): IElementType? = tokenType
    override fun getTokenStart(): Int = tokenStart
    override fun getTokenEnd(): Int = tokenEnd
    override fun getState(): Int = state
    override fun getBufferSequence(): CharSequence = buffer
    override fun getBufferEnd(): Int = bufferEnd

    private fun readNextToken(): IElementType {
        var pos = tokenStart
        val ch  = buffer[pos]

        if (ch == '\n' || ch == '\r') {
            pos++
            if (ch == '\r' && pos < bufferEnd && buffer[pos] == '\n') pos++
            tokenEnd = pos
            state = STATE_BOL
            return TokenType.WHITE_SPACE
        }

        if (ch == ' ' || ch == '\t') {
            while (pos < bufferEnd && (buffer[pos] == ' ' || buffer[pos] == '\t')) pos++
            tokenEnd = pos
            return TokenType.WHITE_SPACE
        }

        if (ch == '#') {
            while (pos < bufferEnd && buffer[pos] != '\n' && buffer[pos] != '\r') pos++
            tokenEnd = pos
            return HaproxyTokenTypes.COMMENT
        }

        if (ch == '"' || ch == '\'') {
            val quote = ch
            pos++
            while (pos < bufferEnd && buffer[pos] != quote && buffer[pos] != '\n' && buffer[pos] != '\r') {
                if (buffer[pos] == '\\') pos++
                if (pos < bufferEnd) pos++
            }
            if (pos < bufferEnd && buffer[pos] == quote) pos++
            tokenEnd = pos
            state = STATE_IN_LINE
            return HaproxyTokenTypes.STRING
        }

        if (ch.isLetterOrDigit() || ch == '_' || ch == '-' || ch == '*' || ch == '~') {
            val wordStart = pos
            while (pos < bufferEnd && isWordChar(buffer[pos])) pos++
            tokenEnd = pos
            return classifyWord(buffer.substring(wordStart, pos))
        }

        tokenEnd = pos + 1
        return if (ch.code >= 32) HaproxyTokenTypes.IDENTIFIER else TokenType.BAD_CHARACTER
    }

    private fun isWordChar(ch: Char): Boolean =
        ch.isLetterOrDigit() || ch == '_' || ch == '-' || ch == '.' ||
        ch == ':' || ch == '/' || ch == '*' || ch == '@' || ch == '~' || ch == '+'

    private fun classifyWord(word: String): IElementType = when (state) {

        STATE_BOL -> when {
            word in HaproxyKeywords.SECTION_KEYWORDS -> {
                state = STATE_AFTER_SECTION; HaproxyTokenTypes.SECTION_KEYWORD
            }
            word in HaproxyKeywords.DIRECTIVE_KEYWORDS -> {
                state = nextStateForDirective(word); HaproxyTokenTypes.KEYWORD
            }
            isNumber(word) -> { state = STATE_IN_LINE; HaproxyTokenTypes.NUMBER }
            else           -> { state = STATE_IN_LINE; HaproxyTokenTypes.IDENTIFIER }
        }

        STATE_AFTER_SECTION -> {
            state = STATE_IN_LINE; HaproxyTokenTypes.SECTION_NAME
        }

        STATE_AFTER_OPTION -> {
            state = STATE_IN_LINE
            if (word in HaproxyKeywords.OPTION_KEYWORDS) HaproxyTokenTypes.OPTION_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_BALANCE -> {
            state = STATE_IN_LINE
            if (word in HaproxyKeywords.BALANCE_ALGORITHMS) HaproxyTokenTypes.PARAM_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_MODE -> {
            state = STATE_IN_LINE
            if (word in HaproxyKeywords.MODE_VALUES) HaproxyTokenTypes.PARAM_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_ACL -> {
            state = STATE_AFTER_ACL_NAME; HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_ACL_NAME -> {
            state = STATE_IN_LINE
            if (word in HaproxyKeywords.ACL_FETCH_METHODS) HaproxyTokenTypes.SUB_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_ACTION_DIRECTIVE -> {
            state = if (word == "redirect") STATE_REDIRECT_LINE else STATE_IN_LINE
            if (word in HaproxyKeywords.ACTION_KEYWORDS) HaproxyTokenTypes.SUB_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_STATS -> {
            state = STATE_IN_LINE
            if (word in HaproxyKeywords.STATS_LINE_KEYWORDS) HaproxyTokenTypes.SUB_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_STATS_LINE -> when {
            word in HaproxyKeywords.STATS_LINE_KEYWORDS -> HaproxyTokenTypes.SUB_KEYWORD
            isNumber(word)                              -> HaproxyTokenTypes.NUMBER
            else                                        -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_SSL_OPTIONS_LINE -> when {
            word in HaproxyKeywords.SSL_OPTIONS_KEYWORDS -> HaproxyTokenTypes.PARAM_KEYWORD
            else                                         -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_COOKIE_LINE -> when {
            word in HaproxyKeywords.COOKIE_KEYWORDS -> HaproxyTokenTypes.SUB_KEYWORD
            isNumber(word)                          -> HaproxyTokenTypes.NUMBER
            else                                    -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_TIMEOUT -> {
            state = STATE_IN_LINE
            if (word in HaproxyKeywords.TIMEOUT_TYPES) HaproxyTokenTypes.SUB_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_STICK -> {
            state = STATE_IN_LINE
            if (word in HaproxyKeywords.STICK_TYPES) HaproxyTokenTypes.SUB_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_FILTER -> {
            state = STATE_IN_LINE
            if (word in HaproxyKeywords.FILTER_TYPES) HaproxyTokenTypes.SUB_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_HOLD -> {
            state = STATE_IN_LINE
            if (word in HaproxyKeywords.HOLD_TYPES) HaproxyTokenTypes.SUB_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_DECLARE -> {
            state = STATE_CAPTURE_LINE
            if (word in HaproxyKeywords.DECLARE_TYPES) HaproxyTokenTypes.SUB_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        // ── Persistent line states ─────────────────────────────────────────

        STATE_LOG_LINE -> when {
            word in HaproxyKeywords.LOG_KEYWORDS -> HaproxyTokenTypes.SUB_KEYWORD
            isNumber(word)                       -> HaproxyTokenTypes.NUMBER
            else                                 -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_REDIRECT_LINE -> when {
            word in HaproxyKeywords.INLINE_KEYWORDS   -> HaproxyTokenTypes.KEYWORD
            word in HaproxyKeywords.REDIRECT_KEYWORDS -> HaproxyTokenTypes.SUB_KEYWORD
            isNumber(word)                            -> HaproxyTokenTypes.NUMBER
            else                                      -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_CAPTURE_LINE -> when {
            word in HaproxyKeywords.CAPTURE_KEYWORDS -> HaproxyTokenTypes.SUB_KEYWORD
            isNumber(word)                           -> HaproxyTokenTypes.NUMBER
            else                                     -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_BIND -> {
            state = STATE_BIND_LINE; HaproxyTokenTypes.IDENTIFIER
        }

        STATE_BIND_LINE -> when {
            word in HaproxyKeywords.BIND_QUALIFIERS -> HaproxyTokenTypes.PARAM_KEYWORD
            isNumber(word)                          -> HaproxyTokenTypes.NUMBER
            else                                    -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_SERVER -> {
            state = STATE_AFTER_SERVER_NAME; HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_SERVER_NAME -> {
            state = STATE_SERVER_LINE; HaproxyTokenTypes.IDENTIFIER
        }

        STATE_SERVER_LINE -> when {
            word in HaproxyKeywords.SERVER_QUALIFIERS -> HaproxyTokenTypes.PARAM_KEYWORD
            isNumber(word)                            -> HaproxyTokenTypes.NUMBER
            else                                      -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_HTTP_CHECK_LINE -> when {
            word in HaproxyKeywords.HTTP_CHECK_KEYWORDS -> HaproxyTokenTypes.SUB_KEYWORD
            isNumber(word)                              -> HaproxyTokenTypes.NUMBER
            else                                        -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_TCP_CHECK_LINE -> when {
            word in HaproxyKeywords.TCP_CHECK_KEYWORDS -> HaproxyTokenTypes.SUB_KEYWORD
            isNumber(word)                             -> HaproxyTokenTypes.NUMBER
            else                                       -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_STICK_TABLE_LINE -> when {
            word in HaproxyKeywords.STICK_TABLE_KEYWORDS -> HaproxyTokenTypes.SUB_KEYWORD
            isNumber(word)                               -> HaproxyTokenTypes.NUMBER
            else                                         -> HaproxyTokenTypes.IDENTIFIER
        }

        else /* STATE_IN_LINE */ -> when {
            word in HaproxyKeywords.INLINE_KEYWORDS    -> HaproxyTokenTypes.KEYWORD
            word in HaproxyKeywords.BALANCE_ALGORITHMS -> HaproxyTokenTypes.PARAM_KEYWORD
            word in HaproxyKeywords.MODE_VALUES        -> HaproxyTokenTypes.PARAM_KEYWORD
            isNumber(word)                             -> HaproxyTokenTypes.NUMBER
            else                                       -> HaproxyTokenTypes.IDENTIFIER
        }
    }

    private fun nextStateForDirective(word: String) = when (word) {
        "option"                                    -> STATE_AFTER_OPTION
        "balance"                                   -> STATE_AFTER_BALANCE
        "mode"                                      -> STATE_AFTER_MODE
        "acl"                                       -> STATE_AFTER_ACL
        "http-request", "http-response",
        "tcp-request",  "tcp-response"              -> STATE_AFTER_ACTION_DIRECTIVE
        "stats"                                     -> STATE_STATS_LINE
        "ssl-default-bind-options",
        "ssl-default-server-options"                -> STATE_SSL_OPTIONS_LINE
        "cookie"                                    -> STATE_COOKIE_LINE
        "timeout"                                   -> STATE_AFTER_TIMEOUT
        "log"                                       -> STATE_LOG_LINE
        "redirect"                                  -> STATE_REDIRECT_LINE
        "capture"                                   -> STATE_CAPTURE_LINE
        "bind"                                      -> STATE_AFTER_BIND
        "server"                                    -> STATE_AFTER_SERVER
        "default-server"                            -> STATE_SERVER_LINE
        "stick"                                     -> STATE_AFTER_STICK
        "stick-table"                               -> STATE_STICK_TABLE_LINE
        "http-check"                                -> STATE_HTTP_CHECK_LINE
        "tcp-check"                                 -> STATE_TCP_CHECK_LINE
        "filter"                                    -> STATE_AFTER_FILTER
        "hold"                                      -> STATE_AFTER_HOLD
        "declare"                                   -> STATE_AFTER_DECLARE
        else                                        -> STATE_IN_LINE
    }

    private fun isNumber(word: String): Boolean {
        if (word.isEmpty()) return false
        if (word.all { it.isDigit() }) return true
        for (unit in listOf("ms", "s", "m", "h", "d")) {
            if (word.endsWith(unit)) {
                val prefix = word.dropLast(unit.length)
                if (prefix.isNotEmpty() && prefix.all { it.isDigit() }) return true
            }
        }
        return false
    }
}
