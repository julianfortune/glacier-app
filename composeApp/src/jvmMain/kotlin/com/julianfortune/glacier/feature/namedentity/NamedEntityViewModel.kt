package com.julianfortune.glacier.feature.namedentity

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julianfortune.glacier.data.common.NamedEntity
import com.julianfortune.glacier.repository.NamedEntityRepository
import com.julianfortune.glacier.feature.namedentity.data.EntityOperation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

open class NamedEntityViewModel<T : NamedEntity>(private val repository: NamedEntityRepository<T>) : ViewModel() {

    private val _operation = mutableStateOf<EntityOperation<T>?>(null)
    val operation: State<EntityOperation<T>?> = _operation

    val allEntities = repository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    suspend fun save(name: String) {
        repository.execute(NamedEntityRepository.Command.Insert(name))
    }

    suspend fun update(id: Long, name: String) {
        repository.execute(NamedEntityRepository.Command.Update(id, name))
    }

    suspend fun delete(id: Long) {
        repository.execute(NamedEntityRepository.Command.Delete(id))
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