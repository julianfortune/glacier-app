package com.julianfortune.glacier.ui.page.item

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.domain.Item
import com.julianfortune.glacier.data.domain.ItemHeadline
import com.julianfortune.glacier.data.repository.ItemRepository
import com.julianfortune.glacier.ui.delegate.CategoryOptionsProvider
import com.julianfortune.glacier.ui.page.item.data.ItemBody
import com.julianfortune.glacier.ui.page.namedentity.data.EntityOperation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ItemsPageViewModel(
    private val itemRepository: ItemRepository,
    categoryOptionsProvider: CategoryOptionsProvider,
) : ViewModel(),
    CategoryOptionsProvider by categoryOptionsProvider {

    private val _itemOperation = mutableStateOf<EntityOperation<Item>?>(null)
    val itemOperation: State<EntityOperation<Item>?> = _itemOperation

    val items = itemRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    suspend fun saveItem(body: ItemBody) {
        itemRepository.insert(
            body.name,
            setOfNotNull(body.categoryId),
            body.savedWeights,
        )
    }

    suspend fun updateItem(id: Long, body: ItemBody) {
        itemRepository.update(
            id,
            body.name,
            setOfNotNull(body.categoryId),
            body.savedWeights,
        )
    }

    suspend fun deleteItem(id: Long) {
        itemRepository.deleteById(id)
    }

    fun showNewItem() {
        _itemOperation.value = EntityOperation.CreateNew
    }

    fun showEditItem(item: ItemHeadline) {
        viewModelScope.launch {
            val fullItem = itemRepository.getById(item.id).firstOrNull()
            fullItem?.let { _itemOperation.value = EntityOperation.Edit(it) }
        }
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