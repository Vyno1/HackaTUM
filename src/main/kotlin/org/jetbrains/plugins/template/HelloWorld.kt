package org.jetbrains.plugins.template

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class HelloWorld : AnAction() {
    override fun actionPerformed(p0: AnActionEvent) {
        Messages.showMessageDialog("Hello world!", "TestBrains", Messages.getInformationIcon())
    }
}