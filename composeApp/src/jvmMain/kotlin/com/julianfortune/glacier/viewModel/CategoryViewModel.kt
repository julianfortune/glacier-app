package com.julianfortune.glacier.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.repository.CategoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class CategoryViewModel(categoryRepository: CategoryRepository) : ViewModel() {
    // TODO: Understand what this is doing
    val categories = categoryRepository.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

//    fun addTask(title: String, description: String?) {
//        viewModelScope.launch {
//            taskRepository.insertTask(title, description)
//        }
//    }
}
