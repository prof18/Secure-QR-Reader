package com.prof18.secureqrreader

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mikepenz.aboutlibraries.LibsBuilder
import com.prof18.secureqrreader.R.string
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

            var scanResult by remember {
                mutableStateOf<String?>(null)
            }

            LaunchedEffect(Unit) {
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    dismissSplashScreen = destination.route != Screen.Splash.name
                }
            }

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
                            // TODO: move to compose support?
                            LibsBuilder()
                                .withLicenseShown(true)
                                .withAboutAppName(context.getString(string.app_name))
                                .withActivityTitle("Open Source Libraries")
                                .withAboutDescription("<a href='https://it.freepik.com/foto-vettori-gratuito/tecnologia'>Vectors from freepik - it.freepik.com</a>")
                                .withEdgeToEdge(true)
                                .start(context)
                        },
                        nameClicked = { openUrl("https://www.marcogomiero.com", context) },
                        onBackPressed = { navController.popBackStack() }
                    )
                }
            }

        }
    }
}