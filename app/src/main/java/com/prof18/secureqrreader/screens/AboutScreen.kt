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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prof18.secureqrreader.R
import com.prof18.secureqrreader.components.AboutScreenScaffold
import com.prof18.secureqrreader.style.Margins
import com.prof18.secureqrreader.style.PillShape
import com.prof18.secureqrreader.style.SecureQrReaderTheme
import com.prof18.secureqrreader.style.customColors

@Composable
fun AboutScreen(
    onSupportClick: () -> Unit = {},
    showOnGithubClicked: () -> Unit = {},
    licensesClicked: () -> Unit = {},
    nameClicked: () -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    AboutScreenScaffold(onBackClick = onBackPressed) { scaffoldPadding ->
        AboutContent(
            scaffoldPadding = scaffoldPadding,
            onSupportClick = onSupportClick,
            showOnGithubClicked = showOnGithubClicked,
            licensesClicked = licensesClicked,
            nameClicked = nameClicked,
        )
    }
}

@Composable
private fun AboutContent(
    scaffoldPadding: PaddingValues,
    onSupportClick: () -> Unit,
    showOnGithubClicked: () -> Unit,
    licensesClicked: () -> Unit,
    nameClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(scaffoldPadding)
            .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Margins.medium)
                .padding(top = Margins.regular, bottom = Margins.medium),
        ) {
            Text(
                text = stringResource(R.string.about_headline),
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.h4.copy(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.3).sp,
                ),
            )
            Text(
                modifier = Modifier.padding(top = Margins.small),
                text = stringResource(R.string.about_paragraph_one),
                color = MaterialTheme.customColors.onSurfaceVariant,
                style = MaterialTheme.typography.body1.copy(fontSize = 15.sp),
            )
            Text(
                modifier = Modifier.padding(top = 14.dp),
                text = stringResource(R.string.about_paragraph_two),
                color = MaterialTheme.customColors.onSurfaceVariant,
                style = MaterialTheme.typography.body1.copy(fontSize = 15.sp),
            )
            FlowRow(
                modifier = Modifier.padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(Margins.small),
                verticalArrangement = Arrangement.spacedBy(Margins.small),
            ) {
                AboutChip(text = stringResource(R.string.about_chip_no_ads))
                AboutChip(text = stringResource(R.string.about_chip_no_trackers))
                AboutChip(text = stringResource(R.string.about_chip_on_device))
            }
            Button(
                modifier = Modifier
                    .padding(top = Margins.medium)
                    .fillMaxWidth()
                    .height(54.dp),
                shape = PillShape,
                onClick = onSupportClick,
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                )
                Text(
                    modifier = Modifier.padding(start = Margins.small),
                    text = stringResource(R.string.support_the_project),
                )
            }
            Button(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .height(54.dp),
                shape = PillShape,
                onClick = showOnGithubClicked,
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.ic_github),
                    contentDescription = null,
                )
                Text(
                    modifier = Modifier.padding(start = Margins.small),
                    text = stringResource(R.string.show_on_github),
                )
            }
            Button(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .height(54.dp),
                shape = PillShape,
                onClick = licensesClicked,
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null,
                )
                Text(
                    modifier = Modifier.padding(start = Margins.small),
                    text = stringResource(R.string.open_source_licenses),
                )
            }
        }
        AttributionFooter(nameClicked = nameClicked)
    }
}

@Composable
private fun AboutChip(text: String) {
    Surface(
        color = MaterialTheme.customColors.primaryContainer,
        shape = CircleShape,
        elevation = 0.dp,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp),
            text = text,
            color = MaterialTheme.customColors.onPrimaryContainer,
            style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
private fun AttributionFooter(nameClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Margins.medium)
            .padding(top = Margins.regular, bottom = 22.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.about_footer_prefix),
            color = MaterialTheme.customColors.onSurfaceVariant,
            style = MaterialTheme.typography.body2,
        )
        Text(
            modifier = Modifier.padding(horizontal = 4.dp),
            text = "♥︎",
            color = HeartColor,
            style = MaterialTheme.typography.body2.copy(fontFamily = FontFamily.SansSerif),
        )
        val connector = stringResource(R.string.about_footer_connector)
        if (connector.isNotBlank()) {
            Text(
                text = connector,
                color = MaterialTheme.customColors.onSurfaceVariant,
                style = MaterialTheme.typography.body2,
            )
            Spacer(modifier = Modifier.size(4.dp))
        }
        Text(
            modifier = Modifier.clickable(onClick = nameClicked),
            text = "Marco Gomiero",
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

private val HeartColor = Color(0xFFE0245E)

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AboutScreenPreview() {
    SecureQrReaderTheme {
        Surface {
            AboutScreen()
        }
    }
}
