package org.jetbrains.plugins.template

import com.intellij.openapi.ui.DialogWrapper
import javax.swing.*


class MultiCheckboxDialog : DialogWrapper(true) {

    private val checkboxes = mutableMapOf<Options, JCheckBox>()
    private lateinit var panel: JPanel

    init {
        title = "Testing Categories"
        init()
    }

    override fun createCenterPanel(): JComponent {
        panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        Options.entries.forEach { option ->
            val checkbox = JCheckBox(option.displayName)
            checkboxes[option] = checkbox
            panel.add(checkbox)
        }

        return panel
    }

    fun getSelectedOptions(): List<Options> {
        return checkboxes.filter { it.value.isSelected }.keys.toList()
    }
}

