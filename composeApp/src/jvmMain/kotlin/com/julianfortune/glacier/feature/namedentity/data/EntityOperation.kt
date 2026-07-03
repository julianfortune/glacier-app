package com.julianfortune.glacier.feature.namedentity.data

import com.julianfortune.glacier.data.common.Entity

sealed interface EntityOperation<out T> {
    data object CreateNew : EntityOperation<Nothing>
    data class Edit<T>(val entity: T) : EntityOperation<T>
    data class Delete(val id: Long) : EntityOperation<Nothing>
}