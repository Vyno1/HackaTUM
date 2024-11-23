package org.jetbrains.plugins.template

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.psi.*
import java.io.File
import java.io.IOException

import com.intellij.ui.components.JBLoadingPanel
import com.intellij.util.ui.JBUI

class CheckBoxAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val result = getImportData(event)
        if (result.isEmpty()) {
            displayError()
            return
        }

        val dialog = MultiCheckboxDialog()
        if (!dialog.showAndGet()) {
            // Cancel button was clicked
            return
        }

        val selectedOptions = dialog.getSelectedOptions()
        var selectedOptionsStringArr: MutableList<String> = selectedOptions.map { "--${it.name}" }.toMutableList()

        val additionalPrompt = dialog.getAdditionalOptions()
        if (additionalPrompt.isNotEmpty()) {
            selectedOptionsStringArr.add("--${Options.ADDITIONAL_PROMPTS.name}=\"${additionalPrompt}\"")
        }

        val functionFilePath = result["function_path"]
        if (functionFilePath == null || !File(functionFilePath).exists()) {
            displayError("Absolute path to function file not resolvable.")
            return
        }

        for ((key, value) in result) {
            selectedOptionsStringArr.add("--${key}=\"${value}\"")
        }

        val pyfilepath = System.getenv("PY_FILE_PATH")
        val project = event.project ?: return
        var retStr = ""
        ProgressManager.getInstance().runProcessWithProgressSynchronously(
            {
                retStr = PythonHandler.call(pyfilepath, selectedOptionsStringArr)
            },
            "Generating Tests...",
            true,
            project)
        //val retStr = PythonHandler.call(pyfilepath, selectedOptionsStringArr)
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


    private fun getImportData(event: AnActionEvent): MutableMap<String, String> {
        val results = mutableMapOf<String, String>()

        val psiFile: PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return mutableMapOf()
        val editor: Editor = event.getData(CommonDataKeys.EDITOR) ?: return mutableMapOf()
        val elementAtCaret: PsiElement = psiFile.findElementAt(editor.caretModel.offset) ?: return mutableMapOf()

        val containingFunction: PyFunction =
            PsiTreeUtil.getParentOfType(elementAtCaret, PyFunction::class.java) ?: return mutableMapOf()

        results["function_path"] = createFunctionFile(containingFunction.text)

        // Find the class containing the function (if any)
        val containingClass: PyClass? = PsiTreeUtil.getParentOfType(containingFunction, PyClass::class.java)
        if (containingClass != null) {
            results["module"] = containingClass.qualifiedName ?: "UnnamedClass"
        }

        results["file_path"] = psiFile.virtualFile.path
        return results
    }

    private fun displayError(msg: String = "No function selected") {
        Messages.showMessageDialog(msg, "Error", Messages.getErrorIcon())
    }

    private fun createFunctionFile(function: String): String {
        // Specify the directory where you want to create the file
        val directory = File(System.getProperty("user.dir"), "generated_functions")
        // Create the directory if it does not exist
        if (!directory.exists()) {
            try {
                directory.mkdirs() // Create directories if they don't exist
            } catch (e: IOException) {
                e.printStackTrace()
                return "Failed to create directory"
            }
        }

        // Create the file name, using a timestamp or a unique identifier
        val fileName = "function_${System.currentTimeMillis()}.txt"
        val file = File(directory, fileName)

        try {
            // Write the function string to the file
            file.writeText(function)
        } catch (e: IOException) {
            e.printStackTrace()
            return "Failed to write to file"
        }

        // Return the file path as a string
        return file.absolutePath
    }
}


