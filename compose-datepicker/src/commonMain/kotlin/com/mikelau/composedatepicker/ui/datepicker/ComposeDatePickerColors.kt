package com.mikelau.composedatepicker.ui.datepicker

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

internal val DefaultPickerGreen = Color(0xFF558B2F)
internal val DefaultPickerOrange = Color(0xFFFFB300)
internal val DefaultPickerLightGreen = Color(0xFFDCEDC8)

/**
 * Colors used by [ComposeDatePicker] (dialog / anchored dropdown card).
 * Create an instance with [ComposeDatePickerDefaults.colors].
 */
@Immutable
class ComposeDatePickerColors(
    val containerColor: Color,
    val monthTitleColor: Color,
    val navigationIconColor: Color,
    val dayOfWeekTextColor: Color,
    val dayTextColor: Color,
    val todayTextColor: Color,
    val todayOutlineColor: Color,
    val inRangeDayTextColor: Color,
    val selectedDayTextColor: Color,
    val selectedDayColor: Color,
    val rangeStartColor: Color,
    val rangeEndColor: Color,
    val rangeHighlightColor: Color,
    val rangeHighlightAlpha: Float,
    val confirmButtonColor: Color,
    val confirmButtonTextColor: Color,
    val cancelButtonColor: Color,
)

object ComposeDatePickerDefaults {
    /**
     * Every default reproduces the look of versions prior to the `colors` API, derived from the
     * three legacy colors [primaryColor], [accentColor] and [rangeColor].
     *
     * @param primaryColor Legacy shortcut: seeds [selectedDayColor], [rangeStartColor],
     *   [rangeEndColor] and [todayOutlineColor].
     * @param accentColor Legacy shortcut: seeds [confirmButtonColor] and [cancelButtonColor].
     * @param rangeColor Legacy shortcut: seeds [rangeHighlightColor].
     * @param containerColor Background of the calendar card.
     * @param monthTitleColor Month/year header. [Color.Unspecified] uses the current content color.
     * @param selectedDayColor Circle highlight of the selected date in Single mode.
     * @param rangeStartColor Circle highlight of the range start date.
     * @param rangeEndColor Circle highlight of the range end date.
     * @param rangeHighlightColor Band between the start and end dates.
     * @param rangeHighlightAlpha Multiplied into [rangeHighlightColor]'s alpha.
     */
    fun colors(
        primaryColor: Color = DefaultPickerGreen,
        accentColor: Color = DefaultPickerOrange,
        rangeColor: Color = DefaultPickerLightGreen,
        containerColor: Color = Color.White,
        monthTitleColor: Color = Color.Unspecified,
        navigationIconColor: Color = Color(0xFF1C1C1C),
        dayOfWeekTextColor: Color = Color.Gray,
        dayTextColor: Color = Color.Black,
        todayTextColor: Color = dayTextColor,
        todayOutlineColor: Color = primaryColor,
        inRangeDayTextColor: Color = dayTextColor,
        selectedDayTextColor: Color = Color.White,
        selectedDayColor: Color = primaryColor,
        rangeStartColor: Color = primaryColor,
        rangeEndColor: Color = rangeStartColor,
        rangeHighlightColor: Color = rangeColor,
        rangeHighlightAlpha: Float = 1f,
        confirmButtonColor: Color = accentColor,
        confirmButtonTextColor: Color = Color.White,
        cancelButtonColor: Color = accentColor,
    ): ComposeDatePickerColors = ComposeDatePickerColors(
        containerColor = containerColor,
        monthTitleColor = monthTitleColor,
        navigationIconColor = navigationIconColor,
        dayOfWeekTextColor = dayOfWeekTextColor,
        dayTextColor = dayTextColor,
        todayTextColor = todayTextColor,
        todayOutlineColor = todayOutlineColor,
        inRangeDayTextColor = inRangeDayTextColor,
        selectedDayTextColor = selectedDayTextColor,
        selectedDayColor = selectedDayColor,
        rangeStartColor = rangeStartColor,
        rangeEndColor = rangeEndColor,
        rangeHighlightColor = rangeHighlightColor,
        rangeHighlightAlpha = rangeHighlightAlpha,
        confirmButtonColor = confirmButtonColor,
        confirmButtonTextColor = confirmButtonTextColor,
        cancelButtonColor = cancelButtonColor,
    )
}

/** Applies [alpha] on top of the color's own alpha, so translucent colors stay translucent. */
internal fun Color.withHighlightAlpha(alpha: Float): Color = copy(alpha = this.alpha * alpha)
