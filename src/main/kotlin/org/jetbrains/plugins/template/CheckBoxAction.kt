package org.jetbrains.plugins.template

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

class CheckBoxAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val selectedText = getText(event)
        if (selectedText.isNullOrEmpty()) {
            // Show a message popup if no text is selected
            Messages.showMessageDialog(
                "No text selected!",
                "Selection Error",
                Messages.getWarningIcon()
            )
            return
        }

        val dialog = MultiCheckboxDialog()
        if (!dialog.showAndGet()) {
            // Cancel button was clicked
            return
        }
        val selectedOptions = dialog.getSelectedOptions()
        val selectedOptionsStringArr = selectedOptions.map { "--${it.name}" }

        val additionalPrompt = dialog.getAdditionalOptions()
        if (additionalPrompt.isNotEmpty()) {
            selectedOptionsStringArr.plus("--${Options.ADDITIONAL_PROMPTS.name} \"${additionalPrompt}\"")
        }

        // TODO: Add helper function that infers source file path, class and function
        // Then, add to Args string list
        // If file is module (has no class), don't add --class

        val pyfilepath = "/Users/maxi/code/repos/TestBrains/src/main/resources/python/generate_tests.py"
        val retStr = PythonHandler.call(pyfilepath, selectedOptionsStringArr)
        val (message, icon) =
            if (retStr.isEmpty()) "Success!" to Messages.getInformationIcon()
            else "Error: $retStr" to Messages.getErrorIcon()
        Messages.showMessageDialog(
            message,
            "Test Generation",
            icon
        )
        return
    }


    private fun getText(event: AnActionEvent): String? {
        val editor: Editor? = event.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR)
        val project: Project? = event.getData(com.intellij.openapi.actionSystem.CommonDataKeys.PROJECT)

        // Check if editor and selection exist
        if (editor != null && project != null) {
            // Get the selection model from the editor
            val selectionModel: SelectionModel = editor.selectionModel

            // If there is any selected text, get it
            if (selectionModel.hasSelection()) {
                val selectedText = selectionModel.selectedText
                return selectedText
            }
        }
        return ""
    }

}
