package com.prof18.secureqrreader

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mikepenz.aboutlibraries.LibsBuilder
import kotlinx.coroutines.launch

@Composable
internal fun AppContainer(
    preferencesWrapper: PreferencesWrapper,
    onSplashScreenDone: () -> Unit,
) {

  }

