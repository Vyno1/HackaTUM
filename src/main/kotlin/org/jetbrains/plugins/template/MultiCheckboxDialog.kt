package org.jetbrains.plugins.template

import com.intellij.openapi.ui.DialogWrapper
import java.awt.Dimension
import javax.swing.*


class MultiCheckboxDialog : DialogWrapper(true) {

    private val checkboxes = mutableMapOf<Options, JCheckBox>()
    private lateinit var panel: JPanel
    private lateinit var additionalOptionsJTextField: JTextField

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

            checkbox.alignmentX = JPanel.LEFT_ALIGNMENT
            panel.add(checkbox)
        }

        addAdditionalPromptsTextField()

        return panel
    }

    private fun addAdditionalPromptsTextField() {
        additionalOptionsJTextField.isVisible = false

        val additionalPromptsCheckbox = checkboxes[Options.ADDITIONAL_PROMPTS] ?: return
        additionalOptionsJTextField.alignmentX = JPanel.LEFT_ALIGNMENT
        additionalOptionsJTextField.preferredSize =
            Dimension(panel.width, additionalOptionsJTextField.preferredSize.height)

        additionalPromptsCheckbox.addChangeListener {
            val isSelected = additionalPromptsCheckbox.isSelected
            additionalOptionsJTextField.isVisible = isSelected

            SwingUtilities.invokeLater {
                panel.revalidate()
                panel.repaint()
            }
        }

        panel.add(additionalOptionsJTextField)
    }


    fun getSelectedOptions() = checkboxes.filter { it.value.isSelected }.keys.toList()

    fun getAdditionalOptions() = additionalOptionsJTextField.text
}
