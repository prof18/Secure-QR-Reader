package com.prof18.secureqrreader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun SplashScreen(
    preferencesWrapper: PreferencesWrapper,
    isOnboardingRequired: (Boolean) -> Unit,
) {

    LaunchedEffect(Unit) {
        if (preferencesWrapper.isOnboardingDone()) {
            isOnboardingRequired(false)
        } else {
            isOnboardingRequired(true)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}