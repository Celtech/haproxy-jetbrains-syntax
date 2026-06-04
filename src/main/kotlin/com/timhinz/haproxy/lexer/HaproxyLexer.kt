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

        // ── Keyword sets ──────────────────────────────────────────────────────

        private val SECTION_KEYWORDS = setOf(
            "global", "defaults", "frontend", "backend", "listen",
            "resolvers", "userlist", "peers", "mailers", "ring",
            "http-errors", "program",
            "cache",       // content caching (2.x)
            "fcgi-app"     // FastCGI app (2.x)
        )

        // Only highlighted at the start of a line.
        private val DIRECTIVE_KEYWORDS = setOf(
            // shared across sections
            "acl", "backlog", "balance", "bind", "bind-process",
            "block",         // deprecated request blocker
            "capture", "compression", "cookie",
            "declare", "default-server", "default_backend",
            "description", "disabled", "dispatch", "enabled",
            "email-alert", "email-alert level", "email-alert mailers",
            "email-alert myhostname", "email-alert from", "email-alert to",
            "errorfile", "errorfiles", "errorloc", "errorloc302", "errorloc303",
            "filter", "force-persist", "fullconn",
            "grace", "hash-type",
            "http-after-response", "http-check", "http-error",
            "http-request", "http-response", "http-reuse",
            "http-send-name-header", "id", "ignore-persist",
            "log", "log-format", "log-format-sd", "log-tag", "maxconn",
            "mode", "monitor", "monitor-fail", "monitor-net", "monitor-uri",
            "no", "option",
            "persist", "rate-limit", "redirect",
            "reqadd", "reqallow", "reqdel", "reqdeny",
            "reqiallow", "reqidel", "reqideny",
            "reqirep", "reqisetbe", "reqitarpit", "reqpass", "reqrep",
            "reqsetbe", "reqtarpit", "retries",
            "rspadd", "rspdel", "rspdeny",
            "rspidel", "rspideny",          // case-insensitive response del/deny
            "rspirep", "rspisetbe", "rsprep",
            "server", "server-state-file-name", "server-template",
            "stats", "stick", "stick-table",
            "tcp-check", "tcp-request", "tcp-response", "timeout",
            "transparent", "unique-id-format", "unique-id-header",
            "use-fcgi-app", "use-server", "use_backend",
            // resolvers-specific
            "nameserver", "resolve_retries", "hold", "accepted_payload_size",
            "parse-resolv-conf", "resolve-prefer",
            // userlist-specific
            "group", "user",
            // global-specific
            "anonkey",
            "busy-polling",
            "ca-base", "chroot", "close-spread-time", "cluster-secret",
            "cpu-map", "crt-base",
            "daemon", "debug", "default-path",
            "dynamic-cookie-key",
            "expose-experimental-directives",
            "external-check", "fd-hard-limit",
            "gid",
            "hard-stop-after",
            "h1-accept-payload-with-any-method",
            "h1-case-adjust", "h1-case-adjust-file",
            "h2-workaround-bogus-websocket-clients",
            "issuers-chain-path",
            "limited-quic", "localpeer",
            "log-send-hostname",
            "lua-load", "lua-load-per-thread", "lua-prepend-path",
            "master-worker", "max-spread-checks",
            "maxcompcpuusage", "maxcomprate",
            "maxconnrate", "maxpipes", "maxsessrate",
            "maxsslconn", "maxsslrate", "maxzlibmem",
            "mworker-max-reloads",
            "nbproc", "nbthread", "node",
            "no-quic",
            "noepoll", "noevports", "nogetaddrinfo", "nokqueue",
            "nopoll", "noreuseport", "nosepoll", "nosplice",
            "numa-cpu-mapping",
            "ocsp-update.mode",
            "pidfile", "prealloc-fd", "presetenv",
            "quiet", "resetenv",
            "server-state-base", "server-state-file",
            "set-dumpable",
            "setenv", "spread-checks",
            "ssl-default-bind-ciphers", "ssl-default-bind-ciphersuites",
            "ssl-default-bind-options",
            "ssl-default-server-ciphers", "ssl-default-server-ciphersuites",
            "ssl-default-server-options",
            "ssl-dh-param-file", "ssl-engine", "ssl-mode-async",
            "ssl-server-verify",
            "tune", "uid", "ulimit-n", "unix-bind", "unsetenv", "user",
            "load-server-state-from-file",
            "zero-warning",
            // peers
            "peer", "table",
            // mailers
            "mailer", "timeout",
            // cache-specific (inside cache section)
            "total-max-size", "max-object-size", "max-age", "process-vary",
            // fcgi-app-specific
            "docroot", "index", "log-stderr", "pass-header", "set-param"
        )

        // ACL connectors and comparison operators — highlighted wherever they appear.
        private val INLINE_KEYWORDS = setOf(
            "if", "unless", "else", "or", "and", "not",
            "eq", "ne", "lt", "le", "gt", "ge"   // integer comparison operators
        )

        private val OPTION_KEYWORDS = setOf(
            "abortonclose", "accept-invalid-http-request",
            "accept-invalid-http-response", "allbackups", "checkcache",
            "clitcpka", "clitcpka-cnt", "clitcpka-idle", "clitcpka-intvl",
            "contstats", "dontlog-normal", "dontlognull",
            "external-check",
            "forceclose", "forwardfor",
            "http-auth", "http-buffer-request",
            "http-ignore-probes", "http-keep-alive", "http-no-delay",
            "http-pretend-keepalive", "http-restrict-req-hdr-names",
            "http-server-close",
            "http-use-proxy-header", "http-use-htx", "httpchk",
            "httpclose", "httplog", "http_proxy",
            "idle-close-on-ssl-error", "independent-streams",
            "ldap-check", "log-health-checks", "log-separate-errors",
            "logasap", "mysql-check", "nolinger", "originalto", "persist",
            "pgsql-check", "prefer-last-server", "redispatch",
            "redis-check", "smtpchk", "socket-stats", "splice-auto",
            "splice-request", "splice-response",
            "srvtcpka", "srvtcpka-cnt", "srvtcpka-idle", "srvtcpka-intvl",
            "ssl", "ssl-hello-chk",
            "tcp-smart-accept", "tcp-smart-connect", "tcpka", "tcplog",
            "transparent"
        )

        private val BALANCE_ALGORITHMS = setOf(
            "roundrobin", "static-rr", "leastconn", "first", "source",
            "uri", "url_param", "hdr", "rdp-cookie", "random",
            "hash"
        )

        private val MODE_VALUES = setOf("http", "tcp", "health")

        private val TIMEOUT_TYPES = setOf(
            "check",
            "connect", "client", "client-fin", "client-hs",
            "http-keep-alive", "http-request",
            "queue",
            "resolve", "retry",
            "server", "server-fin",
            "tarpit", "tunnel"
        )

        private val LOG_KEYWORDS = setOf(
            "global", "stdout", "stderr",
            "local0", "local1", "local2", "local3", "local4",
            "local5", "local6", "local7",
            "kern", "user", "mail", "daemon", "auth", "syslog",
            "lpr", "news", "uucp", "cron", "authpriv", "ftp",
            "format", "raw", "rfc3164", "rfc5424", "short", "timed", "priority",
            "len", "sample",
            "emerg", "alert", "crit", "err", "warning", "notice", "info", "debug"
        )

        private val REDIRECT_KEYWORDS = setOf(
            "location", "prefix", "scheme", "code",
            "drop-query", "append-slash", "set-cookie", "clear-cookie"
        )

        private val CAPTURE_KEYWORDS = setOf(
            "request", "response", "cookie", "header", "len"
        )

        private val BIND_QUALIFIERS = setOf(
            "accept-proxy", "accept-netscaler-cip", "allow-0rtt",
            "alpn", "npn",
            "backlog",
            "ca-file", "ca-ignore-err", "ca-sign-file", "ca-sign-pass",
            "ca-sign-passphrase",
            "ciphers", "ciphersuites",
            "crl-file", "crt", "crt-ignore-err", "crt-list",
            "curves",
            "defer-accept",
            "ecdhe", "expose-fd",
            "force-sslv3", "force-tlsv10", "force-tlsv11",
            "force-tlsv12", "force-tlsv13",
            "generate-certificates",
            "id", "interface",
            "maxconn", "mss",
            "name", "nice", "no-ca-names",
            "no-sslv3", "no-tlsv10", "no-tlsv11", "no-tlsv12", "no-tlsv13",
            "no-tls-tickets",
            "npn",
            "prefer-server-ciphers", "process", "proto",
            "quic-force-retry",
            "severity-output", "ssl", "ssl-max-ver", "ssl-min-ver",
            "strict-sni",
            "tcp-ut", "tfo", "thread", "tls-ticket-keys", "transparent",
            "v4v6", "v6only", "verify"
        )

        private val SERVER_QUALIFIERS = setOf(
            "addr", "agent-addr", "agent-check", "agent-inter", "agent-port",
            "allow-0rtt", "alpn",
            "backup",
            "ca-file", "ca-file-send", "check", "check-alpn", "check-proto",
            "check-send-proxy", "check-ssl", "check-via-socks4",
            "ciphers", "ciphersuites", "client-sigalgs", "cookie",
            "crl-file", "crt", "curves",
            "disabled", "downinter",
            "error-limit",
            "fall", "fastinter", "force-tlsv10", "force-tlsv11",
            "force-tlsv12", "force-tlsv13",
            "hash-key",
            "id", "init-addr", "inter",
            "maxconn", "maxqueue", "minconn",
            "namespace", "no-agent-check", "no-ssl-reuse",
            "no-tlsv10", "no-tlsv11", "no-tlsv12", "no-tlsv13",
            "npn",
            "observe", "on-error", "on-marked-down", "on-marked-up",
            "pool-max-conn", "pool-purge-delay", "port", "proto",
            "redir", "resolve-net", "resolve-opts", "resolve-prefer",
            "resolvers", "rise",
            "send-proxy", "send-proxy-v2", "send-proxy-v2-ssl",
            "send-proxy-v2-ssl-cn", "set-proxy-v2-tlv-fmt",
            "sigalgs", "slowstart", "sni", "source", "ssl",
            "ssl-max-ver", "ssl-min-ver",
            "stick",
            "tcp-ut", "tfo", "tls-tickets", "track",
            "usesrc",
            "verify", "weight", "ws"
        )

        // http-request / http-response / tcp-request / tcp-response actions.
        private val ACTION_KEYWORDS = setOf(
            "accept", "add-acl", "add-header", "allow",
            "auth",          // http-request auth realm <realm>
            "cache-store",   // http-response cache-store <cache-name>
            "cache-use",     // http-request cache-use <cache-name>
            "capture", "connection", "content",
            "del-acl", "del-header", "del-map", "deny",
            "disable-l7-retry", "do-resolve",
            "early-hint",
            "inspect-delay", // tcp-request inspect-delay <time>
            "lua",
            "normalize-uri",
            "redirect", "reject", "replace-header", "replace-path",
            "replace-query", "replace-uri", "replace-value", "return",
            "sc-add-gpc0", "sc-add-gpc1",
            "sc-inc-gpc", "sc-inc-gpc0", "sc-inc-gpc1",
            "sc-set-gpt", "sc-set-gpt0",
            "send-spoe-group", "session",
            "set-bandwidth-limit", "set-bc-mark", "set-dst", "set-dst-port",
            "set-fc-mark",
            "set-header", "set-log-level", "set-map", "set-mark",
            "set-method", "set-nice", "set-path", "set-priority-class",
            "set-priority-offset", "set-query", "set-src", "set-src-port",
            "set-status", "set-timeout", "set-tos", "set-uri", "set-var",
            "silent-drop", "strict-mode",
            "tarpit",
            "track-sc", "track-sc0", "track-sc1", "track-sc2",
            "unset-var", "use-service",
            "wait-for-body", "wait-for-handshake"
        )

        // All keywords valid anywhere on a `stats` line (persistent line state).
        // Covers both the global form (stats socket / timeout / maxconn)
        // and the proxy form (stats enable / uri / realm / etc.)
        private val STATS_LINE_KEYWORDS = setOf(
            // sub-commands
            "admin", "auth", "bind-process", "enable", "hide-version",
            "http-request", "maxconn", "realm", "refresh", "scope",
            "show-desc", "show-legends", "show-node", "socket",
            "timeout", "uri",
            // stats socket parameters
            "expose-fd", "gid", "group", "level", "mode",
            "name", "process", "uid", "user",
            // values for `level`
            "operator",
            // value for `expose-fd`
            "listeners"
        )

        private val STICK_TYPES = setOf(
            "on", "match", "store-request", "store-response"
        )

        private val HTTP_CHECK_KEYWORDS = setOf(
            // sub-commands
            "connect", "send", "send-state", "expect", "disable-on-404",
            "comment",
            // connect params
            "default", "addr", "port", "proto", "ssl", "sni", "alpn",
            "linger", "via-socks4",
            // send params
            "meth", "uri", "ver", "hdr", "body",
            // expect params / match types
            "fhdr", "header",
            "min-recv", "ok-status", "error-status", "tout-status",
            "on-success", "on-error", "on-timeout",
            "status", "status-code", "rstatus", "rstring", "string", "binary"
        )

        private val TCP_CHECK_KEYWORDS = setOf(
            "connect", "send", "send-binary", "send-binary-lf", "send-lf",
            "expect", "comment", "abort", "set-var", "unset-var",
            "string", "binary", "rstring"
        )

        private val FILTER_TYPES = setOf(
            "trace", "compression", "spoe", "bwlim-in", "bwlim-out",
            "agent"
        )

        private val HOLD_TYPES = setOf(
            "other", "refused", "nx", "timeout", "valid", "obsolete"
        )

        private val STICK_TABLE_KEYWORDS = setOf(
            "expire", "nopurge", "peers", "process", "size", "srvkey",
            "store", "type", "write-to"
        )

        private val DECLARE_TYPES = setOf("capture")

        // Values for ssl-default-bind-options / ssl-default-server-options
        private val SSL_OPTIONS_KEYWORDS = setOf(
            "allow-0rtt",
            "force-sslv3", "force-tlsv10", "force-tlsv11",
            "force-tlsv12", "force-tlsv13",
            "no-sslv3", "no-tlsv10", "no-tlsv11", "no-tlsv12", "no-tlsv13",
            "no-tls-tickets",
            "prefer-client-ciphers",
            "ssl-max-ver", "ssl-min-ver"
        )

        // Sub-keywords for the `cookie` persistence directive
        private val COOKIE_KEYWORDS = setOf(
            "attr", "domain", "dynamic", "httponly", "indirect", "insert",
            "maxidle", "maxlife", "nocache", "postonly", "prefix", "preserve",
            "rewrite", "secure"
        )

        private val ACL_FETCH_METHODS = setOf(
            // base (host + path without query)
            "base", "base_beg", "base_dir", "base_dom", "base_end",
            "base_len", "base_reg", "base_sub",
            // header-based
            "hdr", "hdr_beg", "hdr_cnt", "hdr_dir", "hdr_dom", "hdr_end",
            "hdr_first", "hdr_ip", "hdr_len", "hdr_reg", "hdr_sub", "hdr_val",
            "req.fhdr", "req.fhdr_val",
            "req.hdr", "req.hdr_cnt", "req.hdr_ip", "req.hdr_len",
            "req.hdr_names", "req.hdr_val",
            "res.fhdr", "res.fhdr_val",
            "res.hdr", "res.hdr_cnt", "res.hdr_ip", "res.hdr_len",
            "res.hdr_names", "res.hdr_val",
            "shdr", "shdr_beg", "shdr_cnt", "shdr_dir", "shdr_dom", "shdr_end",
            "shdr_ip", "shdr_len", "shdr_reg", "shdr_sub", "shdr_val",
            // path / url
            "path", "path_beg", "path_dir", "path_dom", "path_end",
            "path_len", "path_reg", "path_sub",
            "url", "url_beg", "url_dir", "url_dom", "url_end", "url_ip",
            "url_len", "url_port", "url_reg", "url_sub",
            "urlp", "urlp_val",
            // network
            "src", "dst", "dst_conn", "src_port", "dst_port",
            "hostname",
            // cookies
            "cook", "cook_beg", "cook_cnt", "cook_dir", "cook_dom", "cook_end",
            "cook_len", "cook_reg", "cook_sub", "cook_val",
            "req.cook", "req.cook_cnt", "req.cook_val",
            "res.cook", "res.cook_cnt", "res.cook_val",
            "scook", "scook_beg", "scook_cnt", "scook_dir", "scook_dom",
            "scook_end", "scook_len", "scook_reg", "scook_sub", "scook_val",
            // request / response
            "method", "req_ver", "res_ver", "status",
            "req.body", "req.body_len", "req.body_size", "req.body_param",
            "req.len", "req.hdrs", "req.hdrs_bin",
            "res.body", "res.body_len", "res.body_size",
            "res.len", "res.hdrs", "res.hdrs_bin",
            // auth
            "http_auth", "http_auth_group", "http_first_req",
            // SSL / TLS
            "req.ssl_sni", "req.ssl_ver", "req.ssl_ec_ext",
            "req.ssl_hello_type", "req.ssl_alpn",
            "ssl_bc", "ssl_bc_alg_keysize", "ssl_bc_cipher",
            "ssl_bc_is_resumed", "ssl_bc_protocol", "ssl_bc_unique_id",
            "ssl_bc_use_keysize",
            "ssl_c_chain_der",
            "ssl_c_dn", "ssl_c_err", "ssl_c_i_dn", "ssl_c_key_alg",
            "ssl_c_notafter", "ssl_c_notbefore", "ssl_c_s_dn", "ssl_c_serial",
            "ssl_c_sha1", "ssl_c_sha256",
            "ssl_c_sig_alg", "ssl_c_used", "ssl_c_verify",
            "ssl_f_dn", "ssl_f_i_dn", "ssl_f_key_alg", "ssl_f_notafter",
            "ssl_f_notbefore", "ssl_f_s_dn", "ssl_f_serial", "ssl_f_sig_alg",
            "ssl_fc", "ssl_fc_alg_keysize", "ssl_fc_cipher",
            "ssl_fc_client_random", "ssl_fc_err", "ssl_fc_has_crt",
            "ssl_fc_has_early", "ssl_fc_has_sni", "ssl_fc_is_resumed",
            "ssl_fc_keysize", "ssl_fc_npn", "ssl_fc_protocol",
            "ssl_fc_server_random", "ssl_fc_session_id",
            "ssl_fc_sni", "ssl_fc_sni_beg", "ssl_fc_sni_end", "ssl_fc_sni_reg",
            "ssl_fc_unique_id", "ssl_fc_use_keysize",
            "ssl_s_dn",
            // backend / server metrics
            "avg_queue", "nbsrv",
            "be_conn", "be_conn_free", "be_id", "be_name",
            "be_http_err_rate", "be_http_req_rate",
            "be_sess_rate",
            "fe_conn", "fe_id", "fe_name",
            "fe_http_err_rate", "fe_http_req_rate", "fe_sess_rate",
            "queue",
            "srv_conn", "srv_id", "srv_is_up", "srv_name",
            "srv_pct_used", "srv_queue", "srv_sess_rate",
            "srv_time_since_last_change", "srv_weight",
            // health check fetches
            "check.body", "check.hdrs", "check.hdrs_bin", "check.status",
            // misc / converters
            "always_true", "always_false",
            "bin", "bool", "date", "env", "int", "ip", "rand", "str",
            "var",
            // process
            "nbproc", "nbthread", "proc",
            // txn
            "txn.conn_retries", "txn.rtt", "txn.status", "txn.uuid",
            // src-based stick-table counters (global table)
            "src_bytes_in_rate", "src_bytes_out_rate",
            "src_clr_gpc0", "src_clr_gpc1",
            "src_conn_cnt", "src_conn_cur", "src_conn_rate",
            "src_get_gpc0", "src_get_gpc1",
            "src_gpc0_rate", "src_gpc1_rate",
            "src_http_err_cnt", "src_http_err_rate",
            "src_http_req_cnt", "src_http_req_rate",
            "src_kbytes_in", "src_kbytes_out",
            "src_sess_cnt", "src_sess_rate",
            "src_updt_conn_cnt",
            // sc0 stick counter
            "sc0_bytes_in_rate", "sc0_bytes_out_rate",
            "sc0_clr_gpc0", "sc0_clr_gpc1",
            "sc0_conn_cnt", "sc0_conn_cur", "sc0_conn_rate",
            "sc0_get_gpc0", "sc0_get_gpc1",
            "sc0_gpc0_rate", "sc0_gpc1_rate",
            "sc0_http_err_cnt", "sc0_http_err_rate",
            "sc0_http_req_cnt", "sc0_http_req_rate",
            "sc0_kbytes_in", "sc0_kbytes_out",
            "sc0_sess_cnt", "sc0_sess_rate",
            "sc0_tracked", "sc0_trackers",
            // sc1 stick counter
            "sc1_bytes_in_rate", "sc1_bytes_out_rate",
            "sc1_clr_gpc0", "sc1_clr_gpc1",
            "sc1_conn_cnt", "sc1_conn_cur", "sc1_conn_rate",
            "sc1_get_gpc0", "sc1_get_gpc1",
            "sc1_gpc0_rate", "sc1_gpc1_rate",
            "sc1_http_err_cnt", "sc1_http_err_rate",
            "sc1_http_req_cnt", "sc1_http_req_rate",
            "sc1_kbytes_in", "sc1_kbytes_out",
            "sc1_sess_cnt", "sc1_sess_rate",
            "sc1_tracked", "sc1_trackers",
            // sc2 stick counter
            "sc2_bytes_in_rate", "sc2_bytes_out_rate",
            "sc2_clr_gpc0", "sc2_clr_gpc1",
            "sc2_conn_cnt", "sc2_conn_cur", "sc2_conn_rate",
            "sc2_get_gpc0", "sc2_get_gpc1",
            "sc2_gpc0_rate", "sc2_gpc1_rate",
            "sc2_http_err_cnt", "sc2_http_err_rate",
            "sc2_http_req_cnt", "sc2_http_req_rate",
            "sc2_kbytes_in", "sc2_kbytes_out",
            "sc2_sess_cnt", "sc2_sess_rate",
            "sc2_tracked", "sc2_trackers"
        )
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
            word in SECTION_KEYWORDS -> {
                state = STATE_AFTER_SECTION; HaproxyTokenTypes.SECTION_KEYWORD
            }
            word in DIRECTIVE_KEYWORDS -> {
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
            if (word in OPTION_KEYWORDS) HaproxyTokenTypes.OPTION_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_BALANCE -> {
            state = STATE_IN_LINE
            if (word in BALANCE_ALGORITHMS) HaproxyTokenTypes.PARAM_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_MODE -> {
            state = STATE_IN_LINE
            if (word in MODE_VALUES) HaproxyTokenTypes.PARAM_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_ACL -> {
            state = STATE_AFTER_ACL_NAME; HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_ACL_NAME -> {
            state = STATE_IN_LINE
            if (word in ACL_FETCH_METHODS) HaproxyTokenTypes.SUB_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_ACTION_DIRECTIVE -> {
            // When the action is `redirect`, enter redirect-line state so that
            // `code` and `location` later on the same line also get highlighted.
            state = if (word == "redirect") STATE_REDIRECT_LINE else STATE_IN_LINE
            if (word in ACTION_KEYWORDS) HaproxyTokenTypes.SUB_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_STATS -> {
            // Legacy non-persistent branch — no longer reached for "stats" directive
            // (nextStateForDirective now returns STATE_STATS_LINE). Kept for safety.
            state = STATE_IN_LINE
            if (word in STATS_LINE_KEYWORDS) HaproxyTokenTypes.SUB_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_STATS_LINE -> when {
            word in STATS_LINE_KEYWORDS -> HaproxyTokenTypes.SUB_KEYWORD
            isNumber(word)              -> HaproxyTokenTypes.NUMBER
            else                        -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_SSL_OPTIONS_LINE -> when {
            word in SSL_OPTIONS_KEYWORDS -> HaproxyTokenTypes.PARAM_KEYWORD
            else                         -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_COOKIE_LINE -> when {
            word in COOKIE_KEYWORDS -> HaproxyTokenTypes.SUB_KEYWORD
            isNumber(word)          -> HaproxyTokenTypes.NUMBER
            else                    -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_TIMEOUT -> {
            state = STATE_IN_LINE
            if (word in TIMEOUT_TYPES) HaproxyTokenTypes.SUB_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_STICK -> {
            state = STATE_IN_LINE
            if (word in STICK_TYPES) HaproxyTokenTypes.SUB_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_FILTER -> {
            state = STATE_IN_LINE
            if (word in FILTER_TYPES) HaproxyTokenTypes.SUB_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_HOLD -> {
            state = STATE_IN_LINE
            if (word in HOLD_TYPES) HaproxyTokenTypes.SUB_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_DECLARE -> {
            state = STATE_CAPTURE_LINE
            if (word in DECLARE_TYPES) HaproxyTokenTypes.SUB_KEYWORD
            else HaproxyTokenTypes.IDENTIFIER
        }

        // ── Persistent line states ─────────────────────────────────────────

        STATE_LOG_LINE -> when {
            word in LOG_KEYWORDS -> HaproxyTokenTypes.SUB_KEYWORD
            isNumber(word)       -> HaproxyTokenTypes.NUMBER
            else                 -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_REDIRECT_LINE -> when {
            word in INLINE_KEYWORDS   -> HaproxyTokenTypes.KEYWORD
            word in REDIRECT_KEYWORDS -> HaproxyTokenTypes.SUB_KEYWORD
            isNumber(word)            -> HaproxyTokenTypes.NUMBER
            else                      -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_CAPTURE_LINE -> when {
            word in CAPTURE_KEYWORDS -> HaproxyTokenTypes.SUB_KEYWORD
            isNumber(word)           -> HaproxyTokenTypes.NUMBER
            else                     -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_BIND -> {
            state = STATE_BIND_LINE; HaproxyTokenTypes.IDENTIFIER
        }

        STATE_BIND_LINE -> when {
            word in BIND_QUALIFIERS -> HaproxyTokenTypes.PARAM_KEYWORD
            isNumber(word)          -> HaproxyTokenTypes.NUMBER
            else                    -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_SERVER -> {
            state = STATE_AFTER_SERVER_NAME; HaproxyTokenTypes.IDENTIFIER
        }

        STATE_AFTER_SERVER_NAME -> {
            state = STATE_SERVER_LINE; HaproxyTokenTypes.IDENTIFIER
        }

        STATE_SERVER_LINE -> when {
            word in SERVER_QUALIFIERS -> HaproxyTokenTypes.PARAM_KEYWORD
            isNumber(word)            -> HaproxyTokenTypes.NUMBER
            else                      -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_HTTP_CHECK_LINE -> when {
            word in HTTP_CHECK_KEYWORDS -> HaproxyTokenTypes.SUB_KEYWORD
            isNumber(word)              -> HaproxyTokenTypes.NUMBER
            else                        -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_TCP_CHECK_LINE -> when {
            word in TCP_CHECK_KEYWORDS -> HaproxyTokenTypes.SUB_KEYWORD
            isNumber(word)             -> HaproxyTokenTypes.NUMBER
            else                       -> HaproxyTokenTypes.IDENTIFIER
        }

        STATE_STICK_TABLE_LINE -> when {
            word in STICK_TABLE_KEYWORDS -> HaproxyTokenTypes.SUB_KEYWORD
            isNumber(word)               -> HaproxyTokenTypes.NUMBER
            else                         -> HaproxyTokenTypes.IDENTIFIER
        }

        else /* STATE_IN_LINE */ -> when {
            word in INLINE_KEYWORDS    -> HaproxyTokenTypes.KEYWORD
            word in BALANCE_ALGORITHMS -> HaproxyTokenTypes.PARAM_KEYWORD
            word in MODE_VALUES        -> HaproxyTokenTypes.PARAM_KEYWORD
            isNumber(word)             -> HaproxyTokenTypes.NUMBER
            else                       -> HaproxyTokenTypes.IDENTIFIER
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
