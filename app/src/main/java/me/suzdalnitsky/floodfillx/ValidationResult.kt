package me.suzdalnitsky.floodfillx

import androidx.annotation.StringRes

sealed class ValidationResult {

    object OK : ValidationResult()

    data class Error(
        @StringRes val errorText: Int
    ) : ValidationResult()
}