package com.julianfortune.glacier.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.data.Entity
import com.julianfortune.glacier.data.domain.Program
import com.julianfortune.glacier.db.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProgramRepository(private val database: Database) : NamedEntityRepository<Program> {

    // TODO: Understand Flows and coroutine contexts
    override fun getAll(): Flow<List<Entity<Program>>> {
        return database.programQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { programs ->
                programs.map { s ->
                    Entity(s.id, Program(s.name))
                }
            }
    }

    override suspend fun insert(data: Program): Long {
        return database.programQueries.insert(data.name).awaitAsOne()
    }

    override suspend fun update(entity: Entity<Program>) {
        database.programQueries.updateById(entity.data.name, entity.id)
    }

    override suspend fun deleteById(id: Long): Boolean {
        val rowsUpdated = database.programQueries.deleteById(id)

        return rowsUpdated > 1
    }
}
