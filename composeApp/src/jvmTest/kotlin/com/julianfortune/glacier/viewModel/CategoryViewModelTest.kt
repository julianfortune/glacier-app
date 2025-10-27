package com.julianfortune.glacier.viewModel

import com.julianfortune.glacier.repository.CategoryRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class CategoryViewModelTest {

    lateinit var viewModel: CategoryViewModel
    lateinit var categoryRepository: CategoryRepository

    @BeforeEach
    fun setUp() {
        categoryRepository = mock()
        viewModel = CategoryViewModel(categoryRepository)
    }

    @Test
    fun deleteCategory() {
        // WHEN
        viewModel.deleteCategory(1L)

        // THEN
        verify(categoryRepository).deleteById(eq(1L))
    }

}