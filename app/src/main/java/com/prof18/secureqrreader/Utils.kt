package com.prof18.secureqrreader

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Patterns
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

fun isUrl(qrResult: String?): Boolean {
    val url = qrResult ?: return false
    return Patterns.WEB_URL.matcher(url).matches()
}

fun openUrl(qrResult: String?, context: Context) {
    val url = qrResult ?: return
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(browserIntent)
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