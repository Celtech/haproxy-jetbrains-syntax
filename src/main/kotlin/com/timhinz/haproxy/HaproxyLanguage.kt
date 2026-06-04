package com.timhinz.haproxy

import com.intellij.lang.Language

object HaproxyLanguage : Language("HAProxy") {
    private fun readResolve(): Any = HaproxyLanguage
}
