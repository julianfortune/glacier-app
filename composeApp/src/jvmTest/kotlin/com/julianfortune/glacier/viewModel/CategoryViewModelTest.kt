package com.julianfortune.glacier.viewModel

import com.julianfortune.glacier.repository.CategoryRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CategoryViewModelTest {

    lateinit var viewModel: CategoryViewModel
    lateinit var categoryRepository: CategoryRepository

    @BeforeEach
    fun setUp() {
        categoryRepository = mockk(relaxed = true)
        viewModel = CategoryViewModel(categoryRepository)
    }

    @Test
    fun deleteCategory() {
        // WHEN
        viewModel.deleteCategory(1L)

        // THEN
        verify { categoryRepository.deleteById(1L) }
    }
}