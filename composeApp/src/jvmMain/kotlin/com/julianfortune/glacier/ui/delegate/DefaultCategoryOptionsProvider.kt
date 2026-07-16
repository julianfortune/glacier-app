package com.julianfortune.glacier.ui.delegate

import com.julianfortune.glacier.data.repository.CategoryRepository
import com.julianfortune.glacier.ui.common.data.Option
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DefaultCategoryOptionsProvider(
    categoryRepository: CategoryRepository,
    scope: CoroutineScope
) : CategoryOptionsProvider {
    
    override val categoryOptions = categoryRepository.getAll()
        .map { cs ->
            cs.map { Option(it.id, it.name) }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

}
