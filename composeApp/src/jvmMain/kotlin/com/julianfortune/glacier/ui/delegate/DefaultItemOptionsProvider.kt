package com.julianfortune.glacier.ui.delegate

import com.julianfortune.glacier.data.repository.ItemRepository
import com.julianfortune.glacier.ui.common.data.Option
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DefaultItemOptionsProvider(
    itemRepository: ItemRepository,
    scope: CoroutineScope
) : ItemOptionsProvider {
    
    override val itemOptions = itemRepository.getAll()
        .map { items ->
            items.map { Option(it.id, it.name) }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

}
