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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.prof18.secureqrreader.R.string
import com.prof18.secureqrreader.components.ResultScreenScaffold
import com.prof18.secureqrreader.style.Margins
import com.prof18.secureqrreader.style.SecureQrReaderTheme

@Composable
fun ResultScreen(
    scanResult: String? = null,
    isUrl: Boolean = false,
    onOpenButtonClick: () -> Unit = {},
    onCopyButtonClick: () -> Unit = {},
    onShareButtonClick: () -> Unit = {},
    onScanAnotherButtonClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    ResultScreenScaffold(
        onAboutClick = onAboutClick,
        onBackClick = onBackClick,
    ) {
        val configuration = LocalConfiguration.current
        if (scanResult == null) {
            when (configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    ResultNullLandscapeView(onScanAnotherButtonClick)
                }
                else -> {
                    ResultNullPortraitView(onScanAnotherButtonClick)
                }
            }
        } else {
            when (configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    ResultLandscapeView(
                        scanResult = scanResult,
                        onOpenButtonClick = onOpenButtonClick,
                        isUrl = isUrl,
                        onCopyButtonClick = onCopyButtonClick,
                        onShareButtonClick = onShareButtonClick,
                        onScanAnotherButtonClick = onScanAnotherButtonClick,
                    )
                }
                else -> {
                    ResultPortraitView(
                        scanResult = scanResult,
                        onOpenButtonClick = onOpenButtonClick,
                        isUrl = isUrl,
                        onCopyButtonClick = onCopyButtonClick,
                        onShareButtonClick = onShareButtonClick,
                        onScanAnotherButtonClick = onScanAnotherButtonClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultPortraitView(
    scanResult: String,
    onOpenButtonClick: () -> Unit,
    isUrl: Boolean,
    onCopyButtonClick: () -> Unit,
    onShareButtonClick: () -> Unit,
    onScanAnotherButtonClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Margins.regular),
        contentAlignment = Alignment.Center,
    ) {
        Column {
            ScanResultText(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(align = Alignment.CenterHorizontally),
                text = scanResult
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Margins.big),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ScanResultButton(
                    isEnabled = isUrl,
                    text = stringResource(id = string.qr_result_open),
                    onClick = onOpenButtonClick
                )
                ScanResultButton(
                    text = stringResource(id = string.qr_result_copy),
                    onClick = onCopyButtonClick
                )
                ScanResultButton(
                    isEnabled = isUrl,
                    text = stringResource(id = string.qr_result_share),
                    onClick = onShareButtonClick
                )
            }
            ScanResultButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Margins.big),
                text = stringResource(id = string.qr_result_scan_another),
                onClick = onScanAnotherButtonClick
            )
        }
    }
}

@Composable
private fun ResultLandscapeView(
    scanResult: String,
    onOpenButtonClick: () -> Unit,
    isUrl: Boolean,
    onCopyButtonClick: () -> Unit,
    onShareButtonClick: () -> Unit,
    onScanAnotherButtonClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Margins.regular),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScanResultText(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
                    .wrapContentWidth(align = Alignment.CenterHorizontally),
                text = scanResult
            )
            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(top = Margins.medium),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ScanResultButton(
                        isEnabled = isUrl,
                        text = stringResource(id = string.qr_result_open),
                        onClick = onOpenButtonClick
                    )
                    ScanResultButton(
                        text = stringResource(id = string.qr_result_copy),
                        onClick = onCopyButtonClick
                    )
                    ScanResultButton(
                        isEnabled = isUrl,
                        text = stringResource(id = string.qr_result_share),
                        onClick = onShareButtonClick
                    )
                }
                ScanResultButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Margins.regular),
                    text = stringResource(id = string.qr_result_scan_another),
                    onClick = onScanAnotherButtonClick
                )
            }
        }
    }
}

@Composable
private fun ResultNullPortraitView(onScanAnotherButtonClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Margins.regular),
        contentAlignment = Alignment.Center,
    ) {
        Column {
            ScanResultText(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(string.unespected_error)
            )
            ScanResultButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Margins.big),
                text = stringResource(id = string.qr_result_scan_another),
                onClick = onScanAnotherButtonClick
            )
        }
    }
}

@Composable
private fun ResultNullLandscapeView(onScanAnotherButtonClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Margins.regular),
        contentAlignment = Alignment.Center,
    ) {
        Column {
            ScanResultText(
                text = stringResource(string.unespected_error)
            )
            ScanResultButton(
                modifier = Modifier.padding(top = Margins.regular),
                text = stringResource(id = string.qr_result_scan_another),
                onClick = onScanAnotherButtonClick
            )
        }
    }
}

@Composable
private fun ScanResultText(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.body1,
        color = MaterialTheme.colors.onBackground,
    )
}

@Composable
private fun ScanResultButton(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    text: String,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        enabled = isEnabled,
        onClick = onClick
    ) {
        Text(text)
    }
}

@Preview
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ResultScreenScanResultNullPreview() {
    SecureQrReaderTheme {
        Surface {
            ResultScreen(
                scanResult = null
            )
        }
    }
}

@Preview
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ResultScreenScanResultPreview() {
    SecureQrReaderTheme {
        Surface {
            ResultScreen(
                scanResult = "https://www.marcogomiero.com",
                isUrl = true
            )
        }
    }
}

@Preview
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ResultScreenScanResultNotUrlPreview() {
    SecureQrReaderTheme {
        Surface {
            ResultScreen(
                scanResult = "Marco Gomiero",
                isUrl = false
            )
        }
    }
}
