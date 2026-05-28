package com.mikelau.composedatepicker.ui.datepicker

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

object DatePickerUtils {
    fun getDaysInMonth(month: LocalDate): List<LocalDate?> {
        val firstDayOfMonth = LocalDate(month.year, month.month, 1)
        val lastDayOfMonth = firstDayOfMonth.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
        
        val days = mutableListOf<LocalDate?>()
        
        // Add empty slots for days before the first day of the month
        // Assuming week starts on Sunday (index 0 for Sunday in our grid)
        // DayOfWeek.SUNDAY is 7 in ISO-8601, but we want 0.
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek
        val isoDayNumber = firstDayOfWeek.ordinal + 1
        val padding = if (isoDayNumber == 7) 0 else isoDayNumber
        
        repeat(padding) {
            days.add(null)
        }
        
        for (day in 1..lastDayOfMonth.dayOfMonth) {
            days.add(LocalDate(month.year, month.month, day))
        }
        
        return days
    }
}
