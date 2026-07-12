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

package com.prof18.secureqrreader

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.provider.ContactsContract
import android.provider.Settings

fun Context.getActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

fun hasFlash(context: Context): Boolean =
    context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

fun goToAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

fun openUrl(qrResult: String?, context: Context) {
    val url = qrResult ?: return
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(browserIntent)
}

fun connectToWifi(
    ssid: String,
    security: String,
    password: String?,
    context: Context,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !security.equals("WEP", true)) {
        val suggestion = runCatching {
            WifiNetworkSuggestion.Builder()
                .setSsid(ssid)
                .apply {
                    if (password != null && !security.equals("Open", true)) {
                        setWpa2Passphrase(password)
                    }
                }
                .build()
        }.getOrNull()
        if (suggestion != null) {
            val addNetworkIntent = Intent(Settings.ACTION_WIFI_ADD_NETWORKS).apply {
                putParcelableArrayListExtra(
                    Settings.EXTRA_WIFI_NETWORK_LIST,
                    arrayListOf(suggestion),
                )
            }
            val activity = context.getActivity()
            if (
                activity != null &&
                runCatching {
                    @Suppress("DEPRECATION")
                    activity.startActivityForResult(addNetworkIntent, WIFI_NETWORK_REQUEST_CODE)
                }.isSuccess
            ) {
                return
            }
        }
    }

    context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
}

internal const val WIFI_NETWORK_REQUEST_CODE = 1_001

fun addContact(
    name: String,
    phone: String?,
    email: String?,
    context: Context,
) {
    val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
        type = ContactsContract.Contacts.CONTENT_TYPE
        putExtra(ContactsContract.Intents.Insert.NAME, name)
        phone?.let { putExtra(ContactsContract.Intents.Insert.PHONE, it) }
        email?.let { putExtra(ContactsContract.Intents.Insert.EMAIL, it) }
    }
    context.startActivity(intent)
}

fun copyToClipboard(qrResult: String?, context: Context) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE)
        as ClipboardManager
    val clip: ClipData = ClipData.newPlainText("QR Result", qrResult)
    clipboard.setPrimaryClip(clip)
}

fun shareResult(qrResult: String?, context: Context) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, qrResult)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}
