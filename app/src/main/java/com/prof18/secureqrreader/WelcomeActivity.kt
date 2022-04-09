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

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.google.android.material.composethemeadapter.MdcTheme

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)

        val onBoardingDone = sharedPref.getBoolean(ONBOARDING_DONE, false)
        if (onBoardingDone) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        super.onCreate(savedInstanceState)

        setContent {
            MdcTheme {
                WelcomeScreen(
                    onStartClick = {
                        with(sharedPref.edit()) {
                            putBoolean(ONBOARDING_DONE, true)
                            commit()
                        }

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }

    companion object {
        private const val ONBOARDING_DONE = "onboarding_done"
    }
}

@Composable
private fun WelcomeScreen(
    onStartClick: () -> Unit = {}
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
                        .padding(top = AppMargins.big)
                        .fillMaxWidth()
                        .wrapContentWidth(align = Alignment.CenterHorizontally),
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.h1.copy(fontSize = 28.sp),
                )
            }
            item {
                Text(
                    modifier = Modifier
                        .padding(AppMargins.big),
                    text = stringResource(id = R.string.welcome_screen_content),
                    style = MaterialTheme.typography.body1.copy(fontSize = 18.sp),
                )
            }
            item {
                Image(
                    painter = painterResource(id = R.drawable.privacy_vector),
                    contentDescription = null,
                )
            }
        }
        Button(
            modifier = Modifier
                .align(Alignment.End)
                .padding(AppMargins.regular),
            onClick = onStartClick
        ) {
            Text(stringResource(id = R.string.welcome_screen_button))
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WelcomeScreenPreview() {
    MdcTheme {
        Surface {
            WelcomeScreen()
        }
    }
}
