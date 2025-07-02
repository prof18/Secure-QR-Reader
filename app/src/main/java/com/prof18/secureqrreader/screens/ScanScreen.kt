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

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.prof18.secureqrreader.R
import com.prof18.secureqrreader.R.string
import com.prof18.secureqrreader.components.ScanScreenNavigationBar
import com.prof18.secureqrreader.components.ScanScreenWithoutCameraScaffold
import com.prof18.secureqrreader.getActivity
import com.prof18.secureqrreader.goToAppSettings
import com.prof18.secureqrreader.style.Margins
import com.prof18.secureqrreader.style.SecureQrReaderTheme
import com.prof18.secureqrreader.style.toolbarColor

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(
    onResultFound: (String) -> Unit = { },
    onAboutClick: () -> Unit = { },
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val context = LocalContext.current

    val beepManager = BeepManager(context.getActivity())

    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    LaunchedEffect(key1 = Unit) {
        cameraPermissionState.launchPermissionRequest()
    }

    val compoundBarcodeView = remember {
        CompoundBarcodeView(context).apply {

            val formats = listOf(BarcodeFormat.QR_CODE)
            barcodeView.decoderFactory = DefaultDecoderFactory(formats)
            this.setStatusText("")

            decodeContinuous(object : BarcodeCallback {
                override fun barcodeResult(result: BarcodeResult?) {
                    result?.let {
                        beepManager.isBeepEnabled = false
                        beepManager.playBeepSoundAndVibrate()
                        onResultFound(it.text)
                    }
                }

                override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
                }
            })
        }
    }

    when (cameraPermissionState.status) {
        PermissionStatus.Granted -> {
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        compoundBarcodeView.resume()
                    } else if (event == Lifecycle.Event.ON_PAUSE) {
                        compoundBarcodeView.pause()
                    }
                }

                lifecycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            ScanView(compoundBarcodeView, onAboutClick)
        }

        is PermissionStatus.Denied -> {
            PermissionDeniedView(onAboutClick, context)
        }
    }
}

@Composable
private fun ScanView(
    compoundBarcodeView: CompoundBarcodeView,
    onAboutClick: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            ScanLandscapeView(
                compoundBarcodeView,
                setFlashOn = { compoundBarcodeView.setTorchOn() },
                setFlashOff = { compoundBarcodeView.setTorchOff() },
                onAboutClick = onAboutClick
            )
        }

        else -> {
            ScanPortraitView(
                compoundBarcodeView,
                setFlashOn = { compoundBarcodeView.setTorchOn() },
                setFlashOff = { compoundBarcodeView.setTorchOff() },
                onAboutClick = onAboutClick,
                )
        }
    }
}

@Composable
private fun ScanPortraitView(
    compoundBarcodeView: CompoundBarcodeView,
    setFlashOn: () -> Unit,
    setFlashOff: () -> Unit,
    onAboutClick: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ScannerView(
            modifier = Modifier
                .fillMaxSize(),
            compoundBarcodeView = compoundBarcodeView
        )

        Column {
            ScanScreenNavigationBar(
                modifier = Modifier
                    .padding(WindowInsets.statusBars.asPaddingValues()),
                backgroundColor = Color.Transparent,
                usePrimaryColor = true,
                setFlashOn = setFlashOn,
                setFlashOff = setFlashOff,
                onAboutClick = onAboutClick,
            )

            ScanHint(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
                    .padding(top = Margins.regular)
                    .padding(horizontal = Margins.regular),
                color = Color.White,
            )
        }
    }
}

@Composable
private fun ScanLandscapeView(
    compoundBarcodeView: CompoundBarcodeView,
    setFlashOn: () -> Unit,
    setFlashOff: () -> Unit,
    onAboutClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .navigationBarsPadding(),
    ) {
        ScanScreenNavigationBar(
            modifier = Modifier
                .padding(WindowInsets.statusBars.asPaddingValues()),
            backgroundColor = MaterialTheme.colors.background,
            setFlashOn = setFlashOn,
            setFlashOff = setFlashOff,
            onAboutClick = onAboutClick,
        )

        Row(
            modifier = Modifier
                .padding(WindowInsets.navigationBars.asPaddingValues()),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                ScanIllustration(
                    modifier = Modifier
                        .weight(1f)
                )
                ScanHint(
                    modifier = Modifier
                        .padding(top = Margins.regular)
                        .padding(horizontal = Margins.regular),
                    color = MaterialTheme.colors.onBackground
                )
            }
            ScannerView(
                modifier = Modifier
                    .weight(2f)
                    .padding(top = Margins.big, bottom = Margins.regular)
                    .padding(horizontal = Margins.regular),
                compoundBarcodeView = compoundBarcodeView
            )
        }
    }
}

@Composable
private fun PermissionDeniedView(onAboutClick: () -> Unit, context: Context) {
    ScanScreenWithoutCameraScaffold(
        onAboutClick = onAboutClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Text(
                    modifier = Modifier
                        .padding(horizontal = Margins.big),
                    text = stringResource(id = string.permission_required_dialog_title),
                    color = MaterialTheme.colors.onBackground,
                    style = MaterialTheme.typography.h1.copy(fontSize = 28.sp),
                )
                Text(
                    modifier = Modifier
                        .padding(horizontal = Margins.big)
                        .padding(top = Margins.medium),
                    text = stringResource(id = string.camera_permission_explanation),
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onBackground,
                )
                Button(
                    modifier = Modifier
                        .padding(horizontal = Margins.big)
                        .padding(top = Margins.regular),
                    onClick = {
                        goToAppSettings(context)
                    }
                ) {
                    Text(stringResource(string.request_permission))
                }
            }
        }
    }
}

@Composable
private fun ScannerView(
    modifier: Modifier,
    compoundBarcodeView: CompoundBarcodeView,
) {
    AndroidView(
        modifier = modifier,
        factory = { compoundBarcodeView }
    )
}

@Composable
private fun ScanHint(
    modifier: Modifier = Modifier,
    color: Color,
) {
    Text(
        modifier = modifier,
        color = color,
        text = stringResource(id = R.string.scan_instructions),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.body1,
    )
}

@Composable
private fun ScanIllustration(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.scanner_vector),
        contentDescription = null,
    )
}

@Preview
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ScanViewPreview() {
    val context = LocalContext.current
    SecureQrReaderTheme {
        Surface {
            ScanView(
                compoundBarcodeView = CompoundBarcodeView(context).apply { setStatusText("") },
                onAboutClick = {}

            )
        }
    }
}

@Preview
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PermissionDeniedViewPreview() {
    val context = LocalContext.current
    SecureQrReaderTheme {
        Surface {
            PermissionDeniedView(
                onAboutClick = { },
                context = context
            )
        }
    }
}
