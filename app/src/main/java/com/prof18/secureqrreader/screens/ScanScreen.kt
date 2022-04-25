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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
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
import com.prof18.secureqrreader.R.drawable
import com.prof18.secureqrreader.R.string
import com.prof18.secureqrreader.components.ScanScreenScaffold
import com.prof18.secureqrreader.components.ScanScreenWithoutCameraScaffold
import com.prof18.secureqrreader.getActivity
import com.prof18.secureqrreader.goToAppSettings
import com.prof18.secureqrreader.style.Margins

@Composable
fun ScanScreen(
    onResultFound: (String) -> Unit,
    onAboutClick: () -> Unit,
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

            ScanScreenScaffold(
                setFlashOn = { compoundBarcodeView.setTorchOn() },
                setFlashOff = { compoundBarcodeView.setTorchOff() },
                onAboutClick = onAboutClick
            ) {
                Column {
                    Image(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .wrapContentWidth(align = Alignment.CenterHorizontally),
                        painter = painterResource(id = drawable.scanner_vector),
                        contentDescription = null,
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(align = Alignment.CenterHorizontally)
                            .padding(top = Margins.regular)
                            .padding(horizontal = Margins.regular),
                        color = MaterialTheme.colors.onBackground,
                        text = stringResource(id = string.scan_instructions),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                    )
                    AndroidView(
                        modifier = Modifier
                            .weight(2f)
                            .padding(top = Margins.big, bottom = Margins.regular)
                            .padding(horizontal = Margins.regular),
                        factory = { compoundBarcodeView }
                    )
                }
            }
        }

        is PermissionStatus.Denied -> {

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
                            text = stringResource(id = R.string.permission_required_dialog_title),
                            color = MaterialTheme.colors.onBackground,
                            style = MaterialTheme.typography.h1.copy(fontSize = 28.sp),
                        )
                        Text(
                            modifier = Modifier
                                .padding(horizontal = Margins.big)
                                .padding(top = Margins.medium),
                            text = stringResource(id = R.string.camera_permission_explanation),
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
    }
}

