package com.julianfortune.glacier.ui.delegate

import com.julianfortune.glacier.data.repository.ProgramRepository
import com.julianfortune.glacier.ui.common.data.Option
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DefaultProgramOptionsProvider(
    programRepository: ProgramRepository,
    scope: CoroutineScope
) : ProgramOptionsProvider {

    override val programOptions = programRepository.getAll()
        .map { xs ->
            xs.map { Option(it.id, it.name) }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

}
