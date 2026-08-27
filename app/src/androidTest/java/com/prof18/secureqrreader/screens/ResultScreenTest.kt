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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.prof18.secureqrreader.R
import com.prof18.secureqrreader.style.SecureQrReaderTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ResultScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun longUrl_keepsActionsReachable() {
        val longUrl = "https://www.linkedin.com/checkpoint/challenge/" +
            "account-verification-token-0123456789".repeat(30)
        var openRequested = false

        composeTestRule.setContent {
            SecureQrReaderTheme {
                Box(modifier = Modifier.requiredSize(360.dp)) {
                    ResultPortraitView(
                        scaffoldPadding = PaddingValues(0.dp),
                        content = parseQrContent(longUrl),
                        onOpenButtonClick = { openRequested = true },
                        onConnectButtonClick = { _, _, _ -> },
                        onAddContactButtonClick = { _, _, _ -> },
                        onCopyButtonClick = {},
                        onShareButtonClick = {},
                        onScanAnotherButtonClick = {},
                    )
                }
            }
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val scanAnotherButton = composeTestRule
            .onNodeWithText(context.getString(R.string.qr_result_scan_another))
        val openLinkButton = composeTestRule
            .onNodeWithText(context.getString(R.string.qr_action_open_link))

        scanAnotherButton
            .assertIsDisplayed()
        openLinkButton
            .performScrollTo()
            .assertIsDisplayed()
        val buttonGap = scanAnotherButton.getUnclippedBoundsInRoot().top -
            openLinkButton.getUnclippedBoundsInRoot().bottom
        assertTrue("Expected at least 16dp between result actions", buttonGap >= 16.dp)
        openLinkButton.performClick()
        composeTestRule.runOnIdle {
            assertTrue(openRequested)
        }
    }

    @Test
    fun longUrlLandscape_keepsContentReachable() {
        val longUrl = "https://www.linkedin.com/checkpoint/challenge/" +
            "account-verification-token-0123456789".repeat(30)

        composeTestRule.setContent {
            SecureQrReaderTheme {
                Box(modifier = Modifier.requiredSize(width = 400.dp, height = 180.dp)) {
                    ResultLandscapeView(
                        scaffoldPadding = PaddingValues(0.dp),
                        content = parseQrContent(longUrl),
                        onOpenButtonClick = {},
                        onConnectButtonClick = { _, _, _ -> },
                        onAddContactButtonClick = { _, _, _ -> },
                        onCopyButtonClick = {},
                        onShareButtonClick = {},
                        onScanAnotherButtonClick = {},
                    )
                }
            }
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        composeTestRule
            .onNodeWithText(context.getString(R.string.qr_link_safety_title))
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun shortLandscape_keepsScanAnotherReachable() {
        var scanAnotherRequested = false

        composeTestRule.setContent {
            SecureQrReaderTheme {
                Box(modifier = Modifier.requiredSize(width = 400.dp, height = 180.dp)) {
                    ResultLandscapeView(
                        scaffoldPadding = PaddingValues(0.dp),
                        content = parseQrContent("https://www.marcogomiero.com"),
                        onOpenButtonClick = {},
                        onConnectButtonClick = { _, _, _ -> },
                        onAddContactButtonClick = { _, _, _ -> },
                        onCopyButtonClick = {},
                        onShareButtonClick = {},
                        onScanAnotherButtonClick = { scanAnotherRequested = true },
                    )
                }
            }
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        composeTestRule
            .onNodeWithText(context.getString(R.string.qr_result_scan_another))
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()
        composeTestRule.runOnIdle {
            assertTrue(scanAnotherRequested)
        }
    }
}
