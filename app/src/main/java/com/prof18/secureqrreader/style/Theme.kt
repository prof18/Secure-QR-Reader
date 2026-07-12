/*
 * Copyright 2022 Marco Gomiero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.prof18.secureqrreader.style

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
internal data class SecureQrReaderCustomColors(
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val divider: Color,
    val scanAccent: Color,
    val caution: Color,
    val cautionText: Color,
)

private val LightCustomColors = SecureQrReaderCustomColors(
    primaryContainer = LightAppColors.primaryContainer,
    onPrimaryContainer = LightAppColors.onPrimaryContainer,
    surfaceVariant = LightAppColors.surfaceVariant,
    onSurfaceVariant = LightAppColors.onSurfaceVariant,
    outline = LightAppColors.outline,
    divider = LightAppColors.divider,
    scanAccent = LightAppColors.scanAccent,
    caution = LightAppColors.caution,
    cautionText = LightAppColors.cautionText,
)

private val DarkCustomColors = SecureQrReaderCustomColors(
    primaryContainer = DarkAppColors.primaryContainer,
    onPrimaryContainer = DarkAppColors.onPrimaryContainer,
    surfaceVariant = DarkAppColors.surfaceVariant,
    onSurfaceVariant = DarkAppColors.onSurfaceVariant,
    outline = DarkAppColors.outline,
    divider = DarkAppColors.divider,
    scanAccent = DarkAppColors.scanAccent,
    caution = DarkAppColors.caution,
    cautionText = DarkAppColors.cautionText,
)

private val LocalCustomColors = staticCompositionLocalOf { LightCustomColors }

internal val MaterialTheme.customColors: SecureQrReaderCustomColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColors.current

private val LightThemeColors = lightColors(
    primary = LightAppColors.primary,
    primaryVariant = LightAppColors.primaryVariant,
    secondary = LightAppColors.secondary,
    secondaryVariant = LightAppColors.secondaryVariant,
    background = LightAppColors.background,
    surface = LightAppColors.surface,
    error = LightAppColors.error,
    onPrimary = LightAppColors.onPrimary,
    onSecondary = LightAppColors.onSecondary,
    onBackground = LightAppColors.onBackground,
    onSurface = LightAppColors.onSurface,
    onError = LightAppColors.onError,
)

private val DarkThemeColors = darkColors(
    primary = DarkAppColors.primary,
    primaryVariant = DarkAppColors.primaryVariant,
    secondary = DarkAppColors.secondary,
    secondaryVariant = DarkAppColors.secondaryVariant,
    background = DarkAppColors.background,
    surface = DarkAppColors.surface,
    error = DarkAppColors.error,
    onPrimary = DarkAppColors.onPrimary,
    onSecondary = DarkAppColors.onSecondary,
    onBackground = DarkAppColors.onBackground,
    onSurface = DarkAppColors.onSurface,
    onError = DarkAppColors.onError,
)

@Composable
internal fun SecureQrReaderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalCustomColors provides if (darkTheme) DarkCustomColors else LightCustomColors,
    ) {
        MaterialTheme(
            colors = if (darkTheme) DarkThemeColors else LightThemeColors,
            typography = SecureQrReaderTypography,
            shapes = SecureQrReaderShapes,
            content = content
        )
    }
}

@Composable
internal fun toolbarColor(
    darkTheme: Boolean = isSystemInDarkTheme(),
): Color = if (darkTheme) {
    DarkAppColors.toolbar
} else {
    LightAppColors.toolbar
}

@Composable
internal fun toolbarColorSplashScreen(
    darkTheme: Boolean = isSystemInDarkTheme(),
): Color = if (darkTheme) {
    DarkAppColors.toolbar
} else {
    LightAppColors.toolbar
}
