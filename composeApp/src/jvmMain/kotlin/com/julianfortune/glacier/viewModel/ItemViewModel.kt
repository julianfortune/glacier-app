package com.julianfortune.glacier.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.Item
import com.julianfortune.glacier.repository.ItemRepository
import com.julianfortune.glacier.viewModel.data.EntityOperation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ItemViewModel(private val itemRepository: ItemRepository) : ViewModel() {

    private val _itemOperation = mutableStateOf<EntityOperation<Item>?>(null)
    val itemOperation: State<EntityOperation<Item>?> = _itemOperation

    // TODO: Understand what this is doing
    val items = itemRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    suspend fun saveItem(item: Item) {
        itemRepository.insert(item)
    }

    suspend fun updateItem(item: Entity<Item>) {
        itemRepository.update(item)
    }

    suspend fun deleteItem(itemId: Long) {
        itemRepository.deleteById(itemId)
    }

    fun showNewItem() {
        _itemOperation.value = EntityOperation.CreateNew
    }

    fun showEditItem(item: Entity<Item>) {
        _itemOperation.value = EntityOperation.Edit(item)
    }

    fun showDeleteItem(item: Entity<Item>) {
        _itemOperation.value = EntityOperation.Delete(item.id)
    }

    fun dismissItemModal() {
        _itemOperation.value = null
    }

    fun cancelItemOperation() {
        _itemOperation.value = null
    }
}