package com.prof18.secureqrreader

internal sealed class Screen(val name: String) {
    object Splash : Screen("splash_screen")
    object WelcomeScreen : Screen("welcome_screen")
    object ScanScreen : Screen("scan_screen")
    object ResultScreen : Screen("result_screen")
    object AboutScreen : Screen("about_screen")
}
