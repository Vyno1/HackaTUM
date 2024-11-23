package org.jetbrains.plugins.template

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.psi.*
import com.jetbrains.python.pyi.PyiFile

class CheckBoxAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val selectedText = getText(event)
        if (selectedText.isNullOrEmpty()) {
            Messages.showMessageDialog("No function selected", "Error", Messages.getErrorIcon())
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


    private fun getImportData(event: AnActionEvent) {
        val results = mutableMapOf<String, String>()

        val psiFile: PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return
        val editor: Editor = event.getData(CommonDataKeys.EDITOR) ?: return

        //TODO fix this. Want cursor location
        val elementAtCaret: PsiElement = event.getData(CommonDataKeys.PSI_ELEMENT) ?: return

        // val caretPos = editor.selectionModel.selectionStart
        // val elementAtCaret: PsiElement = psiFile.findElementAt(caretPos) ?: return


        results["path"] = psiFile.virtualFile.path

        if (psiFile is PyFile) {
            val containingFunction = PsiTreeUtil.getParentOfType(elementAtCaret, PyFunction::class.java)
            if (containingFunction != null) {
                println("============")
                print(containingFunction)
                println("============")
                results["function"] = containingFunction.name ?: "UnnamedFunction"

                // Find the class containing the function (if any)
                val containingClass = PsiTreeUtil.getParentOfType(containingFunction, PyClass::class.java)
                if (containingClass != null) {
                    results["class"] = containingClass.qualifiedName ?: containingClass.name ?: "UnnamedClass"
                }
            } else {
                // If no function found, fallback to module-level
                results["function"] = "ModuleLevel"
                results["class"] = psiFile.name
            }

            print(results)
            println()
        } else {
            println("Not a Python file.")
        }
    }


    private fun getText(event: AnActionEvent): String? {
        val elementAtCaret: PsiElement = event.getData(CommonDataKeys.PSI_ELEMENT) ?: return ""
        println(elementAtCaret.text)
        return elementAtCaret.text
    }

}


