package me.suzdalnitsky.floodfillx.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_settings.*
import me.suzdalnitsky.floodfillx.R
import me.suzdalnitsky.floodfillx.ValidationResult
import me.suzdalnitsky.floodfillx.persistance.SettingsStore
import me.suzdalnitsky.floodfillx.ui.common.FullscreenDialog

class SettingsFragment : DialogFragment() {

    private lateinit var settingsStore: SettingsStore
    private val listener: Listener?
        get() = activity as? Listener

    private var settingsUpdated = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        settingsStore = SettingsStore(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = FullscreenDialog(requireContext())
        val contentView = View.inflate(requireContext(), R.layout.fragment_settings, null)
        
        return dialog.apply { setContentView(contentView) }
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        with(dialog) {
            widthEditText.setText(settingsStore.userSettings.width.toString())
            heightEditText.setText(settingsStore.userSettings.height.toString())
            fab.setOnClickListener { onConfirmClick() }
            back.setOnClickListener { this@SettingsFragment.dismiss() }
        }
    }

    override fun onDetach() {
        super.onDetach()

        listener?.run {
            if (settingsUpdated) onSettingsUpdated() else onSettingsNotUpdated()
        }
    }

    private fun onConfirmClick() {
        dialog?.let { dialog ->
            val width = dialog.widthEditText.text.toString().toIntOrNull()
            val height = dialog.heightEditText.text.toString().toIntOrNull()

            val widthValidationResult = validateInput(width)
            val heightValidationResult = validateInput(height)

            if (widthValidationResult == ValidationResult.OK && heightValidationResult == ValidationResult.OK) {
                settingsStore.updateSettings { copy(width = width!!) }
                settingsStore.updateSettings { copy(height = height!!) }
                settingsUpdated = true

                return this@SettingsFragment.dismiss()
            }

            dialog.widthPicker.error = widthValidationResult.let { it as? ValidationResult.Error }
                ?.errorText?.let(::getString)

            dialog.heightPicker.error = heightValidationResult.let { it as? ValidationResult.Error }
                ?.errorText?.let(::getString)
        }
    }

    private fun validateInput(input: Int?) = when {
        input == null -> ValidationResult.Error(R.string.error_invalid_value)
        input < 1 -> ValidationResult.Error(R.string.error_small_value)
        else -> ValidationResult.OK
    }

    interface Listener {
        fun onSettingsUpdated()
        fun onSettingsNotUpdated()
    }
}