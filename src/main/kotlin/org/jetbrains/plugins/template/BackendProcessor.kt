package org.jetbrains.plugins.template

import org.jetbrains.plugins.template.TestClass

object BackendProcessor {
    fun processSelectedOptions(selectedOptions: List<Options>) {
        val tc = TestClass()
        tc.main(selectedOptions)
    }
}
