package com.julianfortune.glacier.ui.page.namedentity

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.common.NamedEntity
import com.julianfortune.glacier.data.repository.NamedEntityRepository
import com.julianfortune.glacier.ui.page.namedentity.data.EntityOperation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

open class NamedEntityPageViewModel<T : NamedEntity>(private val repository: NamedEntityRepository<*, T>) : ViewModel() {

    private val _operation = mutableStateOf<EntityOperation<T>?>(null)
    val operation: State<EntityOperation<T>?> = _operation

    val allEntities = repository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    suspend fun save(name: String) {
        repository.insert(name = name)
    }

    suspend fun update(id: Long, name: String) {
        repository.update(id = id, name = name)
    }

    suspend fun delete(id: Long) {
        repository.delete(id = id)
    }

    fun showCreateNew() {
        _operation.value = EntityOperation.CreateNew
    }

    fun showEdit(entity: T) {
        _operation.value = EntityOperation.Edit(entity)
    }

    fun showDelete(entity: T) {
        _operation.value = EntityOperation.Delete(entity.id)
    }

    fun dismissOperation() {
        _operation.value = null
    }
}