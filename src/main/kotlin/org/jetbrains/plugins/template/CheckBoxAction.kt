package org.jetbrains.plugins.template

import org.jetbrains.plugins.template.MultiCheckboxDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class CheckBoxAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val dialog = MultiCheckboxDialog()
        if (dialog.showAndGet()) {
            // OK button was clicked
            val selectedOptions = dialog.getSelectedOptions()
            BackendProcessor.processSelectedOptions(selectedOptions)
        } else {
            // Cancel button was clicked

        }
    }
}
