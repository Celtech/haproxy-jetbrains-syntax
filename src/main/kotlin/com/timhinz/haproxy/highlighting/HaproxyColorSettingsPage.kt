package com.timhinz.haproxy.highlighting

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class HaproxyColorSettingsPage : ColorSettingsPage {

    override fun getDisplayName(): String = "HAProxy"
    override fun getIcon(): Icon? = null
    override fun getHighlighter(): SyntaxHighlighter = HaproxySyntaxHighlighter()
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = arrayOf(
        AttributesDescriptor("Comment",                    HaproxyHighlightingColors.COMMENT),
        AttributesDescriptor("Section//Section keyword",   HaproxyHighlightingColors.SECTION_KEYWORD),
        AttributesDescriptor("Section//Section name",      HaproxyHighlightingColors.SECTION_NAME),
        AttributesDescriptor("Directive keyword",          HaproxyHighlightingColors.KEYWORD),
        AttributesDescriptor("Sub-command keyword",        HaproxyHighlightingColors.SUB_KEYWORD),
        AttributesDescriptor("Values//Option value",       HaproxyHighlightingColors.OPTION_KEYWORD),
        AttributesDescriptor("Values//Parameter keyword",  HaproxyHighlightingColors.PARAM_KEYWORD),
        AttributesDescriptor("Number",                     HaproxyHighlightingColors.NUMBER),
        AttributesDescriptor("String",                     HaproxyHighlightingColors.STRING),
        AttributesDescriptor("Identifier",                 HaproxyHighlightingColors.IDENTIFIER),
        AttributesDescriptor("User-defined name reference", HaproxyHighlightingColors.USER_DEFINED_REF),
        AttributesDescriptor("Bad character",              HaproxyHighlightingColors.BAD_CHARACTER),
    )

    override fun getDemoText(): String = """
# HAProxy load balancer configuration
global
    log 127.0.0.1 local0 notice
    maxconn 4096
    daemon
    pidfile /var/run/haproxy.pid

defaults
    mode http
    timeout connect 5s
    timeout client 30s
    timeout server 30s
    option httplog
    option dontlognull
    option forwardfor
    retries 3

frontend http-in
    bind *:80
    bind *:443 ssl crt /etc/ssl/haproxy.pem
    default_backend web-servers
    acl is_api path_beg /api
    acl is_static path_beg /static
    acl host_beta hdr_end(host) -i .beta.example.com
    use_backend api-servers if is_api
    use_backend static-servers if is_static
    http-request set-header X-Forwarded-Host %[req.hdr(Host)]
    http-request deny if { src 10.0.0.0/8 } unless is_api
    http-response set-header X-Frame-Options SAMEORIGIN

backend web-servers
    balance roundrobin
    server web1 192.168.1.10:8080 check weight 1
    server web2 192.168.1.11:8080 check weight 2

backend api-servers
    balance leastconn
    option http-server-close
    server api1 10.0.0.1:3000 check
    server api2 10.0.0.2:3000 check backup

listen stats
    bind *:8404
    stats enable
    stats uri /stats
    stats refresh 30s
    """.trimIndent()
}
