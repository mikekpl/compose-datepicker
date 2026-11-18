package com.mikelau.composedatepicker.ui.datepicker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

private val DayCircleSize = 36.dp
private val DayCellHeight = 54.dp

/**
 * Colors used by [ComposeDatePickerBottomSheet].
 * Create an instance with [DatePickerBottomSheetDefaults.colors].
 */
@Immutable
class DatePickerBottomSheetColors(
    val containerColor: Color,
    val scrimColor: Color,
    val confirmTextColor: Color,
    val monthTitleColor: Color,
    val dayOfWeekTextColor: Color,
    val dayTextColor: Color,
    val todayTextColor: Color,
    val inRangeDayTextColor: Color,
    val selectedDayTextColor: Color,
    val selectedDayColor: Color,
    val rangeStartColor: Color,
    val rangeEndColor: Color,
    val rangeHighlightColor: Color,
    val rangeHighlightAlpha: Float,
)

object DatePickerBottomSheetDefaults {
    /**
     * @param containerColor Background of the bottom sheet / calendar.
     * @param selectedDayColor Circle highlight of the selected date in Single mode.
     * @param rangeStartColor Circle highlight of the range start date (Range mode).
     * @param rangeEndColor Circle highlight of the range end date (Range mode).
     * @param rangeHighlightColor Color of the band between start and end. Drawn with [rangeHighlightAlpha].
     * @param rangeHighlightAlpha Multiplied into [rangeHighlightColor]'s alpha.
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun colors(
        containerColor: Color = Color(0xFF1E1D22),
        scrimColor: Color = BottomSheetDefaults.ScrimColor,
        confirmTextColor: Color = Color.White,
        monthTitleColor: Color = Color.White,
        dayOfWeekTextColor: Color = Color.White,
        dayTextColor: Color = Color(0xFFD4D4D8),
        todayTextColor: Color = Color.White,
        inRangeDayTextColor: Color = dayTextColor,
        selectedDayTextColor: Color = Color.White,
        rangeStartColor: Color = Color(0xFFFFB547),
        rangeEndColor: Color = rangeStartColor,
        rangeHighlightColor: Color = rangeStartColor,
        rangeHighlightAlpha: Float = 0.2f,
        selectedDayColor: Color = rangeStartColor,
    ): DatePickerBottomSheetColors = DatePickerBottomSheetColors(
        containerColor = containerColor,
        scrimColor = scrimColor,
        confirmTextColor = confirmTextColor,
        monthTitleColor = monthTitleColor,
        dayOfWeekTextColor = dayOfWeekTextColor,
        dayTextColor = dayTextColor,
        todayTextColor = todayTextColor,
        inRangeDayTextColor = inRangeDayTextColor,
        selectedDayTextColor = selectedDayTextColor,
        selectedDayColor = selectedDayColor,
        rangeStartColor = rangeStartColor,
        rangeEndColor = rangeEndColor,
        rangeHighlightColor = rangeHighlightColor,
        rangeHighlightAlpha = rangeHighlightAlpha,
    )

    val Shape: Shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

    /** "June 2026" */
    fun monthTitle(month: LocalDate): String =
        "${month.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${month.year}"

    /** "Mo", "Tu", "We", ... */
    fun dayOfWeekLabel(dayOfWeek: DayOfWeek): String =
        dayOfWeek.name.take(2).lowercase().replaceFirstChar { it.uppercase() }
}

/**
 * A modal bottom sheet showing a vertically scrolling list of months for picking a single
 * date or a date range, depending on [DatePickerState.selectionMode].
 *
 * There is no cancel button: swiping the sheet down (or tapping the scrim / pressing back)
 * calls [onDismissRequest]. Tapping [confirmText] hides the sheet and then calls [onConfirm].
 * Callers should remove the sheet from composition in both callbacks.
 *
 * @param state Selection state. [DatePickerState.currentMonth] is used as the anchor month of the list.
 * @param monthsBefore Number of months shown before the anchor month.
 * @param monthsAfter Number of months shown after the anchor month.
 * @param maxHeightFraction Fraction of the available height the sheet occupies.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeDatePickerBottomSheet(
    onDismissRequest: () -> Unit,
    onConfirm: (DatePickerState) -> Unit,
    modifier: Modifier = Modifier,
    state: DatePickerState = remember { DatePickerState() },
    confirmText: String = "Done",
    colors: DatePickerBottomSheetColors = DatePickerBottomSheetDefaults.colors(),
    shape: Shape = DatePickerBottomSheetDefaults.Shape,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    monthsBefore: Int = 0,
    monthsAfter: Int = 11,
    maxHeightFraction: Float = 0.85f,
    monthTitle: (LocalDate) -> String = DatePickerBottomSheetDefaults::monthTitle,
    dayOfWeekLabel: (DayOfWeek) -> String = DatePickerBottomSheetDefaults::dayOfWeekLabel,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        shape = shape,
        containerColor = colors.containerColor,
        contentColor = colors.dayTextColor,
        scrimColor = colors.scrimColor,
        dragHandle = null,
    ) {
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(maxHeightFraction)) {
            // "Done" (confirm) in the top-right corner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, end = 8.dp)
            ) {
                Text(
                    text = confirmText,
                    color = colors.confirmTextColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onConfirm(state)
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                )
            }

            val anchorMonth = remember(state) {
                LocalDate(state.currentMonth.year, state.currentMonth.month, 1)
            }
            val months = remember(anchorMonth, monthsBefore, monthsAfter) {
                (-monthsBefore..monthsAfter).map { anchorMonth.plus(it, DateTimeUnit.MONTH) }
            }
            val initialIndex = remember(months) {
                val focus = when (state.selectionMode) {
                    DatePickerState.SelectionMode.Single -> state.selectedDate
                    DatePickerState.SelectionMode.Range -> state.rangeStart
                } ?: anchorMonth
                months.indexOfFirst { it.year == focus.year && it.month == focus.month }
                    .takeIf { it >= 0 } ?: monthsBefore.coerceIn(0, months.lastIndex.coerceAtLeast(0))
            }
            val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
            val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                items(months, key = { "${it.year}-${it.month.ordinal}" }) { month ->
                    CalendarMonth(
                        month = month,
                        state = state,
                        today = today,
                        colors = colors,
                        firstDayOfWeek = firstDayOfWeek,
                        monthTitle = monthTitle,
                        dayOfWeekLabel = dayOfWeekLabel,
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarMonth(
    month: LocalDate,
    state: DatePickerState,
    today: LocalDate,
    colors: DatePickerBottomSheetColors,
    firstDayOfWeek: DayOfWeek,
    monthTitle: (LocalDate) -> String,
    dayOfWeekLabel: (DayOfWeek) -> String,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = monthTitle(month),
            color = colors.monthTitleColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 4.dp)
        )

        val weekDays = remember(firstDayOfWeek) {
            (0 until 7).map { DayOfWeek((firstDayOfWeek.ordinal + it) % 7 + 1) }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(DayCellHeight)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            weekDays.forEach { day ->
                Text(
                    text = dayOfWeekLabel(day),
                    color = colors.dayOfWeekTextColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        val weeks = remember(month, firstDayOfWeek) {
            DatePickerUtils.getDaysInMonth(month, firstDayOfWeek).chunked(7)
        }
        weeks.forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                for (column in 0 until 7) {
                    val date = week.getOrNull(column)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(DayCellHeight)
                    ) {
                        if (date != null) {
                            CalendarDay(
                                date = date,
                                column = column,
                                state = state,
                                isToday = date == today,
                                colors = colors,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    date: LocalDate,
    column: Int,
    state: DatePickerState,
    isToday: Boolean,
    colors: DatePickerBottomSheetColors,
) {
    // Circle colour for this day, or null when the day is not selected.
    val circleColor: Color?
    // Range band state (always false in Single mode).
    val hasBand: Boolean
    val isStart: Boolean
    val isEnd: Boolean

    when (state.selectionMode) {
        DatePickerState.SelectionMode.Single -> {
            circleColor = if (date == state.selectedDate) colors.selectedDayColor else null
            hasBand = false
            isStart = false
            isEnd = false
        }
        DatePickerState.SelectionMode.Range -> {
            val start = state.rangeStart
            val end = state.rangeEnd
            isStart = date == start
            isEnd = date == end
            hasBand = start != null && end != null && start < end && date >= start && date <= end
            circleColor = when {
                isStart -> colors.rangeStartColor
                isEnd -> colors.rangeEndColor
                else -> null
            }
        }
    }

    val isInRange = hasBand && !isStart && !isEnd
    val bandColor = colors.rangeHighlightColor.withHighlightAlpha(colors.rangeHighlightAlpha)
    val isLastDayOfMonth = date.plus(1, DateTimeUnit.DAY).month != date.month
    val extendsLeft = hasBand && !isStart && column > 0 && date.day != 1
    val extendsRight = hasBand && !isEnd && column < 6 && !isLastDayOfMonth

    val textColor = when {
        circleColor != null -> colors.selectedDayTextColor
        isToday -> colors.todayTextColor
        isInRange -> colors.inRangeDayTextColor
        else -> colors.dayTextColor
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(DayCellHeight)
            .drawBehind {
                if (!hasBand) return@drawBehind
                val cx = size.width / 2f
                val radius = DayCircleSize.toPx() / 2f
                // Band runs edge-to-edge between neighbouring days, and stops one circle
                // radius past the centre at week/month boundaries, or at the centre for start/end.
                val left = when {
                    isStart -> cx
                    extendsLeft -> 0f
                    else -> cx - radius
                }
                val right = when {
                    isEnd -> cx
                    extendsRight -> size.width
                    else -> cx + radius
                }
                if (right > left) {
                    drawRect(
                        color = bandColor,
                        topLeft = Offset(left, size.height / 2f - radius),
                        size = Size(right - left, radius * 2f)
                    )
                }
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = DayCircleSize / 2)
            ) { state.onDateSelected(date) },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(DayCircleSize)
                .clip(CircleShape)
                .background(circleColor ?: Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.day.toString(),
                color = textColor,
                fontSize = 15.sp,
                fontWeight = if (circleColor != null || isToday) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}
