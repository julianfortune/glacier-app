package com.julianfortune.glacier.ui.common

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

fun formatLocalDate(d: LocalDate, style: FormatStyle = FormatStyle.MEDIUM): String {
    val usDateFormatter = DateTimeFormatter.ofLocalizedDate(style).withLocale(Locale.US)
    return d.format(usDateFormatter)
}
