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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.prof18.secureqrreader.R.string
import com.prof18.secureqrreader.components.AboutScreenScaffold
import com.prof18.secureqrreader.style.Margins
import com.prof18.secureqrreader.style.SecureQrReaderTheme

@Composable
fun AboutScreen(
    showOnGithubClicked: () -> Unit = {},
    licensesClicked: () -> Unit = {},
    nameClicked: () -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    AboutScreenScaffold(
        onBackClick = onBackPressed,
    ) {
        val configuration = LocalConfiguration.current
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                LandscapeView(showOnGithubClicked, licensesClicked, nameClicked)
            }
            else -> {
                PortraitView(showOnGithubClicked, licensesClicked, nameClicked)
            }
        }
    }
}

@Composable
private fun LandscapeView(
    showOnGithubClicked: () -> Unit,
    licensesClicked: () -> Unit,
    nameClicked: () -> Unit,
) {
    Row {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            item {
                AboutTextItem(
                    modifier = Modifier.padding(Margins.regular),
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(2f)
        ) {
            item {
                AboutButtonItem(
                    onClick = showOnGithubClicked,
                    buttonText = stringResource(id = string.show_on_github)
                )
            }
            item {
                AboutButtonItem(
                    onClick = licensesClicked,
                    buttonText = stringResource(id = string.open_source_licenses)
                )
            }
            item {
                AuthorText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(align = Alignment.CenterHorizontally),
                    nameClicked = nameClicked
                )
            }
        }
    }
}

@Composable
private fun PortraitView(
    showOnGithubClicked: () -> Unit,
    licensesClicked: () -> Unit,
    nameClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            item {
                AboutTextItem(
                    modifier = Modifier.padding(Margins.regular),
                )
            }
            item {
                AboutButtonItem(
                    onClick = showOnGithubClicked,
                    buttonText = stringResource(id = string.show_on_github)
                )
            }
            item {
                AboutButtonItem(
                    onClick = licensesClicked,
                    buttonText = stringResource(id = string.open_source_licenses)
                )
            }
        }
        AuthorText(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            nameClicked = nameClicked,
        )
    }
}

@Composable
private fun AuthorText(nameClicked: () -> Unit, modifier: Modifier = Modifier) {
    AnnotatedClickableText(
        modifier = modifier
            .padding(Margins.big),
        onTextClick = nameClicked
    )
}

@Composable
private fun AboutButtonItem(
    onClick: () -> Unit,
    buttonText: String,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Margins.regular),
        onClick = onClick
    ) {
        Text(buttonText)
    }
}

@Composable
private fun AboutTextItem(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        color = MaterialTheme.colors.onBackground,
        text = stringResource(id = string.welcome_screen_content),
        style = MaterialTheme.typography.body1,
    )
}

@Composable
private fun AnnotatedClickableText(
    modifier: Modifier = Modifier,
    onTextClick: () -> Unit,
) {
    val annotatedText = buildAnnotatedString {
        append(stringResource(id = string.author_label))

        pushStringAnnotation(
            tag = "URL",
            annotation = "https://www.marcogomiero.com"
        )
        withStyle(
            style = SpanStyle(
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            )
        ) {
            append(" Marco Gomiero")
        }

        pop()
    }

    ClickableText(
        modifier = modifier,
        text = annotatedText,
        style = MaterialTheme.typography.body1.copy(
            color = MaterialTheme.colors.onBackground,
        ),
        onClick = { offset ->
            annotatedText.getStringAnnotations(
                tag = "URL",
                start = offset,
                end = offset,
            ).firstOrNull()?.let {
                onTextClick()
            }
        }
    )
}

@Preview
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AboutScreenPreview() {
    SecureQrReaderTheme {
        Surface {
            AboutScreen()
        }
    }
}


