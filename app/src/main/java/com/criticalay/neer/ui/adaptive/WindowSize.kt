/*
 * Copyright (c) 2026 Ashish Yadav <mailtoashish693@gmail.com>
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

package com.criticalay.neer.ui.adaptive

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf

/**
 * Single source of truth for every adaptive decision in the app. Populated
 * once in [com.criticalay.neer.NeerActivity] via
 * [androidx.compose.material3.windowsizeclass.calculateWindowSizeClass] and
 * consumed from any composable via [currentWindowSizeClass].
 */
val LocalWindowSizeClass = compositionLocalOf<WindowSizeClass> {
    error("WindowSizeClass not provided — wrap the root content in CompositionLocalProvider.")
}

@Composable
@ReadOnlyComposable
fun currentWindowSizeClass(): WindowSizeClass = LocalWindowSizeClass.current

/**
 * True when the content should render as two side-by-side columns — any
 * "wider than a phone in portrait" window. Covers phone landscape with a
 * medium+ width, tablet portrait, and tablet landscape.
 */
@Composable
@ReadOnlyComposable
fun isExpandedWidth(): Boolean = currentWindowSizeClass().widthSizeClass >= WindowWidthSizeClass.Medium

/**
 * True when vertical space is tight — phone landscape, foldable compact, etc.
 * Used by screens to swap a tall hero for a horizontally-oriented variant.
 */
@Composable
@ReadOnlyComposable
fun isCompactHeight(): Boolean = currentWindowSizeClass().heightSizeClass == WindowHeightSizeClass.Compact
