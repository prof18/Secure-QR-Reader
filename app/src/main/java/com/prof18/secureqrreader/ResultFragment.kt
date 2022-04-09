/*
 * Copyright 2020 Marco Gomiero
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

package com.prof18.secureqrreader

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.composethemeadapter.MdcTheme

class ResultFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val qrResult: String? = arguments?.getString(QR_RESULT)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MdcTheme {
                    ResultScreen(
                        scanResult = qrResult,
                        isUrl = isUrl(qrResult),
                        onOpenButtonClick = { openUrl(qrResult) },
                        onCopyButtonClick = { copyToClipboard(qrResult) },
                        onShareButtonClick = { shareResult(qrResult) },
                        onScanAnotherButtonClick = { performAnotherScan() }
                    )
                }
            }
        }
    }

    private fun isUrl(qrResult: String?): Boolean {
        val url = qrResult ?: return false
        return Patterns.WEB_URL.matcher(url).matches()
    }

    private fun openUrl(qrResult: String?) {
        val url = qrResult ?: return
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun copyToClipboard(qrResult: String?) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE)
                as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("QR Result", qrResult)
        clipboard.setPrimaryClip(clip)
    }

    private fun shareResult(qrResult: String?) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, qrResult)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun performAnotherScan() {
        requireActivity().onBackPressed()
    }

    companion object {
        private const val QR_RESULT = "qr_result"
        const val RESULT_FRAGMENT_TAG = "Result Fragment TAG"

        fun create(qrResult: String): ResultFragment {
            val fragment = ResultFragment()
            val args = bundleOf(
                QR_RESULT to qrResult
            )
            fragment.arguments = args
            return fragment
        }
    }
}

@Composable
private fun ResultScreen(
    scanResult: String? = null,
    isUrl: Boolean = false,
    onOpenButtonClick: () -> Unit = {},
    onCopyButtonClick: () -> Unit = {},
    onShareButtonClick: () -> Unit = {},
    onScanAnotherButtonClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppMargins.regular),
        contentAlignment = Alignment.Center,
    ) {
        if (scanResult == null) {
            Column {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "Sorry, something unexpected happened. Please retry", // TODO: localise
                    style = MaterialTheme.typography.body1.copy(fontSize = 18.sp),
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AppMargins.big),
                    onClick = onScanAnotherButtonClick
                ) {
                    Text(
                        text = stringResource(id = R.string.qr_result_scan_another)
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
                    style = MaterialTheme.typography.body1.copy(fontSize = 18.sp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AppMargins.big),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier,
                        onClick = onOpenButtonClick,
                        enabled = isUrl,
                    ) {
                        Text(
                            text = stringResource(id = R.string.qr_result_open)
                        )
                    }
                    Button(
                        modifier = Modifier,
                        onClick = onCopyButtonClick
                    ) {
                        Text(
                            text = stringResource(id = R.string.qr_result_copy)
                        )
                    }
                    Button(
                        modifier = Modifier,
                        onClick = onShareButtonClick
                    ) {
                        Text(
                            text = stringResource(id = R.string.qr_result_share)
                        )
                    }
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AppMargins.big),
                    onClick = onScanAnotherButtonClick
                ) {
                    Text(
                        text = stringResource(id = R.string.qr_result_scan_another)
                    )
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ResultScreenScanResultNullPreview() {
    MdcTheme {
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
    MdcTheme {
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
    MdcTheme {
        Surface {
            ResultScreen(
                scanResult = "Marco Gomiero",
                isUrl = false
            )
        }
    }
}
