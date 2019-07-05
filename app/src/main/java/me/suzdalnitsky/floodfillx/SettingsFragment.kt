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
            fab.setOnClickListener {
                settingsStore.updateSettings { copy(width = widthEditText.text.toString().toInt()) }
                settingsStore.updateSettings { copy(height = heightEditText.text.toString().toInt()) }
                listener.onSettingsUpdated()
                dismiss()
            }
        }
    }

    interface Listener {
        fun onSettingsUpdated()
    }

//    private fun onHeightChanged(): TextWatcher {
//        return object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                val intValue = try {
//                    s.toString().toInt()
//                } catch (e: NumberFormatException) {
//                    heightPicker.error = getString(R.string.error_invalid_value)
//                    return
//                }
//
//                when {
//                    intValue == 0 -> {
//                        heightPicker.error = getString(R.string.error_small_value)
//                    }
//                    intValue == settingsStore.userSettings.height -> {
//                        heightPicker.error = null
//                    }
//                    intValue > 512 -> {
//                        heightPicker.error = getString(R.string.error_big_value)
//                    }
//                    else -> {
//                        settingsStore.updateSettings { copy(height = intValue) }
//                        refresh(settingsStore.userSettings.width, settingsStore.userSettings.height)
//                        heightPicker.error = null
//                    }
//                }
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            }
//        }
//    }

}