package com.julianfortune.glacier.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.Category
import com.julianfortune.glacier.repository.CategoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryViewModel(private val categoryRepository: CategoryRepository) : ViewModel() {
    // TODO: Understand what this is doing
    val categories = categoryRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun deleteCategory(category: Entity<Category>) {
        viewModelScope.launch {
            val successful = categoryRepository.deleteById(category.id)
        }
    }
}
