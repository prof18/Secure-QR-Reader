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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.prof18.secureqrreader.screens.AboutScreen
import com.prof18.secureqrreader.screens.LibrariesScreen
import com.prof18.secureqrreader.screens.ResultScreen
import com.prof18.secureqrreader.screens.ScanScreen
import com.prof18.secureqrreader.screens.SplashScreen
import com.prof18.secureqrreader.screens.WelcomeScreen
import com.prof18.secureqrreader.style.SecureQrReaderTheme
import com.prof18.secureqrreader.style.toolbarColor
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val preferencesWrapper = PreferencesWrapper(this)

        var dismissSplashScreen = false
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !dismissSplashScreen }

        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberAnimatedNavController()
            val scope = rememberCoroutineScope()
            val navBackStackEntry by navController.currentBackStackEntryAsState()

            var scanResult by rememberSaveable {
                mutableStateOf<String?>(null)
            }

            LaunchedEffect(Unit) {
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    dismissSplashScreen = destination.route != Screen.Splash.name
                }
            }

            SecureQrReaderTheme {

                val toolbarColor = toolbarColor()
                val splashScreenColor = MaterialTheme.colors.background

                val statusBarColor = if (navBackStackEntry?.destination?.route == Screen.WelcomeScreen.name) {
                    splashScreenColor
                } else {
                    toolbarColor
                }

                SetupTransparentSystemUi(
                    systemUiController = rememberSystemUiController(),
                    actualBackgroundColor = statusBarColor
                )

                AnimatedNavHost(
                    navController,
                    startDestination = Screen.Splash.name,
                    enterTransition = { fadeIn() + slideIntoContainer(AnimatedContentScope.SlideDirection.Start) },
                    exitTransition = { fadeOut() + slideOutOfContainer(AnimatedContentScope.SlideDirection.Start) },
                    popEnterTransition = { fadeIn() + slideIntoContainer(AnimatedContentScope.SlideDirection.End) },
                    popExitTransition = { fadeOut() + slideOutOfContainer(AnimatedContentScope.SlideDirection.End) }
                ) {

                    composable(Screen.Splash.name) {
                        SplashScreen(preferencesWrapper) { isOnboardingRequired ->
                            if (isOnboardingRequired) {
                                navController.navigate(Screen.WelcomeScreen.name) {
                                    popUpTo(Screen.Splash.name) {
                                        inclusive = true
                                    }
                                }
                            } else {
                                navController.navigate(Screen.ScanScreen.name) {
                                    popUpTo(Screen.Splash.name) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }

                    composable(Screen.WelcomeScreen.name) {
                        WelcomeScreen(
                            onStartClick = {
                                scope.launch {
                                    preferencesWrapper.setOnboardingDone()
                                }
                                navController.navigate(Screen.ScanScreen.name) {
                                    popUpTo(Screen.WelcomeScreen.name) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    composable(Screen.ScanScreen.name) {
                        ScanScreen(
                            onResultFound = { result ->
                                scanResult = result
                                navController.navigate(Screen.ResultScreen.name)
                            },
                            onAboutClick = {
                                navController.navigate(Screen.AboutScreen.name)
                            }
                        )
                    }

                    composable(Screen.ResultScreen.name) {
                        ResultScreen(
                            scanResult = scanResult,
                            isUrl = isUrl(scanResult),
                            onOpenButtonClick = { openUrl(scanResult, this@MainActivity) },
                            onCopyButtonClick = { copyToClipboard(scanResult, this@MainActivity) },
                            onShareButtonClick = { shareResult(scanResult, this@MainActivity) },
                            onScanAnotherButtonClick = { navController.popBackStack() },
                            onAboutClick = { navController.navigate(Screen.AboutScreen.name) },
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.AboutScreen.name) {
                        AboutScreen(
                            showOnGithubClicked = { openUrl("https://www.marcogomiero.com", this@MainActivity) },
                            licensesClicked = {
                                navController.navigate(Screen.LibrariesScreen.name)
                            },
                            nameClicked = { openUrl("https://www.marcogomiero.com", this@MainActivity) },
                            onBackPressed = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.LibrariesScreen.name) {
                        LibrariesScreen(
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun SetupTransparentSystemUi(
    systemUiController: SystemUiController,
    actualBackgroundColor: Color,
) {
    val minLuminanceForDarkIcons = .5f
    SideEffect {
        systemUiController.setStatusBarColor(
            color = actualBackgroundColor,
            darkIcons = actualBackgroundColor.luminance() > minLuminanceForDarkIcons
        )
    }
}