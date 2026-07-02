package com.julianfortune.glacier.repository

import com.julianfortune.glacier.data.common.Entity
import com.julianfortune.glacier.data.common.NamedEntity
import kotlinx.coroutines.flow.Flow

interface NamedEntityRepository<T: NamedEntity> {

    sealed interface Command {
        data class Insert(val name: String) : Command
        data class Update(val id: Long, val name: String) : Command
        data class Delete(val id: Long) : Command
    }

    fun getAll(): Flow<List<T>>

    suspend fun execute(command: Command): Result<Long>
}
