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

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.mikepenz.aboutlibraries.LibsBuilder
import com.prof18.secureqrreader.style.Margins
import com.prof18.secureqrreader.style.SecureQrReaderTheme
import com.prof18.secureqrreader.style.toolbarColor

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SecureQrReaderTheme {
                AboutScreen(
                    showOnGithubClicked = {
                        val browserIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/prof18/Secure-QR-Reader"),
                        )
                        startActivity(browserIntent)
                    },
                    licensesClicked = {
                        // TODO: move to compose support?
                        LibsBuilder()
                            .withLicenseShown(true)
                            .withAboutAppName(getString(R.string.app_name))
                            .withActivityTitle("Open Source Libraries")
                            .withAboutDescription("<a href='https://it.freepik.com/foto-vettori-gratuito/tecnologia'>Vectors from freepik - it.freepik.com</a>")
                            .withEdgeToEdge(true)
                            .start(this)
                    },
                    nameClicked = {
                        val browserIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.marcogomiero.com"),
                        )
                        startActivity(browserIntent)
                    },
                    onBackPressed = { onBackPressed() }
                )
            }
        }
    }
}

@Composable
private fun AboutScreen(
    showOnGithubClicked: () -> Unit = {},
    licensesClicked: () -> Unit = {},
    nameClicked: () -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") }, // TODO: localize
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                backgroundColor = toolbarColor()
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                item {
                    Text(
                        modifier = Modifier
                            .padding(Margins.regular),
                        color = MaterialTheme.colors.onBackground,
                        text = stringResource(id = R.string.welcome_screen_content),
                        style = MaterialTheme.typography.body1,
                    )
                }
                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Margins.regular),
                        onClick = showOnGithubClicked
                    ) {
                        Text(stringResource(id = R.string.show_on_github))
                    }
                }
                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Margins.regular)
                            .padding(bottom = Margins.regular),
                        onClick = licensesClicked
                    ) {
                        Text(stringResource(id = R.string.open_source_licenses))
                    }
                }
            }
            AnnotatedClickableText(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(Margins.big),
                onTextClick = nameClicked
            )
        }
    }
}

@Composable
fun AnnotatedClickableText(
    modifier: Modifier = Modifier,
    onTextClick: () -> Unit,
) {
    val annotatedText = buildAnnotatedString {
        append(stringResource(id = R.string.author_label))

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
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AboutScreenPreview() {
    SecureQrReaderTheme {
        Surface {
            AboutScreen()
        }
    }
}


