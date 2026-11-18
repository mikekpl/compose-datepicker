package com.mikelau.composedatepicker.ui.datepicker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import composedatepicker.compose_datepicker.generated.resources.Res
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlin.time.Clock

@Composable
private fun SvgIcon(
    resourcePath: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1c1c1c)
) {
    var svgBytes by remember { mutableStateOf<ByteArray?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalPlatformContext.current

    LaunchedEffect(resourcePath) {
        scope.launch {
            svgBytes = Res.readBytes(resourcePath)
        }
    }

    svgBytes?.let { bytes ->
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(context)
                .data(bytes)
                .decoderFactory(SvgDecoder.Factory())
                .build()
        )
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            modifier = modifier.height(16.dp),
            tint = tint
        )
    }
}

/**
 * Card-style date picker, usable inside a Dialog or an anchored Popup.
 *
 * [primaryColor], [accentColor] and [rangeColor] are kept for backward compatibility and seed the
 * defaults of [colors]. When [colors] is passed explicitly, it takes precedence.
 */
@Composable
fun ComposeDatePicker(
    modifier: Modifier = Modifier,
    state: DatePickerState = remember { DatePickerState() },
    cancelText: String = "CANCEL",
    confirmText: String = "CONFIRM",
    primaryColor: Color = DefaultPickerGreen,
    accentColor: Color = DefaultPickerOrange,
    rangeColor: Color = DefaultPickerLightGreen,
    colors: ComposeDatePickerColors = ComposeDatePickerDefaults.colors(
        primaryColor = primaryColor,
        accentColor = accentColor,
        rangeColor = rangeColor,
    ),
    onCancel: () -> Unit = {},
    onConfirm: (DatePickerState) -> Unit = {}
) {
    Card(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            DatePickerHeader(
                currentMonth = state.currentMonth,
                onPreviousMonth = { state.previousMonth() },
                onNextMonth = { state.nextMonth() },
                titleColor = colors.monthTitleColor,
                iconColor = colors.navigationIconColor,
            )

            Spacer(modifier = Modifier.height(16.dp))

            DayOfWeekHeader(textColor = colors.dayOfWeekTextColor)

            Spacer(modifier = Modifier.height(8.dp))

            DatePickerGrid(
                state = state,
                primaryColor = primaryColor,
                rangeColor = rangeColor,
                colors = colors,
                onDateSelected = { state.onDateSelected(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            DatePickerActionButtons(
                cancelText = cancelText,
                confirmText = confirmText,
                accentColor = accentColor,
                colors = colors,
                onCancel = onCancel,
                onConfirm = { onConfirm(state) }
            )
        }
    }
}

@Composable
fun DatePickerHeader(
    currentMonth: LocalDate,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    titleColor: Color = Color.Unspecified,
    iconColor: Color = Color(0xFF1C1C1C),
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            SvgIcon(
                resourcePath = "files/ic_keyboard_arrow_left.svg",
                contentDescription = "Previous Month",
                tint = iconColor
            )
        }

        Text(
            text = "${currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${currentMonth.year}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = titleColor
        )

        IconButton(onClick = onNextMonth) {
            SvgIcon(
                resourcePath = "files/ic_keyboard_arrow_right.svg",
                contentDescription = "Next Month",
                tint = iconColor
            )
        }
    }
}

@Composable
fun DayOfWeekHeader(textColor: Color = Color.Gray) {
    Row(modifier = Modifier.fillMaxWidth()) {
        val days = listOf("S", "M", "T", "W", "T", "F", "S")
        days.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@Composable
fun DatePickerGrid(
    state: DatePickerState,
    primaryColor: Color,
    rangeColor: Color,
    colors: ComposeDatePickerColors = ComposeDatePickerDefaults.colors(
        primaryColor = primaryColor,
        rangeColor = rangeColor,
    ),
    onDateSelected: (LocalDate) -> Unit
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val days = DatePickerUtils.getDaysInMonth(state.currentMonth)
    
    Column(modifier = Modifier.fillMaxWidth()) {
        val rows = days.chunked(7)
        rows.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null) {
                            val isRange = state.selectionMode == DatePickerState.SelectionMode.Range
                            val isRangeStart = isRange && date == state.rangeStart
                            val isRangeEnd = isRange && date == state.rangeEnd
                            val isSelected = if (isRange) {
                                isRangeStart || isRangeEnd
                            } else {
                                date == state.selectedDate
                            }
                            
                            val isInRange = isRange &&
                                    state.rangeStart != null && state.rangeEnd != null &&
                                    date > state.rangeStart!! && date < state.rangeEnd!!

                            DatePickerDay(
                                date = date,
                                isSelected = isSelected,
                                isInRange = isInRange,
                                isToday = date == today,
                                primaryColor = primaryColor,
                                rangeColor = rangeColor,
                                colors = colors,
                                isRangeStart = isRangeStart,
                                isRangeEnd = isRangeEnd,
                                onClick = { onDateSelected(date) }
                            )
                        }
                    }
                }
                // Fill the row if it's the last one and has fewer than 7 days
                if (week.size < 7) {
                    repeat(7 - week.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun DatePickerDay(
    date: LocalDate,
    isSelected: Boolean,
    isInRange: Boolean,
    isToday: Boolean,
    primaryColor: Color,
    rangeColor: Color,
    colors: ComposeDatePickerColors = ComposeDatePickerDefaults.colors(
        primaryColor = primaryColor,
        rangeColor = rangeColor,
    ),
    isRangeStart: Boolean = false,
    isRangeEnd: Boolean = false,
    onClick: () -> Unit
) {
    val circleColor = when {
        !isSelected -> Color.Transparent
        isRangeEnd && !isRangeStart -> colors.rangeEndColor
        isRangeStart -> colors.rangeStartColor
        else -> colors.selectedDayColor
    }
    val textColor = when {
        isSelected -> colors.selectedDayTextColor
        isInRange -> colors.inRangeDayTextColor
        isToday -> colors.todayTextColor
        else -> colors.dayTextColor
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isInRange) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    .background(colors.rangeHighlightColor.withHighlightAlpha(colors.rangeHighlightAlpha))
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize(0.85f)
                .clip(CircleShape)
                .background(circleColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.day.toString(),
                color = textColor,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
            )

            if (isToday && !isSelected) {
                androidx.compose.foundation.Canvas(modifier = Modifier.matchParentSize()) {
                    drawCircle(
                        color = colors.todayOutlineColor,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }
    }
}

@Composable
fun DatePickerActionButtons(
    cancelText: String,
    confirmText: String,
    accentColor: Color,
    colors: ComposeDatePickerColors = ComposeDatePickerDefaults.colors(accentColor = accentColor),
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, colors.cancelButtonColor),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.cancelButtonColor)
        ) {
            Text(cancelText, fontSize = 10.sp, maxLines = 1, fontWeight = FontWeight.Bold)
        }
        
        Button(
            onClick = onConfirm,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.confirmButtonColor,
                contentColor = colors.confirmButtonTextColor
            )
        ) {
            Text(confirmText, fontSize = 10.sp, maxLines = 1, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ComposeDatePickerSinglePreview() {
    MaterialTheme {
        ComposeDatePicker(
            state = DatePickerState(selectionMode = DatePickerState.SelectionMode.Single)
        )
    }
}

@Composable
fun ComposeDatePickerRangePreview() {
    MaterialTheme {
        ComposeDatePicker(
            state = DatePickerState(selectionMode = DatePickerState.SelectionMode.Range).apply {
                rangeStart = LocalDate(2025, 9, 10)
                rangeEnd = LocalDate(2025, 9, 20)
            }
        )
    }
}
