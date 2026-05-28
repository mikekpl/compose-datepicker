# ComposeDatePicker

A lightweight, customizable Date Picker for Jetpack Compose and Compose Multiplatform, built with Material 3.

## Features

- 📅 **Single Date Selection**: Simple and intuitive date picking.
- ↔️ **Range Date Selection**: Easy start and end date selection with range highlighting.
- 🎨 **Material 3**: Designed with modern Material 3 components and principles.
- 🌍 **Compose Multiplatform**: Ready for Android and iOS.
- 🛠️ **Customizable**: Easy to theme with your application's colors.

## Installation

This library is hosted on GitHub Packages. To use it, you need to add the repository to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/mikekpl/compose-datepicker")
            credentials {
                username = "YOUR_GITHUB_USERNAME"
                password = "YOUR_GITHUB_PERSONAL_ACCESS_TOKEN"
            }
        }
    }
}
```

Then, add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.mikekpl:compose-datepicker:1.0.0")
}
```

## Usage

### Single Date Selection

```kotlin
val state = remember { DatePickerState(selectionMode = DatePickerState.SelectionMode.Single) }

ComposeDatePicker(
    state = state,
    onConfirm = { confirmedState ->
        println("Selected date: ${confirmedState.selectedDate}")
    },
    onCancel = { /* Handle cancel */ }
)
```

### Range Date Selection

```kotlin
val state = remember { DatePickerState(selectionMode = DatePickerState.SelectionMode.Range) }

ComposeDatePicker(
    state = state,
    onConfirm = { confirmedState ->
        println("Selected range: ${confirmedState.rangeStart} to ${confirmedState.rangeEnd}")
    },
    onCancel = { /* Handle cancel */ }
)
```

### Customizing Colors

You can easily override the default colors to match your brand:

```kotlin
ComposeDatePicker(
    primaryColor = Color(0xFF6200EE),
    accentColor = Color(0xFF03DAC6),
    rangeColor = Color(0xFFBB86FC),
    onConfirm = { /* ... */ }
)
```

## DatePickerState

`DatePickerState` provides access to the current selection:

- `selectedDate`: The currently selected `LocalDate` (Single mode).
- `rangeStart`: The start of the selected range (Range mode).
- `rangeEnd`: The end of the selected range (Range mode).
- `currentMonth`: The month currently being displayed in the picker.

## Dependencies

- [Kotlinx Datetime](https://github.com/Kotlin/kotlinx-datetime)
- Jetpack Compose / Compose Multiplatform (Material 3)

## License

```
Copyright 2026 Mike Lau

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
