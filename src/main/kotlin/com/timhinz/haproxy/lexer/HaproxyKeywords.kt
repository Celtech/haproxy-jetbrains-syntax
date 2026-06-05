package com.timhinz.haproxy.lexer

object HaproxyKeywords {

    val SECTION_KEYWORDS = setOf(
        "global", "defaults", "frontend", "backend", "listen",
        "resolvers", "userlist", "peers", "mailers", "ring",
        "http-errors", "program",
        "cache",
        "fcgi-app"
    )

    val DIRECTIVE_KEYWORDS = setOf(
        "acl", "backlog", "balance", "bind", "bind-process",
        "block",
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
        "rspidel", "rspideny",
        "rspirep", "rspisetbe", "rsprep",
        "server", "server-state-file-name", "server-template",
        "stats", "stick", "stick-table",
        "tcp-check", "tcp-request", "tcp-response", "timeout",
        "transparent", "unique-id-format", "unique-id-header",
        "use-fcgi-app", "use-server", "use_backend",
        "nameserver", "resolve_retries", "hold", "accepted_payload_size",
        "parse-resolv-conf", "resolve-prefer",
        "group", "user",
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
        "tune", "uid", "ulimit-n", "unix-bind", "unsetenv",
        "load-server-state-from-file",
        "zero-warning",
        "peer", "table",
        "mailer",
        "total-max-size", "max-object-size", "max-age", "process-vary",
        "docroot", "index", "log-stderr", "pass-header", "set-param"
    )

    val INLINE_KEYWORDS = setOf(
        "if", "unless", "else", "or", "and", "not",
        "eq", "ne", "lt", "le", "gt", "ge"
    )

    val OPTION_KEYWORDS = setOf(
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

    val BALANCE_ALGORITHMS = setOf(
        "roundrobin", "static-rr", "leastconn", "first", "source",
        "uri", "url_param", "hdr", "rdp-cookie", "random",
        "hash"
    )

    val MODE_VALUES = setOf("http", "tcp", "health")

    val TIMEOUT_TYPES = setOf(
        "check",
        "connect", "client", "client-fin", "client-hs",
        "http-keep-alive", "http-request",
        "queue",
        "resolve", "retry",
        "server", "server-fin",
        "tarpit", "tunnel"
    )

    val LOG_KEYWORDS = setOf(
        "global", "stdout", "stderr",
        "local0", "local1", "local2", "local3", "local4",
        "local5", "local6", "local7",
        "kern", "user", "mail", "daemon", "auth", "syslog",
        "lpr", "news", "uucp", "cron", "authpriv", "ftp",
        "format", "raw", "rfc3164", "rfc5424", "short", "timed", "priority",
        "len", "sample",
        "emerg", "alert", "crit", "err", "warning", "notice", "info", "debug"
    )

    val REDIRECT_KEYWORDS = setOf(
        "location", "prefix", "scheme", "code",
        "drop-query", "append-slash", "set-cookie", "clear-cookie"
    )

    val CAPTURE_KEYWORDS = setOf(
        "request", "response", "cookie", "header", "len"
    )

    val BIND_QUALIFIERS = setOf(
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
        "prefer-server-ciphers", "process", "proto",
        "quic-force-retry",
        "severity-output", "ssl", "ssl-max-ver", "ssl-min-ver",
        "strict-sni",
        "tcp-ut", "tfo", "thread", "tls-ticket-keys", "transparent",
        "v4v6", "v6only", "verify"
    )

    val SERVER_QUALIFIERS = setOf(
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

    val ACTION_KEYWORDS = setOf(
        "accept", "add-acl", "add-header", "allow",
        "auth",
        "cache-store",
        "cache-use",
        "capture", "connection", "content",
        "del-acl", "del-header", "del-map", "deny",
        "disable-l7-retry", "do-resolve",
        "early-hint",
        "inspect-delay",
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

    val STATS_LINE_KEYWORDS = setOf(
        "admin", "auth", "bind-process", "enable", "hide-version",
        "http-request", "maxconn", "realm", "refresh", "scope",
        "show-desc", "show-legends", "show-node", "socket",
        "timeout", "uri",
        "expose-fd", "gid", "group", "level", "mode",
        "name", "process", "uid", "user",
        "operator",
        "listeners"
    )

    val STICK_TYPES = setOf(
        "on", "match", "store-request", "store-response"
    )

    val HTTP_CHECK_KEYWORDS = setOf(
        "connect", "send", "send-state", "expect", "disable-on-404",
        "comment",
        "default", "addr", "port", "proto", "ssl", "sni", "alpn",
        "linger", "via-socks4",
        "meth", "uri", "ver", "hdr", "body",
        "fhdr", "header",
        "min-recv", "ok-status", "error-status", "tout-status",
        "on-success", "on-error", "on-timeout",
        "status", "status-code", "rstatus", "rstring", "string", "binary"
    )

    val TCP_CHECK_KEYWORDS = setOf(
        "connect", "send", "send-binary", "send-binary-lf", "send-lf",
        "expect", "comment", "abort", "set-var", "unset-var",
        "string", "binary", "rstring"
    )

    val FILTER_TYPES = setOf(
        "trace", "compression", "spoe", "bwlim-in", "bwlim-out",
        "agent"
    )

    val HOLD_TYPES = setOf(
        "other", "refused", "nx", "timeout", "valid", "obsolete"
    )

    val STICK_TABLE_KEYWORDS = setOf(
        "expire", "nopurge", "peers", "process", "size", "srvkey",
        "store", "type", "write-to"
    )

    val DECLARE_TYPES = setOf("capture")

    val SSL_OPTIONS_KEYWORDS = setOf(
        "allow-0rtt",
        "force-sslv3", "force-tlsv10", "force-tlsv11",
        "force-tlsv12", "force-tlsv13",
        "no-sslv3", "no-tlsv10", "no-tlsv11", "no-tlsv12", "no-tlsv13",
        "no-tls-tickets",
        "prefer-client-ciphers",
        "ssl-max-ver", "ssl-min-ver"
    )

    val COOKIE_KEYWORDS = setOf(
        "attr", "domain", "dynamic", "httponly", "indirect", "insert",
        "maxidle", "maxlife", "nocache", "postonly", "prefix", "preserve",
        "rewrite", "secure"
    )

    val ACL_FETCH_METHODS = setOf(
        "base", "base_beg", "base_dir", "base_dom", "base_end",
        "base_len", "base_reg", "base_sub",
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
        "path", "path_beg", "path_dir", "path_dom", "path_end",
        "path_len", "path_reg", "path_sub",
        "url", "url_beg", "url_dir", "url_dom", "url_end", "url_ip",
        "url_len", "url_port", "url_reg", "url_sub",
        "urlp", "urlp_val",
        "src", "dst", "dst_conn", "src_port", "dst_port",
        "hostname",
        "cook", "cook_beg", "cook_cnt", "cook_dir", "cook_dom", "cook_end",
        "cook_len", "cook_reg", "cook_sub", "cook_val",
        "req.cook", "req.cook_cnt", "req.cook_val",
        "res.cook", "res.cook_cnt", "res.cook_val",
        "scook", "scook_beg", "scook_cnt", "scook_dir", "scook_dom",
        "scook_end", "scook_len", "scook_reg", "scook_sub", "scook_val",
        "method", "req_ver", "res_ver", "status",
        "req.body", "req.body_len", "req.body_size", "req.body_param",
        "req.len", "req.hdrs", "req.hdrs_bin",
        "res.body", "res.body_len", "res.body_size",
        "res.len", "res.hdrs", "res.hdrs_bin",
        "http_auth", "http_auth_group", "http_first_req",
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
        "check.body", "check.hdrs", "check.hdrs_bin", "check.status",
        "always_true", "always_false",
        "bin", "bool", "date", "env", "int", "ip", "rand", "str",
        "var",
        "nbproc", "nbthread", "proc",
        "txn.conn_retries", "txn.rtt", "txn.status", "txn.uuid",
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
