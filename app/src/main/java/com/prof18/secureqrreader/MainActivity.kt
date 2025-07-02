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

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.prof18.secureqrreader.screens.AboutScreen
import com.prof18.secureqrreader.screens.LibrariesScreen
import com.prof18.secureqrreader.screens.ResultScreen
import com.prof18.secureqrreader.screens.ScanScreen
import com.prof18.secureqrreader.screens.SplashScreen
import com.prof18.secureqrreader.screens.WelcomeScreen
import com.prof18.secureqrreader.style.SecureQrReaderTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val preferencesWrapper = PreferencesWrapper(this)

        var dismissSplashScreen = false
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !dismissSplashScreen }

        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val scope = rememberCoroutineScope()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val isDarkTheme = isSystemInDarkTheme()

            val defaultSystemBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT,
                detectDarkMode = { isDarkTheme },
            )
            var systemBarStyle by remember {
                mutableStateOf(
                    defaultSystemBarStyle
                )
            }

            var scanResult by rememberSaveable {
                mutableStateOf<String?>(null)
            }
            val configuration = LocalConfiguration.current

            LaunchedEffect(Unit) {
                navController.currentBackStackEntryFlow.collect { destination ->
                    Log.d("Tag", "Got destination change: $destination")
                    dismissSplashScreen = destination.destination.route != Screen.Splash.name

                    systemBarStyle = when (destination.destination.route) {
                        Screen.ScanScreen.name -> {
                            when (configuration.orientation) {
                                Configuration.ORIENTATION_LANDSCAPE -> defaultSystemBarStyle
                                else -> SystemBarStyle.dark(
                                    android.graphics.Color.TRANSPARENT,
                                )
                            }
                        }

                        else -> defaultSystemBarStyle
                    }
                }
            }

            SecureQrReaderTheme {
                val navigationBarStyle =
                    if (navBackStackEntry?.destination?.route == Screen.ScanScreen.name && configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        SystemBarStyle.dark(
                            Color.Transparent.toArgb(),
                        )
                    } else {
                        SystemBarStyle.auto(
                            lightScrim,
                            darkScrim
                        )
                    }

                enableEdgeToEdge(
                    statusBarStyle = systemBarStyle,
                    navigationBarStyle = navigationBarStyle,
                )

                NavHost(
                    navController,
                    startDestination = Screen.Splash.name,
                    enterTransition = { fadeIn() + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
                    exitTransition = {
                        fadeOut() + slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Start
                        )
                    },
                    popEnterTransition = {
                        fadeIn() + slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.End
                        )
                    },
                    popExitTransition = {
                        fadeOut() + slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.End
                        )
                    }
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
                            showOnGithubClicked = {
                                openUrl(
                                    "https://github.com/prof18/Secure-QR-Reader",
                                    this@MainActivity
                                )
                            },
                            licensesClicked = {
                                navController.navigate(Screen.LibrariesScreen.name)
                            },
                            nameClicked = {
                                openUrl(
                                    "https://www.marcogomiero.com",
                                    this@MainActivity
                                )
                            },
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

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)