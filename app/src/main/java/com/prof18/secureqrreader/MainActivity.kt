package com.prof18.secureqrreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
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
            val navController = rememberNavController()
            val scope = rememberCoroutineScope()
            val context = LocalContext.current
            val navBackStackEntry by navController.currentBackStackEntryAsState()

            var scanResult by remember {
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

                NavHost(navController = navController, startDestination = Screen.Splash.name) {

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
                            onOpenButtonClick = { openUrl(scanResult, context) },
                            onCopyButtonClick = { copyToClipboard(scanResult, context) },
                            onShareButtonClick = { shareResult(scanResult, context) },
                            onScanAnotherButtonClick = { navController.popBackStack() },
                            onAboutClick = { navController.navigate(Screen.AboutScreen.name) }
                        )
                    }

                    composable(Screen.AboutScreen.name) {
                        AboutScreen(
                            showOnGithubClicked = { openUrl("https://www.marcogomiero.com", context) },
                            licensesClicked = {
                                navController.navigate(Screen.LibrariesScreen.name)
                            },
                            nameClicked = { openUrl("https://www.marcogomiero.com", context) },
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
    systemUiController: SystemUiController = rememberSystemUiController(),
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