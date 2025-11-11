package com.julianfortune.glacier.viewModel

import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.Category
import com.julianfortune.glacier.repository.CategoryRepository
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
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
        // GIVEN
        val target = Entity(1L, Category("Foo"))

        // WHEN
        viewModel.deleteCategory(target)

        // THEN
        verify {
            runBlocking {
                categoryRepository.deleteById(target.id)
            }
        }
    }
}