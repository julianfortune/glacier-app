package com.julianfortune.glacier.feature.namedentity

import com.julianfortune.glacier.data.common.Entity
import com.julianfortune.glacier.data.domain.Category
import com.julianfortune.glacier.repository.CategoryRepository
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NamedEntityViewModelTest {

    lateinit var viewModel: NamedEntityViewModel<Category>
    lateinit var categoryRepository: CategoryRepository

    @BeforeEach
    fun setUp() {
        categoryRepository = mockk(relaxed = true)
        viewModel = NamedEntityViewModel(categoryRepository)
    }

    @Test
    fun deleteCategory() {
        // GIVEN
        val target = Entity(1L, Category("Foo"))

        // WHEN
        runBlocking {
            viewModel.delete(target.id)
        }

        // THEN
        verify {
            runBlocking {
                categoryRepository.deleteById(target.id)
            }
        }
    }
}