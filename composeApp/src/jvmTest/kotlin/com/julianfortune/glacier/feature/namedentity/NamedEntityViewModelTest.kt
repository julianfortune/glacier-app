package com.julianfortune.glacier.feature.namedentity

import com.julianfortune.glacier.data.domain.Category
import com.julianfortune.glacier.data.repository.CategoryRepository
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach

class NamedEntityViewModelTest {

    lateinit var viewModel: NamedEntityViewModel<Category>
    lateinit var categoryRepository: CategoryRepository

    @BeforeEach
    fun setUp() {
        categoryRepository = mockk(relaxed = true)
        viewModel = NamedEntityViewModel(categoryRepository)
    }

//    @Test
//    fun deleteCategory() {
//        // GIVEN
//        val target = Entity(1L, Category("Foo"))
//
//        // WHEN
//        runBlocking {
//            viewModel.delete(target.id)
//        }
//
//        // THEN
//        verify {
//            runBlocking {
//                categoryRepository.deleteById(target.id)
//            }
//        }
//    }
}