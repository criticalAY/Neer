/*
 * Copyright (c) 2024 Ashish Yadav <mailtoashish693@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.criticalay.neer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = NeerLightPrimary,
    onPrimary = NeerLightOnPrimary,
    primaryContainer = NeerLightPrimaryContainer,
    onPrimaryContainer = NeerLightOnPrimaryContainer,
    secondary = NeerLightSecondary,
    onSecondary = NeerLightOnSecondary,
    secondaryContainer = NeerLightSecondaryContainer,
    onSecondaryContainer = NeerLightOnSecondaryContainer,
    tertiary = NeerLightTertiary,
    onTertiary = NeerLightOnTertiary,
    tertiaryContainer = NeerLightTertiaryContainer,
    onTertiaryContainer = NeerLightOnTertiaryContainer,
    error = NeerLightError,
    onError = NeerLightOnError,
    errorContainer = NeerLightErrorContainer,
    onErrorContainer = NeerLightOnErrorContainer,
    background = NeerLightBackground,
    onBackground = NeerLightOnBackground,
    surface = NeerLightSurface,
    onSurface = NeerLightOnSurface,
    surfaceVariant = NeerLightSurfaceVariant,
    onSurfaceVariant = NeerLightOnSurfaceVariant,
    outline = NeerLightOutline,
    outlineVariant = NeerLightOutlineVariant,
    surfaceContainerLowest = NeerLightSurfaceContainerLowest,
    surfaceContainerLow = NeerLightSurfaceContainerLow,
    surfaceContainer = NeerLightSurfaceContainer,
    surfaceContainerHigh = NeerLightSurfaceContainerHigh,
    surfaceContainerHighest = NeerLightSurfaceContainerHighest,
    inverseSurface = NeerLightInverseSurface,
    inverseOnSurface = NeerLightInverseOnSurface,
    inversePrimary = NeerLightInversePrimary,
    scrim = NeerLightScrim,
    surfaceTint = NeerLightSurfaceTint,
)

private val DarkColorScheme = darkColorScheme(
    primary = NeerDarkPrimary,
    onPrimary = NeerDarkOnPrimary,
    primaryContainer = NeerDarkPrimaryContainer,
    onPrimaryContainer = NeerDarkOnPrimaryContainer,
    secondary = NeerDarkSecondary,
    onSecondary = NeerDarkOnSecondary,
    secondaryContainer = NeerDarkSecondaryContainer,
    onSecondaryContainer = NeerDarkOnSecondaryContainer,
    tertiary = NeerDarkTertiary,
    onTertiary = NeerDarkOnTertiary,
    tertiaryContainer = NeerDarkTertiaryContainer,
    onTertiaryContainer = NeerDarkOnTertiaryContainer,
    error = NeerDarkError,
    onError = NeerDarkOnError,
    errorContainer = NeerDarkErrorContainer,
    onErrorContainer = NeerDarkOnErrorContainer,
    background = NeerDarkBackground,
    onBackground = NeerDarkOnBackground,
    surface = NeerDarkSurface,
    onSurface = NeerDarkOnSurface,
    surfaceVariant = NeerDarkSurfaceVariant,
    onSurfaceVariant = NeerDarkOnSurfaceVariant,
    outline = NeerDarkOutline,
    outlineVariant = NeerDarkOutlineVariant,
    surfaceContainerLowest = NeerDarkSurfaceContainerLowest,
    surfaceContainerLow = NeerDarkSurfaceContainerLow,
    surfaceContainer = NeerDarkSurfaceContainer,
    surfaceContainerHigh = NeerDarkSurfaceContainerHigh,
    surfaceContainerHighest = NeerDarkSurfaceContainerHighest,
    inverseSurface = NeerDarkInverseSurface,
    inverseOnSurface = NeerDarkInverseOnSurface,
    inversePrimary = NeerDarkInversePrimary,
    scrim = NeerDarkScrim,
    surfaceTint = NeerDarkSurfaceTint,
)

@Composable
fun NeerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+; off by default so our brand colors win
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
