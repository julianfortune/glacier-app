package com.julianfortune.glacier.feature.namedentity

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.common.Entity
import com.julianfortune.glacier.data.repository.NamedEntityRepository
import com.julianfortune.glacier.feature.namedentity.data.EntityOperation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

open class NamedEntityViewModel<T>(private val repository: NamedEntityRepository<T>) : ViewModel() {

    private val _operation = mutableStateOf<EntityOperation<T>?>(null)
    val operation: State<EntityOperation<T>?> = _operation

    val allEntities = repository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    suspend fun save(data: T) {
        repository.insert(data)
    }

    suspend fun update(entity: Entity<T>) {
        repository.update(entity)
    }

    suspend fun delete(entityId: Long) {
        repository.deleteById(entityId)
    }

    fun showCreateNew() {
        _operation.value = EntityOperation.CreateNew
    }

    fun showEdit(entity: Entity<T>) {
        _operation.value = EntityOperation.Edit(entity)
    }

    fun showDelete(entity: Entity<T>) {
        _operation.value = EntityOperation.Delete(entity.id)
    }

    fun dismissOperation() {
        _operation.value = null
    }
}