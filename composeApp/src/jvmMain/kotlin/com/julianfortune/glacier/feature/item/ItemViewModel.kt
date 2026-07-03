package com.julianfortune.glacier.feature.item

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.domain.ItemHeadline
import com.julianfortune.glacier.data.repository.ItemRepository
import com.julianfortune.glacier.feature.namedentity.data.EntityOperation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ItemViewModel(private val itemRepository: ItemRepository) : ViewModel() {

    private val _itemOperation = mutableStateOf<EntityOperation<ItemHeadline>?>(null)
    val itemOperation: State<EntityOperation<ItemHeadline>?> = _itemOperation

    // TODO: Understand what this is doing
    val items = itemRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    suspend fun saveItem(name: String) {
        // TODO: Category IDs
        itemRepository.insert(name, emptySet())
    }

    suspend fun updateItem(id: Long, name: String) {
        itemRepository.update(id, name, emptySet())
    }

    suspend fun deleteItem(id: Long) {
        itemRepository.deleteById(id)
    }

    fun showNewItem() {
        _itemOperation.value = EntityOperation.CreateNew
    }

    fun showEditItem(item: ItemHeadline) {
        _itemOperation.value = EntityOperation.Edit(item)
    }

    fun showDeleteItem(item: ItemHeadline) {
        _itemOperation.value = EntityOperation.Delete(item.id)
    }

    fun dismissItemModal() {
        _itemOperation.value = null
    }

    fun cancelItemOperation() {
        _itemOperation.value = null
    }
}