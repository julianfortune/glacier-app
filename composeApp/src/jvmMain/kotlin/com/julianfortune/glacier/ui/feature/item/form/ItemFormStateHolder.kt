package com.julianfortune.glacier.ui.page.item.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.julianfortune.glacier.data.domain.Weight
import com.julianfortune.glacier.ui.common.data.FormFieldState
import com.julianfortune.glacier.ui.page.item.data.ItemBody
import com.julianfortune.glacier.ui.page.item.data.ItemFormState
import com.julianfortune.glacier.ui.page.item.data.PackagingType


sealed interface PackagingInput {
    data object Loose : PackagingInput
    data class Discrete(val variants: Set<Weight>) : PackagingInput
}

class ItemFormStateHolder(initialValue: ItemBody? = null) {

    private var nameInput by mutableStateOf(initialValue?.name ?: "")
    private var categoryIdInput by mutableStateOf(initialValue?.categoryId)
    private var packagingInput by mutableStateOf(
        initialValue?.savedWeights?.let { PackagingInput.Discrete(it) } ?: PackagingInput.Loose
    )

    val validData: ItemBody? by derivedStateOf {
        val currentName = nameInput
        val currentCategoryId = categoryIdInput

        val currentPackagingInput = packagingInput
        val packagingIsValid = when (currentPackagingInput) {
            is PackagingInput.Loose -> true
            is PackagingInput.Discrete -> currentPackagingInput.variants.isNotEmpty()
        }

        if (currentName != "" && packagingIsValid) {
            val savedWeights = when (currentPackagingInput) {
                is PackagingInput.Loose -> null
                is PackagingInput.Discrete -> currentPackagingInput.variants
            }

            ItemBody(
                currentName,
                currentCategoryId,
                savedWeights,
            )
        } else {
            null
        }
    }

    val uiState: ItemFormState by derivedStateOf {
        val savedWeights = when (val currentPackagingInput = packagingInput) {
            is PackagingInput.Loose -> PackagingType.Loose
            is PackagingInput.Discrete -> {
                val variants = currentPackagingInput.variants
                    .sortedBy { it.centigrams }
                    .map {
                        val (lbs, oz) = it.toImperial()
                        val lbsString = if (lbs != 0L) "${lbs}lb" else null
                        val ozString = if (oz > 0) {
                            oz.toString().removeSuffix(".0") + "oz"
                        } else null

                        listOfNotNull(lbsString, ozString).joinToString(" ")
                    }

                PackagingType.Discrete(variants)
            }
        }

        ItemFormState(
            FormFieldState(nameInput),
            FormFieldState(categoryIdInput),
            savedWeights,
            isValid = validData != null
        )
    }

    fun onNameChange(newValue: String) {
        nameInput = newValue
    }

    fun onCategoryIdChange(newValue: Long?) {
        categoryIdInput = newValue
    }

    fun onPackagingIsLooseChange(newValue: Boolean) {
        packagingInput = when (newValue) {
            true -> PackagingInput.Loose
            false -> PackagingInput.Discrete(emptySet())
        }
    }

    fun onAddDiscretePackageSize(newSize: Weight) {
        when (val current = packagingInput) {
            is PackagingInput.Discrete -> {
                packagingInput = current.copy(
                    variants = current.variants.plus(newSize)
                )
            }

            else -> Unit
        }
    }

    fun onRemoveDiscretePackageSize(index: Int) {
        when (val current = packagingInput) {
            is PackagingInput.Discrete -> {
                packagingInput = current.copy(
                    variants = current.variants.sortedBy { it.centigrams }.filterIndexed { i, _ -> i != index }.toSet()
                )
            }

            else -> Unit
        }
    }
}