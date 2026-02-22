package com.julianfortune.glacier.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.util.formatCents

@JvmInline
value class CurrencyInput private constructor(val value: String) {

    companion object {
        val DECIMAL_REGEX = Regex("^(\\d+)?(\\.)?(\\d)?(\\d)?$")

        fun fromLong(cents: Long): CurrencyInput {
            return CurrencyInput(formatCents(cents))
        }

        fun fromString(value: String): CurrencyInput? {

            return when {
                value == "" -> CurrencyInput("")
                DECIMAL_REGEX.matches(value) -> CurrencyInput(value)
                else -> null
            }
        }
    }

    private fun numberInputToLong(number: String?): Long {
        return when (number) {
            null, "" -> 0L
            else -> number.toLong()
        }
    }

    fun toLong() = when (value) {
        "" -> null
        "." -> 0L
        else -> {
            // We know the value matches `decimalRegex` otherwise it could not be `CurrencyInput`
            val parts = value.split('.')

            val dollars = numberInputToLong(parts.getOrNull(0))
            val cents = numberInputToLong(parts.getOrNull(1))

            dollars * 100 + cents
        }
    }

    fun toSimplifiedForm() = toLong()?.let { fromLong(it) } // Strip any leading zeroes
}

@Composable
fun CurrencyInputTextField(
    value: CurrencyInput?,
    onValueChange: (CurrencyInput) -> Unit,
    label: @Composable (() -> Unit)? = { Text("Cost") },
    enabled: Boolean = true,
    isError: Boolean = false,
    onFocusLost: () -> Unit = {}
) {
    OutlinedTextField(
        value = value?.value ?: "",
        placeholder = { Text("0.00") },
        onValueChange = { newValue ->
            CurrencyInput.fromString(newValue)?.let { onValueChange(it) }
        },
        label = label,
        modifier = Modifier
            .height(64.dp)
            .onFocusChanged({ state ->
                if (!state.isFocused) {
                    // Check for error / format / etc.
                    onFocusLost()
                }
            }),
        // For i18n probably want to achieve this via `visualTransformation` property instead
        // (see https://developer.android.com/develop/ui/compose/quick-guides/content/auto-format-phone-number)
        prefix = {
            Icon(
                imageVector = Icons.Filled.AttachMoney,
                contentDescription = "Dollar sign",
                modifier = Modifier.height(20.dp)
            )
        },
        singleLine = true,
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(),
        enabled = enabled,
    )
}