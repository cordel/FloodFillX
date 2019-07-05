package me.suzdalnitsky.floodfillx

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_settings.*
import me.suzdalnitsky.floodfillx.persistance.SettingsStore

class SettingsFragment : DialogFragment() {

    private lateinit var settingsStore: SettingsStore
    private val listener
        get() = activity as Listener

    private var settingsUpdated = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        settingsStore = SettingsStore(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : AppCompatDialog(context, R.style.FullScreenDialogStyle) {

            init {
                supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
            }

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                window?.apply {
                    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }
            }
        }
        val contentView = View.inflate(requireContext(), R.layout.fragment_settings, null)
        return dialog.apply { setContentView(contentView) }
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        with(dialog) {
            widthEditText.setText(settingsStore.userSettings.width.toString())
            heightEditText.setText(settingsStore.userSettings.height.toString())
            fab.setOnClickListener { onConfirmClick() }
        }
    }

    override fun onDetach() {
        super.onDetach()

        if (settingsUpdated) {
            listener.onSettingsUpdated()
        } else {
            listener.onSettingsNotUpdated()
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