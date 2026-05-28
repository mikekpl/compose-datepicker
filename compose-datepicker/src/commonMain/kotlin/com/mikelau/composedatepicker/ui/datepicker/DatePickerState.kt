package com.mikelau.composedatepicker.ui.datepicker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class DatePickerState(
    initialDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val selectionMode: SelectionMode = SelectionMode.Single
) {
    var currentMonth by mutableStateOf(LocalDate(initialDate.year, initialDate.month, 1))
    var selectedDate by mutableStateOf<LocalDate?>(initialDate)
    var rangeStart by mutableStateOf<LocalDate?>(null)
    var rangeEnd by mutableStateOf<LocalDate?>(null)

    enum class SelectionMode { Single, Range }

    fun onDateSelected(date: LocalDate) {
        when (selectionMode) {
            SelectionMode.Single -> {
                selectedDate = date
            }
            SelectionMode.Range -> {
                if (rangeStart == null || (rangeStart != null && rangeEnd != null)) {
                    rangeStart = date
                    rangeEnd = null
                } else if (rangeStart != null && rangeEnd == null) {
                    if (date < rangeStart!!) {
                        rangeEnd = rangeStart
                        rangeStart = date
                    } else if (date == rangeStart) {
                        // Clicking start again clears it or keeps it?
                        // Let's keep it as start and just don't set end yet, 
                        // or treat it as a 1-day range.
                        rangeEnd = date
                    } else {
                        rangeEnd = date
                    }
                }
            }
        }
    }

    fun nextMonth() {
        currentMonth = currentMonth.plus(1, DateTimeUnit.MONTH)
    }

    fun previousMonth() {
        currentMonth = currentMonth.minus(1, DateTimeUnit.MONTH)
    }
}
