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

import android.Manifest
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.prof18.secureqrreader.style.Margins
import com.prof18.secureqrreader.style.SecureQrReaderTheme
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class ScanFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private lateinit var beepManager: BeepManager
    private lateinit var scannerView: DecoratedBarcodeView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val root = layoutInflater.inflate(R.layout.layout_barcode_scanner, null)
        scannerView = root.findViewById(R.id.QRScannerView)
        val formats = listOf(BarcodeFormat.QR_CODE)
        beepManager = BeepManager(requireActivity())
        scannerView.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        scannerView.setStatusText("")

        scannerView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    beepManager.isBeepEnabled = false
                    beepManager.playBeepSoundAndVibrate()

                    requireActivity().supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.fragmentContainer,
                            ResultFragment.create(result.text),
                            ResultFragment.RESULT_FRAGMENT_TAG
                        )
                        .addToBackStack(null)
                        .commit()
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
            }
        })

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SecureQrReaderTheme {
                    ScanScreen(root)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissionsAndStartQRScan()
    }

    @AfterPermissionGranted(RC_CAMERA)
    private fun checkPermissionsAndStartQRScan() {
        val permission = Manifest.permission.CAMERA
        if (EasyPermissions.hasPermissions(requireContext(), permission)) {
            // Already have permission, do the thing
            openScanner()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this, getString(R.string.camera_permission_explanation),
                RC_CAMERA, permission
            )
        }
    }

    override fun onPause() {
        super.onPause()
        scannerView.pause()
    }

    override fun onResume() {
        super.onResume()
        scannerView.resume()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this,
        )
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.permission_required_dialog_title))
            .setMessage(getString(R.string.permission_required_dialog_content))
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.dismiss()
                checkPermissionsAndStartQRScan()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        openScanner()
    }

    private fun openScanner() {
        scannerView.resume()
    }

    fun enableFlash(enable: Boolean) {
        if (enable) {
            scannerView.setTorchOn();
        } else {
            scannerView.setTorchOff()
        }
    }

    companion object {
        const val RC_CAMERA = 1
        const val SCANNER_FRAGMENT_TAG = "Scanner Fragment TAG"
    }
}

@Composable
private fun ScanScreen(
    root: View,
) {
    Column {
        Image(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .wrapContentWidth(align = Alignment.CenterHorizontally),
            painter = painterResource(id = R.drawable.scanner_vector),
            contentDescription = null,
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(align = Alignment.CenterHorizontally)
                .padding(top = Margins.regular)
                .padding(horizontal = Margins.regular),
            color = MaterialTheme.colors.onBackground,
            text = stringResource(id = R.string.scan_instructions),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1,
        )
        AndroidView(
            modifier = Modifier
                .weight(2f)
                .padding(top = Margins.big, bottom = Margins.regular)
                .padding(horizontal = Margins.regular),
            factory = { root }
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ScanScreenPreview() {
    SecureQrReaderTheme {
        Surface {
            ScanScreen(
                root = LinearLayout(LocalContext.current)
            )
        }
    }
}
