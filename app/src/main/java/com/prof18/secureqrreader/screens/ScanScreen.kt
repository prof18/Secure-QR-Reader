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
import android.provider.Settings
import android.view.View
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
import com.prof18.secureqrreader.components.ScanScreenWithoutCameraScaffold
import com.prof18.secureqrreader.getActivity
import com.prof18.secureqrreader.goToAppSettings
import com.prof18.secureqrreader.hasFlash
import com.prof18.secureqrreader.style.LightAppColors
import com.prof18.secureqrreader.style.Margins
import com.prof18.secureqrreader.style.SecureQrReaderTheme

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
            viewFinder.setLaserVisibility(false)
            viewFinder.visibility = View.GONE

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
            .background(ScanBackground)
            .fillMaxSize()
    ) {
        ScannerView(
            modifier = Modifier
                .fillMaxSize(),
            compoundBarcodeView = compoundBarcodeView
        )
        ViewfinderOverlay(frameSize = 236.dp, cornerLength = 42.dp, cornerRadius = 32.dp)
        ScanTopBar(
            setFlashOn = setFlashOn,
            setFlashOff = setFlashOff,
            onAboutClick = onAboutClick,
        )
        PortraitBottomContent(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ScanLandscapeView(
    compoundBarcodeView: CompoundBarcodeView,
    setFlashOn: () -> Unit,
    setFlashOff: () -> Unit,
    onAboutClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .background(ScanBackground)
            .fillMaxSize(),
    ) {
        ScannerView(
            modifier = Modifier.fillMaxSize(),
            compoundBarcodeView = compoundBarcodeView,
        )
        ViewfinderOverlay(frameSize = 210.dp, cornerLength = 38.dp, cornerRadius = 30.dp)
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = Margins.medium, top = Margins.small),
        ) {
            Text(
                text = stringResource(R.string.app_name),
                color = Color.White,
                style = MaterialTheme.typography.h6,
            )
            PrivacyChip(
                modifier = Modifier
                    .padding(top = Margins.small),
            )
        }
        ScanControlRail(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .systemBarsPadding()
                .padding(end = Margins.regular),
            setFlashOn = setFlashOn,
            setFlashOff = setFlashOff,
            onAboutClick = onAboutClick,
        )
        ScanHint(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 96.dp, vertical = Margins.regular),
        )
    }
}

@Composable
private fun ScanTopBar(
    setFlashOn: () -> Unit,
    setFlashOff: () -> Unit,
    onAboutClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(124.dp)
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.55f), Color.Transparent)
                )
            )
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = Margins.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.app_name),
                color = Color.White,
                style = MaterialTheme.typography.h6,
            )
            FlashControl(
                size = 42.dp,
                setFlashOn = setFlashOn,
                setFlashOff = setFlashOff,
            )
            Spacer(modifier = Modifier.size(Margins.small))
            CircularControl(
                size = 42.dp,
                onClick = onAboutClick,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(R.string.settings_about),
                    tint = Color.White,
                )
            }
        }
    }
}

@Composable
private fun PortraitBottomContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.64f))
                )
            )
            .navigationBarsPadding()
            .padding(horizontal = Margins.medium, vertical = Margins.big),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PrivacyChip()
        ScanHint(
            modifier = Modifier.padding(top = Margins.regular),
        )
    }
}

@Composable
private fun ScanControlRail(
    modifier: Modifier = Modifier,
    setFlashOn: () -> Unit,
    setFlashOff: () -> Unit,
    onAboutClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FlashControl(
            size = 46.dp,
            setFlashOn = setFlashOn,
            setFlashOff = setFlashOff,
        )
        Spacer(modifier = Modifier.height(14.dp))
        CircularControl(
            size = 46.dp,
            onClick = onAboutClick,
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(R.string.settings_about),
                tint = Color.White,
            )
        }
    }
}

@Composable
private fun FlashControl(
    size: Dp,
    setFlashOn: () -> Unit,
    setFlashOff: () -> Unit,
) {
    val context = LocalContext.current
    if (!hasFlash(context)) return

    var isFlashActive by remember { mutableStateOf(false) }
    CircularControl(
        size = size,
        onClick = {
            if (isFlashActive) {
                setFlashOff()
            } else {
                setFlashOn()
            }
            isFlashActive = !isFlashActive
        },
    ) {
        Icon(
            imageVector = if (isFlashActive) Icons.Filled.FlashOff else Icons.Filled.FlashOn,
            contentDescription = stringResource(
                if (isFlashActive) R.string.turn_off_flash else R.string.turn_on_flash
            ),
            tint = Color.White,
        )
    }
}

@Composable
private fun CircularControl(
    size: Dp,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.size(size),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.14f),
        elevation = 0.dp,
    ) {
        IconButton(onClick = onClick, content = content)
    }
}

@Composable
private fun PrivacyChip(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = ScanAccent.copy(alpha = 0.16f),
        shape = CircleShape,
        elevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(15.dp),
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = ScanAccent,
            )
            Text(
                modifier = Modifier.padding(start = 7.dp),
                text = stringResource(R.string.scan_privacy_chip),
                color = ScanAccent,
                style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

@Composable
private fun ViewfinderOverlay(
    frameSize: Dp,
    cornerLength: Dp,
    cornerRadius: Dp,
) {
    val context = LocalContext.current
    val animationsEnabled = remember(context) {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        ) > 0f
    }
    val scanProgress = if (animationsEnabled) {
        val transition = rememberInfiniteTransition(label = "scan line")
        val progress by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "scan progress",
        )
        progress
    } else {
        0.5f
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
        ) {
            val framePx = frameSize.toPx()
            val left = (size.width - framePx) / 2f
            val top = (size.height - framePx) / 2f
            drawRect(ScanDim)
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(framePx, framePx),
                cornerRadius = CornerRadius(cornerRadius.toPx()),
                blendMode = BlendMode.Clear,
            )
        }
        Canvas(
            modifier = Modifier
                .align(Alignment.Center)
                .size(frameSize),
        ) {
            val radius = cornerRadius.toPx()
            val leg = cornerLength.toPx()
            val strokeWidth = 4.dp.toPx()
            val maxX = size.width
            val maxY = size.height
            val paths = listOf(
                Path().apply {
                    moveTo(0f, leg)
                    lineTo(0f, radius)
                    quadraticBezierTo(0f, 0f, radius, 0f)
                    lineTo(leg, 0f)
                },
                Path().apply {
                    moveTo(maxX - leg, 0f)
                    lineTo(maxX - radius, 0f)
                    quadraticBezierTo(maxX, 0f, maxX, radius)
                    lineTo(maxX, leg)
                },
                Path().apply {
                    moveTo(0f, maxY - leg)
                    lineTo(0f, maxY - radius)
                    quadraticBezierTo(0f, maxY, radius, maxY)
                    lineTo(leg, maxY)
                },
                Path().apply {
                    moveTo(maxX - leg, maxY)
                    lineTo(maxX - radius, maxY)
                    quadraticBezierTo(maxX, maxY, maxX, maxY - radius)
                    lineTo(maxX, maxY - leg)
                },
            )
            paths.forEach { path ->
                drawPath(
                    path = path,
                    color = ScanAccent,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Square),
                )
            }

            val inset = 14.dp.toPx()
            val lineY = inset + scanProgress * (size.height - inset * 2f)
            val lineAlpha = when {
                !animationsEnabled -> 0.9f
                scanProgress < 0.12f -> scanProgress / 0.12f
                scanProgress > 0.88f -> (1f - scanProgress) / 0.12f
                else -> 1f
            }
            val lineBrush = Brush.horizontalGradient(
                listOf(Color.Transparent, ScanAccent.copy(alpha = lineAlpha), Color.Transparent),
                startX = inset,
                endX = size.width - inset,
            )
            drawLine(
                brush = lineBrush,
                start = Offset(inset, lineY),
                end = Offset(size.width - inset, lineY),
                strokeWidth = 11.dp.toPx(),
                alpha = 0.18f * lineAlpha,
                cap = StrokeCap.Round,
            )
            drawLine(
                brush = lineBrush,
                start = Offset(inset, lineY),
                end = Offset(size.width - inset, lineY),
                strokeWidth = 3.dp.toPx(),
                alpha = lineAlpha,
                cap = StrokeCap.Round,
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
) {
    Text(
        modifier = modifier,
        color = Color.White,
        text = stringResource(id = R.string.scan_instructions),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.subtitle1,
    )
}

private val ScanBackground = Color(0xFF0A0F0C)
private val ScanDim = Color(0x9E050907)
private val ScanAccent = LightAppColors.scanAccent

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
