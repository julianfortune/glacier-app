package com.julianfortune.glacier.view.delivery

import com.julianfortune.glacier.data.domain.Item

fun renderItemName(item: Item): String {
    val weight = item.weight?.toImperial()?.let { (lbs, oz) ->
        "(${lbs}lb ${"%.2f".format(oz).trimEnd('0').trimEnd('.')}oz)"
    }

    val description = if (item.description != null) {
        "— ${item.description}"
    } else null
    return listOfNotNull(item.name, description, weight).joinToString(" ")
}
