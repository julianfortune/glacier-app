package com.julianfortune.glacier.repository

import com.julianfortune.glacier.data.Entity
import kotlinx.coroutines.flow.Flow

interface NamedEntityRepository<T> {

    fun getAll(): Flow<List<Entity<T>>>

    suspend fun insert(data: T): Long

    suspend fun update(entity: Entity<T>)

    suspend fun deleteById(id: Long): Boolean
}
