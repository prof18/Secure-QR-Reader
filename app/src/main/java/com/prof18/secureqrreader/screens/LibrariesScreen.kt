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

package com.prof18.secureqrreader.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.prof18.secureqrreader.components.LibrariesScreenScaffold
import com.prof18.secureqrreader.style.Margins
import com.prof18.secureqrreader.style.SecureQrReaderTheme

@Composable
fun LibrariesScreen(
    onBackClick: () -> Unit,
) {
    SecureQrReaderTheme {
        LibrariesScreenScaffold(
            onBackClick = onBackClick
        ) { paddingValues ->
            val configuration = LocalConfiguration.current

            val padding = if (configuration.orientation   == Configuration.ORIENTATION_LANDSCAPE) {
                WindowInsets.displayCutout.asPaddingValues()
            } else {
                PaddingValues(0.dp)
            }
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(padding)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Margins.regular)
                        .wrapContentWidth(align = Alignment.CenterHorizontally),
                    text = "Vectors from freepik - it.freepik.com",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onBackground,
                )

                LibrariesContainer(
                    Modifier.fillMaxSize()
                        .navigationBarsPadding()
                )
            }
        }
    }
}