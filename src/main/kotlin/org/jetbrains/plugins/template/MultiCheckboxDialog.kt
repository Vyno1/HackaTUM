package org.jetbrains.plugins.template

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.JBColor
import java.awt.Dimension
import javax.swing.*
import javax.swing.border.LineBorder

class MultiCheckboxDialog : DialogWrapper(true) {

    private val checkboxes = mutableMapOf<Options, JCheckBox>()
    private lateinit var panel: JPanel
    private lateinit var additionalOptionsJTextArea: JTextArea

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
        additionalOptionsJTextArea = JTextArea()
        additionalOptionsJTextArea.isVisible = false
        additionalOptionsJTextArea.lineWrap = true
        additionalOptionsJTextArea.wrapStyleWord = true
        additionalOptionsJTextArea.border = LineBorder(JBColor.GRAY, 1)
        additionalOptionsJTextArea.alignmentX = JPanel.LEFT_ALIGNMENT
        additionalOptionsJTextArea.preferredSize =
            Dimension(panel.width, additionalOptionsJTextArea.preferredSize.height)

        val additionalPromptsCheckbox = checkboxes[Options.ADDITIONAL_PROMPTS] ?: return

        additionalPromptsCheckbox.addChangeListener {
            val isSelected = additionalPromptsCheckbox.isSelected
            additionalOptionsJTextArea.isVisible = isSelected

            SwingUtilities.invokeLater {
                panel.revalidate()
                panel.repaint()
            }
        }

        panel.add(additionalOptionsJTextArea)
    }


    fun getSelectedOptions() =
        checkboxes.filter { it.value.isSelected && (it.key != Options.ADDITIONAL_PROMPTS) }.keys.toList()

    fun getAdditionalOptions(): String = additionalOptionsJTextArea.text
}
