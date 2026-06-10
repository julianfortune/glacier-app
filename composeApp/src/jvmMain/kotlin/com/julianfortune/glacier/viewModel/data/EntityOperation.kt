package com.julianfortune.glacier.viewModel.data

import com.julianfortune.glacier.data.Entity

sealed interface EntityOperation<in T> {
    data object CreateNew : EntityOperation<Any>
    data class Edit<T>(val entity: Entity<T>) : EntityOperation<T>
    data class Delete(val id: Long) : EntityOperation<Any>
}