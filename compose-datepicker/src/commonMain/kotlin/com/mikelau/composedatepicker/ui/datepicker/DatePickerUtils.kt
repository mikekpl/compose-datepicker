package com.mikelau.composedatepicker.ui.datepicker

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

object DatePickerUtils {
    fun getDaysInMonth(
        month: LocalDate,
        firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY
    ): List<LocalDate?> {
        val firstDayOfMonth = LocalDate(month.year, month.month, 1)
        val lastDayOfMonth = firstDayOfMonth.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
        
        val days = mutableListOf<LocalDate?>()
        
        // Add empty slots for days before the first day of the month,
        // relative to the configured first day of the week.
        val monthStartIso = firstDayOfMonth.dayOfWeek.ordinal + 1
        val weekStartIso = firstDayOfWeek.ordinal + 1
        val padding = (monthStartIso - weekStartIso + 7) % 7
        
        repeat(padding) {
            days.add(null)
        }
        
        for (day in 1..lastDayOfMonth.day) {
            days.add(LocalDate(month.year, month.month, day))
        }
        
        return days
    }
}
