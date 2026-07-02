package com.julianfortune.glacier.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.glacier.data.common.Entity
import com.julianfortune.glacier.data.domain.Program
import com.julianfortune.glacier.db.Database
import com.julianfortune.glacier.repository.NamedEntityRepository.Command
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProgramRepository(private val database: Database) : NamedEntityRepository<Program> {

    // TODO: Understand Flows and coroutine contexts
    override fun getAll(): Flow<List<Program>> {
        return database.programQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { programs ->
                programs.map { s ->
                    Program(s.id, s.name)
                }
            }
    }

    override suspend fun execute(command: Command): Result<Long> {
        return when (command) {
            is Command.Insert -> {
                val newId = database.programQueries.insert(command.name).awaitAsOne()

                Result.success(newId)
            }

            is Command.Update -> {
                database.programQueries.updateById(command.name, command.id)
                Result.success(command.id)
            }

            is Command.Delete -> {
                val rowsUpdated = database.programQueries.deleteById(command.id)

                if (rowsUpdated > 1) {
                    Result.success(command.id)
                } else {
                    Result.failure(RuntimeException("Failed to delete Program with id: ${command.id}"))
                }
            }
        }
    }
}
