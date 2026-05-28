package com.mikelau.composedatepicker.model

import kotlinx.datetime.LocalDate

sealed class DateSelection {
    data class Single(val date: LocalDate?) : DateSelection()
    data class Range(val start: LocalDate?, val end: LocalDate?) : DateSelection()
}
