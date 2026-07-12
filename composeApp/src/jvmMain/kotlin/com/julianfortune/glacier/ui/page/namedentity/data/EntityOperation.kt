package com.julianfortune.glacier.ui.page.namedentity.data


sealed interface EntityOperation<out T> {
    data object CreateNew : EntityOperation<Nothing>
    data class Edit<T>(val entity: T) : EntityOperation<T>
    data class Delete(val id: Long) : EntityOperation<Nothing>
}