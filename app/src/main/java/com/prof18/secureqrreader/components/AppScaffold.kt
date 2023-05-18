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

package com.prof18.secureqrreader.components

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.Icons.Outlined
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.prof18.secureqrreader.R
import com.prof18.secureqrreader.R.string
import com.prof18.secureqrreader.hasFlash
import com.prof18.secureqrreader.style.toolbarColor

@Composable
fun AboutScreenScaffold(
    onBackClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) = AppScaffold(
    showBackButton = true,
    showToolbarActions = false,
    showFlashSelector = false,
    setFlashOn = { },
    setFlashOff = { },
    onAboutClick = { },
    onBackClick = onBackClick,
    content = content
)

@Composable
fun ScanScreenWithoutCameraScaffold(
    onAboutClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) = AppScaffold(
    showBackButton = false,
    showToolbarActions = true,
    showFlashSelector = false,
    setFlashOn = { },
    setFlashOff = { },
    onAboutClick = onAboutClick,
    onBackClick = {},
    content = content
)

@Composable
fun ResultScreenScaffold(
    onBackClick: () -> Unit,
    onAboutClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) = AppScaffold(
    showBackButton = true,
    showToolbarActions = true,
    showFlashSelector = false,
    setFlashOn = { },
    setFlashOff = { },
    onAboutClick = onAboutClick,
    onBackClick = onBackClick,
    content = content
)

@Composable
fun LibrariesScreenScaffold(
    onBackClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) = AppScaffold(
    title = stringResource(id = R.string.open_source_licenses),
    showBackButton = true,
    showToolbarActions = false,
    showFlashSelector = false,
    setFlashOn = { },
    setFlashOff = { },
    onAboutClick = {},
    onBackClick = onBackClick,
    content = content
)

@Composable
private fun AppScaffold(
    title: String = stringResource(id = R.string.app_name),
    showBackButton: Boolean,
    showFlashSelector: Boolean,
    showToolbarActions: Boolean,
    setFlashOn: () -> Unit,
    setFlashOff: () -> Unit,
    onAboutClick: () -> Unit,
    onBackClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            NavigationBar(
                Modifier
                    .padding(WindowInsets.statusBars.asPaddingValues()),
                title = title,
                backgroundColor = toolbarColor(),
                showBackButton = showBackButton,
                onBackClick = onBackClick,
                showToolbarActions = showToolbarActions,
                showFlashSelector = showFlashSelector,
                setFlashOff = setFlashOff,
                setFlashOn = setFlashOn,
                onAboutClick = onAboutClick
            )
        },
        content = { content(it) }
    )
}
