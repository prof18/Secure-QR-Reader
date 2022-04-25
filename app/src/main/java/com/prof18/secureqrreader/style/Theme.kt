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
import androidx.compose.ui.graphics.Color

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
//    onBackground = LightAppColors.onBackground,
//    onSurface = LightAppColors.onSurface,
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
    MaterialTheme(
        colors = if (darkTheme) DarkThemeColors else LightThemeColors,
        typography = SecureQrReaderTypography,
        shapes = SecureQrReaderShapes,
        content = content
    )
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