package com.timhinz.haproxy

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object HaproxyFileType : LanguageFileType(HaproxyLanguage) {
    override fun getName(): String = "HAProxy Configuration"
    override fun getDescription(): String = "HAProxy configuration file"
    override fun getDefaultExtension(): String = "haproxy"
    override fun getIcon(): Icon? = null
}
