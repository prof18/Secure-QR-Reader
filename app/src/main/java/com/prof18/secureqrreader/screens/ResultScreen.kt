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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Margins.regular),
            contentAlignment = Alignment.Center,
        ) {
            if (scanResult == null) {
                Column {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(string.unespected_error),
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onBackground,
                    )
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Margins.big),
                        onClick = onScanAnotherButtonClick
                    ) {
                        Text(
                            text = stringResource(id = string.qr_result_scan_another)
                        )
                    }
                }
            } else {
                Column {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(align = Alignment.CenterHorizontally),
                        text = scanResult,
                        color = MaterialTheme.colors.onBackground,
                        style = MaterialTheme.typography.body1,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Margins.big),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            modifier = Modifier,
                            onClick = onOpenButtonClick,
                            enabled = isUrl,
                        ) {
                            Text(
                                text = stringResource(id = string.qr_result_open)
                            )
                        }
                        Button(
                            modifier = Modifier,
                            onClick = onCopyButtonClick
                        ) {
                            Text(
                                text = stringResource(id = string.qr_result_copy)
                            )
                        }
                        Button(
                            modifier = Modifier,
                            onClick = onShareButtonClick
                        ) {
                            Text(
                                text = stringResource(id = string.qr_result_share)
                            )
                        }
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Margins.big),
                        onClick = onScanAnotherButtonClick
                    ) {
                        Text(
                            text = stringResource(id = string.qr_result_scan_another)
                        )
                    }
                }
            }
        }
    }
}

@Preview
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
