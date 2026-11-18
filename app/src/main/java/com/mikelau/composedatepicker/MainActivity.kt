package com.mikelau.composedatepicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.mikelau.composedatepicker.ui.datepicker.ComposeDatePicker
import com.mikelau.composedatepicker.ui.datepicker.ComposeDatePickerBottomSheet
import com.mikelau.composedatepicker.ui.datepicker.ComposeDatePickerColors
import com.mikelau.composedatepicker.ui.datepicker.ComposeDatePickerDefaults
import com.mikelau.composedatepicker.ui.datepicker.DatePickerBottomSheetDefaults
import com.mikelau.composedatepicker.ui.datepicker.DatePickerState
import com.mikelau.composedatepicker.ui.theme.ComposeDatePickerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeDatePickerTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Compose Date Picker",
                style = MaterialTheme.typography.headlineMedium
            )

            AdaptiveDemoContent()
        }
    }
}

@Composable
fun AdaptiveDemoContent() {
    var showDialog by remember { mutableStateOf(false) }
    var showAnchored by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectionMode by remember { mutableStateOf(DatePickerState.SelectionMode.Single) }
    var useCustomColors by remember { mutableStateOf(false) }
    
    var selectedText by remember { mutableStateOf("No date selected") }

    // Defaults reproduce the original look; custom shows the dark/purple palette.
    val cardColors: ComposeDatePickerColors =
        if (useCustomColors) customCardColors() else ComposeDatePickerDefaults.colors()
    val sheetColors = if (useCustomColors) {
        DatePickerBottomSheetDefaults.colors(
            containerColor = DemoDark,
            rangeStartColor = DemoPurple,
            rangeHighlightAlpha = 0.3f,
        )
    } else {
        DatePickerBottomSheetDefaults.colors()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Selection Mode Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Single")
            Switch(
                checked = selectionMode == DatePickerState.SelectionMode.Range,
                onCheckedChange = { 
                    selectionMode = if (it) DatePickerState.SelectionMode.Range else DatePickerState.SelectionMode.Single
                }
            )
            Text("Range")
        }

        // Color customization Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Default colors")
            Switch(checked = useCustomColors, onCheckedChange = { useCustomColors = it })
            Text("Custom colors")
        }

        HorizontalDivider()

        // Popup Dialog Trigger
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(Modifier.width(8.dp))
            Text("Open in Dialog")
        }

        // Anchored Picker Section
        Box {
            OutlinedButton(
                onClick = { showAnchored = !showAnchored },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Toggle Anchored (Dropdown)")
            }

            if (showAnchored) {
                Popup(
                    alignment = Alignment.TopCenter,
                    offset = IntOffset(0, 160),
                    onDismissRequest = { showAnchored = false },
                    properties = PopupProperties(focusable = true)
                ) {
                    ComposeDatePicker(
                        modifier = Modifier.fillMaxWidth(),
                        state = remember(selectionMode) { DatePickerState(selectionMode = selectionMode) },
                        colors = cardColors,
                        onCancel = { showAnchored = false },
                        onConfirm = { state ->
                            selectedText = formatSelection(state)
                            showAnchored = false
                        }
                    )
                }
            }
        }

        // Bottom Sheet Trigger
        FilledTonalButton(
            onClick = { showBottomSheet = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open in Bottom Sheet")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Selection: $selectedText",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    if (showBottomSheet) {
        ComposeDatePickerBottomSheet(
            state = remember(selectionMode) { DatePickerState(selectionMode = selectionMode) },
            colors = sheetColors,
            onDismissRequest = { showBottomSheet = false },
            onConfirm = { state ->
                selectedText = formatSelection(state)
                showBottomSheet = false
            }
        )
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            ComposeDatePicker(
                state = remember(selectionMode) { DatePickerState(selectionMode = selectionMode) },
                colors = cardColors,
                onCancel = { showDialog = false },
                onConfirm = { state ->
                    selectedText = formatSelection(state)
                    showDialog = false
                }
            )
        }
    }
}

private val DemoDark = Color(0xFF1E1D22)
private val DemoPurple = Color(0xFF9C7BFF)

private fun customCardColors(): ComposeDatePickerColors = ComposeDatePickerDefaults.colors(
    containerColor = DemoDark,
    monthTitleColor = Color.White,
    navigationIconColor = Color.White,
    dayOfWeekTextColor = Color(0xFFA1A1AA),
    dayTextColor = Color(0xFFD4D4D8),
    todayTextColor = Color.White,
    todayOutlineColor = DemoPurple,
    selectedDayColor = DemoPurple,
    rangeStartColor = DemoPurple,
    rangeEndColor = DemoPurple,
    rangeHighlightColor = DemoPurple,
    rangeHighlightAlpha = 0.3f,
    confirmButtonColor = DemoPurple,
    cancelButtonColor = DemoPurple,
)

private fun formatSelection(state: DatePickerState): String {
    return if (state.selectionMode == DatePickerState.SelectionMode.Single) {
        state.selectedDate?.toString() ?: "None"
    } else {
        "${state.rangeStart ?: "..."} to ${state.rangeEnd ?: "..."}"
    }
}
