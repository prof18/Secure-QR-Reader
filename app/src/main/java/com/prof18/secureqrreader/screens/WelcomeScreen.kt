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

package com.prof18.secureqrreader.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prof18.secureqrreader.R
import com.prof18.secureqrreader.style.Margins
import com.prof18.secureqrreader.style.PillShape
import com.prof18.secureqrreader.style.SecureQrReaderTheme
import com.prof18.secureqrreader.style.customColors

@Composable
internal fun WelcomeScreen(
    onStartClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = Margins.medium)
            .padding(top = 12.dp, bottom = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            VaultLockup()
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.h4.copy(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.5).sp,
                ),
                textAlign = TextAlign.Center,
            )
            Text(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(0.88f),
                text = stringResource(R.string.welcome_tagline),
                color = MaterialTheme.customColors.onSurfaceVariant,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
            )
            Column(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .fillMaxWidth(),
            ) {
                PromiseRow(
                    icon = Icons.Outlined.VisibilityOff,
                    text = stringResource(R.string.welcome_promise_no_tracking),
                )
                Spacer(modifier = Modifier.height(14.dp))
                PromiseRow(
                    icon = Icons.Outlined.Lock,
                    text = stringResource(R.string.welcome_promise_no_permissions),
                )
                Spacer(modifier = Modifier.height(14.dp))
                PromiseRow(
                    icon = Icons.Outlined.PhoneAndroid,
                    text = stringResource(R.string.welcome_promise_on_device),
                )
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = PillShape,
            onClick = onStartClick,
        ) {
            Text(text = stringResource(R.string.welcome_start_scanning))
            Icon(
                modifier = Modifier
                    .padding(start = Margins.small)
                    .size(20.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun VaultLockup() {
    Box(modifier = Modifier.size(128.dp)) {
        Surface(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopStart),
            color = MaterialTheme.customColors.primaryContainer,
            shape = RoundedCornerShape(34.dp),
            elevation = 0.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    modifier = Modifier.size(58.dp),
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.customColors.onPrimaryContainer,
                )
            }
        }
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(50.dp),
            color = MaterialTheme.colors.surface,
            shape = CircleShape,
            elevation = 0.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(42.dp),
                    color = MaterialTheme.colors.primary,
                    shape = CircleShape,
                    elevation = 0.dp,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            modifier = Modifier.size(22.dp),
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PromiseRow(
    icon: ImageVector,
    text: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            color = MaterialTheme.customColors.promiseTile,
            shape = RoundedCornerShape(12.dp),
            elevation = 0.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    modifier = Modifier.size(21.dp),
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary,
                )
            }
        }
        Text(
            modifier = Modifier.padding(start = 14.dp),
            text = text,
            color = MaterialTheme.colors.onSurface,
            style = MaterialTheme.typography.body2.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WelcomeScreenPreview() {
    SecureQrReaderTheme {
        Surface {
            WelcomeScreen()
        }
    }
}
