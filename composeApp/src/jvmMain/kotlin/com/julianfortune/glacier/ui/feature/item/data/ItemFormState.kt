package com.julianfortune.glacier.ui.page.item.data

import com.julianfortune.glacier.ui.common.data.FormFieldState

sealed interface PackagingType {
    data object Loose : PackagingType
    data class Discrete(val variants: List<String>) : PackagingType
}

data class ItemFormState(
    val name: FormFieldState<String> = FormFieldState(""),
    val categoryId: FormFieldState<Long?> = FormFieldState(null),
    val packaging: PackagingType = PackagingType.Loose,
    val isValid: Boolean = false,
)
