package com.julianfortune.glacier.view.delivery

import com.julianfortune.glacier.data.domain.Item
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun renderItemName(item: Item): String {
    val weight = item.weight?.toImperial()?.let { (lbs, oz) ->
        "(${lbs}lb ${"%.2f".format(oz).trimEnd('0').trimEnd('.')}oz)"
    }

    val description = if (item.description != null) {
        "— ${item.description}"
    } else null
    return listOfNotNull(item.name, description, weight).joinToString(" ")
}

fun formatLocalDate(d: LocalDate, style: FormatStyle = FormatStyle.MEDIUM): String {
    val usDateFormatter = DateTimeFormatter.ofLocalizedDate(style).withLocale(Locale.US)
    return d.format(usDateFormatter)
}
