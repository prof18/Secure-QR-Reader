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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prof18.secureqrreader.R
import com.prof18.secureqrreader.components.ResultScreenScaffold
import com.prof18.secureqrreader.style.CardShape
import com.prof18.secureqrreader.style.Margins
import com.prof18.secureqrreader.style.PillShape
import com.prof18.secureqrreader.style.SecureQrReaderTheme
import com.prof18.secureqrreader.style.customColors

@Composable
fun ResultScreen(
    scanResult: String? = null,
    onOpenButtonClick: (String) -> Unit = {},
    onConnectButtonClick: (String, String, String?) -> Unit = { _, _, _ -> },
    onAddContactButtonClick: (String, String?, String?) -> Unit = { _, _, _ -> },
    onCopyButtonClick: (String) -> Unit = {},
    onShareButtonClick: (String) -> Unit = {},
    onScanAnotherButtonClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    ResultScreenScaffold(
        onAboutClick = onAboutClick,
        onBackClick = onBackClick,
    ) { scaffoldPadding ->
        val content = scanResult?.let(::parseQrContent)
        val orientation = LocalConfiguration.current.orientation
        when {
            content == null -> ResultErrorView(
                scaffoldPadding = scaffoldPadding,
                onScanAnotherButtonClick = onScanAnotherButtonClick,
            )
            orientation == Configuration.ORIENTATION_LANDSCAPE -> ResultLandscapeView(
                scaffoldPadding = scaffoldPadding,
                content = content,
                onOpenButtonClick = onOpenButtonClick,
                onConnectButtonClick = onConnectButtonClick,
                onAddContactButtonClick = onAddContactButtonClick,
                onCopyButtonClick = onCopyButtonClick,
                onShareButtonClick = onShareButtonClick,
                onScanAnotherButtonClick = onScanAnotherButtonClick,
            )
            else -> ResultPortraitView(
                scaffoldPadding = scaffoldPadding,
                content = content,
                onOpenButtonClick = onOpenButtonClick,
                onConnectButtonClick = onConnectButtonClick,
                onAddContactButtonClick = onAddContactButtonClick,
                onCopyButtonClick = onCopyButtonClick,
                onShareButtonClick = onShareButtonClick,
                onScanAnotherButtonClick = onScanAnotherButtonClick,
            )
        }
    }
}

@Composable
private fun ResultPortraitView(
    scaffoldPadding: PaddingValues,
    content: QrContent,
    onOpenButtonClick: (String) -> Unit,
    onConnectButtonClick: (String, String, String?) -> Unit,
    onAddContactButtonClick: (String, String?, String?) -> Unit,
    onCopyButtonClick: (String) -> Unit,
    onShareButtonClick: (String) -> Unit,
    onScanAnotherButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(scaffoldPadding)
            .navigationBarsPadding()
            .padding(horizontal = Margins.medium)
            .padding(top = Margins.small, bottom = Margins.regular),
    ) {
        TypeBadge(content)
        Spacer(modifier = Modifier.height(Margins.regular))
        ResultContentCard(content)
        Spacer(modifier = Modifier.height(Margins.medium))
        ResultActions(
            content = content,
            onOpenButtonClick = onOpenButtonClick,
            onConnectButtonClick = onConnectButtonClick,
            onAddContactButtonClick = onAddContactButtonClick,
            onCopyButtonClick = onCopyButtonClick,
            onShareButtonClick = onShareButtonClick,
        )
        Spacer(modifier = Modifier.weight(1f))
        ScanAnotherButton(onClick = onScanAnotherButtonClick)
    }
}

@Composable
private fun ResultLandscapeView(
    scaffoldPadding: PaddingValues,
    content: QrContent,
    onOpenButtonClick: (String) -> Unit,
    onConnectButtonClick: (String, String, String?) -> Unit,
    onAddContactButtonClick: (String, String?, String?) -> Unit,
    onCopyButtonClick: (String) -> Unit,
    onShareButtonClick: (String) -> Unit,
    onScanAnotherButtonClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(scaffoldPadding)
            .padding(WindowInsets.displayCutout.asPaddingValues())
            .navigationBarsPadding()
            .padding(horizontal = Margins.medium, vertical = Margins.regular),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
        ) {
            TypeBadge(content)
            Spacer(modifier = Modifier.height(12.dp))
            ResultContentCard(content)
        }
        Spacer(modifier = Modifier.width(22.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
        ) {
            ResultActions(
                content = content,
                onOpenButtonClick = onOpenButtonClick,
                onConnectButtonClick = onConnectButtonClick,
                onAddContactButtonClick = onAddContactButtonClick,
                onCopyButtonClick = onCopyButtonClick,
                onShareButtonClick = onShareButtonClick,
            )
            Spacer(modifier = Modifier.height(Margins.regular))
            ScanAnotherButton(onClick = onScanAnotherButtonClick)
        }
    }
}

@Composable
private fun TypeBadge(content: QrContent) {
    val (icon, label) = when (content) {
        is QrContent.Url -> Icons.Outlined.Link to stringResource(R.string.qr_type_link)
        is QrContent.Wifi -> Icons.Outlined.Wifi to stringResource(R.string.qr_type_wifi)
        is QrContent.ContactInfo -> Icons.Outlined.Person to stringResource(R.string.qr_type_contact)
        is QrContent.PlainText -> Icons.Outlined.TextFields to stringResource(R.string.qr_type_text)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(44.dp),
            color = MaterialTheme.customColors.primaryContainer,
            shape = RoundedCornerShape(14.dp),
            elevation = 0.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    modifier = Modifier.size(23.dp),
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.customColors.onPrimaryContainer,
                )
            }
        }
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = label,
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = stringResource(R.string.qr_detected_now),
                color = MaterialTheme.customColors.onSurfaceVariant,
                style = MaterialTheme.typography.caption,
            )
        }
    }
}

@Composable
private fun ResultContentCard(content: QrContent) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.customColors.surfaceVariant,
        shape = CardShape,
        elevation = 0.dp,
    ) {
        when (content) {
            is QrContent.Url -> UrlCard(content)
            is QrContent.Wifi -> WifiCard(content)
            is QrContent.ContactInfo -> ContactCard(content)
            is QrContent.PlainText -> PlainTextCard(content)
        }
    }
}

@Composable
private fun UrlCard(content: QrContent.Url) {
    Column(modifier = Modifier.padding(20.dp)) {
        ResultValue(text = content.raw)
        CardDivider()
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(18.dp),
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = MaterialTheme.customColors.caution,
            )
            Column(modifier = Modifier.padding(start = Margins.small)) {
                Text(
                    text = content.host,
                    color = MaterialTheme.customColors.cautionText,
                    style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.SemiBold),
                )
                Text(
                    modifier = Modifier.padding(top = 3.dp),
                    text = stringResource(R.string.qr_link_verify_note),
                    color = MaterialTheme.customColors.cautionText,
                    style = MaterialTheme.typography.caption,
                )
            }
        }
    }
}

@Composable
private fun WifiCard(content: QrContent.Wifi) {
    var isPasswordVisible by remember(content) { mutableStateOf(false) }
    Column(modifier = Modifier.padding(20.dp)) {
        ResultValue(text = content.ssid)
        CardDivider()
        DetailRow(
            label = stringResource(R.string.qr_wifi_security),
            value = content.security,
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.qr_wifi_password),
                    color = MaterialTheme.customColors.onSurfaceVariant,
                    style = MaterialTheme.typography.caption,
                )
                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = when {
                        content.password == null -> stringResource(R.string.qr_wifi_no_password)
                        isPasswordVisible -> content.password
                        else -> "••••••••"
                    },
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.body1,
                )
            }
            if (content.password != null) {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) {
                            Icons.Outlined.VisibilityOff
                        } else {
                            Icons.Outlined.Visibility
                        },
                        contentDescription = stringResource(
                            if (isPasswordVisible) {
                                R.string.qr_hide_password
                            } else {
                                R.string.qr_show_password
                            }
                        ),
                        tint = MaterialTheme.customColors.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactCard(content: QrContent.ContactInfo) {
    Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            color = MaterialTheme.colors.primary,
            shape = CircleShape,
            elevation = 0.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = initials(content.name),
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }
        ResultValue(
            modifier = Modifier.padding(top = 12.dp),
            text = content.name,
            textAlign = TextAlign.Center,
        )
        CardDivider()
        content.phone?.let {
            DetailRow(label = stringResource(R.string.qr_contact_phone), value = it)
        }
        if (content.phone != null && content.email != null) {
            Spacer(modifier = Modifier.height(12.dp))
        }
        content.email?.let {
            DetailRow(label = stringResource(R.string.qr_contact_email), value = it)
        }
    }
}

@Composable
private fun PlainTextCard(content: QrContent.PlainText) {
    ResultValue(
        modifier = Modifier.padding(20.dp),
        text = content.raw,
    )
}

@Composable
private fun ResultValue(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
) {
    Text(
        modifier = modifier.fillMaxWidth(),
        text = text,
        color = MaterialTheme.colors.onSurface,
        style = MaterialTheme.typography.h5.copy(
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        textAlign = textAlign,
    )
}

@Composable
private fun CardDivider() {
    Divider(
        modifier = Modifier.padding(vertical = Margins.regular),
        color = MaterialTheme.customColors.divider,
        thickness = 1.dp,
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            modifier = Modifier.weight(0.42f),
            text = label,
            color = MaterialTheme.customColors.onSurfaceVariant,
            style = MaterialTheme.typography.caption,
        )
        Text(
            modifier = Modifier.weight(0.58f),
            text = value,
            color = MaterialTheme.colors.onSurface,
            style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
private fun ResultActions(
    content: QrContent,
    onOpenButtonClick: (String) -> Unit,
    onConnectButtonClick: (String, String, String?) -> Unit,
    onAddContactButtonClick: (String, String?, String?) -> Unit,
    onCopyButtonClick: (String) -> Unit,
    onShareButtonClick: (String) -> Unit,
) {
    val primaryLabel: String
    val primaryIcon: ImageVector
    val primaryAction: () -> Unit
    when (content) {
        is QrContent.Url -> {
            primaryLabel = stringResource(R.string.qr_action_open_link)
            primaryIcon = Icons.Filled.OpenInNew
            primaryAction = { onOpenButtonClick(content.openableUrl) }
        }
        is QrContent.Wifi -> {
            primaryLabel = stringResource(R.string.qr_action_connect)
            primaryIcon = Icons.Filled.Wifi
            primaryAction = {
                onConnectButtonClick(content.ssid, content.security, content.password)
            }
        }
        is QrContent.ContactInfo -> {
            primaryLabel = stringResource(R.string.qr_action_add_contact)
            primaryIcon = Icons.Filled.PersonAdd
            primaryAction = {
                onAddContactButtonClick(content.name, content.phone, content.email)
            }
        }
        is QrContent.PlainText -> {
            primaryLabel = stringResource(R.string.qr_result_copy)
            primaryIcon = Icons.Filled.ContentCopy
            primaryAction = { onCopyButtonClick(content.raw) }
        }
    }

    PrimaryActionButton(
        text = primaryLabel,
        icon = primaryIcon,
        onClick = primaryAction,
    )
    Spacer(modifier = Modifier.height(12.dp))
    if (content is QrContent.PlainText) {
        TonalActionButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.qr_result_share),
            icon = Icons.Filled.Share,
            onClick = { onShareButtonClick(content.raw) },
        )
    } else {
        Row(modifier = Modifier.fillMaxWidth()) {
            val copyValue = if (content is QrContent.Wifi) content.password else content.raw
            TonalActionButton(
                modifier = Modifier.weight(1f),
                text = stringResource(
                    if (content is QrContent.Wifi) {
                        R.string.qr_action_copy_password
                    } else {
                        R.string.qr_result_copy
                    }
                ),
                icon = Icons.Filled.ContentCopy,
                enabled = copyValue != null,
                onClick = { copyValue?.let(onCopyButtonClick) },
            )
            Spacer(modifier = Modifier.width(12.dp))
            TonalActionButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.qr_result_share),
                icon = Icons.Filled.Share,
                onClick = { onShareButtonClick(content.raw) },
            )
        }
    }
}

@Composable
private fun PrimaryActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = PillShape,
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = icon,
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(start = Margins.small),
            text = text,
        )
    }
}

@Composable
private fun TonalActionButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier.height(48.dp),
        enabled = enabled,
        shape = PillShape,
        contentPadding = PaddingValues(horizontal = 12.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.customColors.primaryContainer,
            contentColor = MaterialTheme.customColors.onPrimaryContainer,
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp,
        ),
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier.size(17.dp),
            imageVector = icon,
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(start = 6.dp),
            text = text,
            style = MaterialTheme.typography.button.copy(
                fontSize = 14.sp,
                letterSpacing = 0.25.sp,
            ),
            maxLines = 1,
        )
    }
}

@Composable
private fun ScanAnotherButton(onClick: () -> Unit) {
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = PillShape,
        border = BorderStroke(1.5.dp, MaterialTheme.customColors.outline),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.primary),
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier.size(18.dp),
            imageVector = Icons.Filled.Apps,
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(start = Margins.small),
            text = stringResource(R.string.qr_result_scan_another),
        )
    }
}

@Composable
private fun ResultErrorView(
    scaffoldPadding: PaddingValues,
    onScanAnotherButtonClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(scaffoldPadding)
            .navigationBarsPadding()
            .padding(Margins.medium),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.unespected_error),
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(Margins.medium))
            ScanAnotherButton(onClick = onScanAnotherButtonClick)
        }
    }
}

private fun initials(name: String): String = name
    .split(Regex("\\s+"))
    .filter(String::isNotBlank)
    .take(2)
    .mapNotNull(String::firstOrNull)
    .joinToString("")
    .uppercase()

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ResultLinkPreview() {
    SecureQrReaderTheme {
        Surface {
            ResultScreen(scanResult = "https://www.marcogomiero.com")
        }
    }
}

@Preview
@Composable
private fun ResultWifiPreview() {
    SecureQrReaderTheme {
        Surface {
            ResultScreen(scanResult = "WIFI:T:WPA;S:Gomiero_Home;P:supersecret;;")
        }
    }
}

@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Composable
private fun ResultLandscapePreview() {
    SecureQrReaderTheme {
        Surface {
            ResultScreen(scanResult = "https://www.marcogomiero.com")
        }
    }
}
