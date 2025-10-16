package com.julianfortune.glacier.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.repository.CategoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class CategoryViewModel(categoryRepository: CategoryRepository) : ViewModel() {
    // TODO: Understand what this is doing
    // TODO: Does this refresh automatically ? Seems like it should
    val categories = categoryRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

//    fun addTask(title: String, description: String?) {
//        viewModelScope.launch {
//            taskRepository.insertTask(title, description)
//        }
//    }
}
