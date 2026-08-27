/*
 * Copyright 2026 Marco Gomiero
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

import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.prof18.secureqrreader.R
import com.prof18.secureqrreader.style.SecureQrReaderTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ScanScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun permissionDenied_largeTextKeepsSettingsButtonReachable() {
        var settingsRequested = false

        composeTestRule.setContent {
            SecureQrReaderTheme {
                val currentDensity = LocalDensity.current
                CompositionLocalProvider(
                    LocalDensity provides Density(
                        density = currentDensity.density,
                        fontScale = 2f,
                    ),
                ) {
                    PermissionDeniedContent(
                        modifier = Modifier.requiredSize(width = 320.dp, height = 180.dp),
                        onRequestPermission = { settingsRequested = true },
                    )
                }
            }
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        composeTestRule
            .onNodeWithText(context.getString(R.string.request_permission))
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()
        composeTestRule.runOnIdle {
            assertTrue(settingsRequested)
        }
    }
}
