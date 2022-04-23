package com.prof18.secureqrreader.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.prof18.secureqrreader.R
import com.prof18.secureqrreader.hasFlash
import com.prof18.secureqrreader.style.SecureQrReaderTheme
import com.prof18.secureqrreader.style.toolbarColor

@Composable
fun AboutScreenScaffold(
    onBackClick: () -> Unit,
    content: @Composable () -> Unit,
) = AppScaffold(
    showBackButton = true,
    showToolbarActions = false,
    showFlashSelector = false,
    setFlashOn = { },
    setFlashOff = { },
    onAboutClick = { },
    onBackClick = onBackClick,
    content = content
)

@Composable
fun ScanScreenScaffold(
    setFlashOn: () -> Unit,
    setFlashOff: () -> Unit,
    onAboutClick: () -> Unit,
    content: @Composable () -> Unit,
) = AppScaffold(
    showBackButton = false,
    showToolbarActions = true,
    showFlashSelector = true,
    setFlashOn = setFlashOn,
    setFlashOff = setFlashOff,
    onAboutClick = onAboutClick,
    onBackClick = {},
    content = content
)

@Composable
fun ScanScreenWithoutCameraScaffold(
    onAboutClick: () -> Unit,
    content: @Composable () -> Unit,
) = AppScaffold(
    showBackButton = false,
    showToolbarActions = true,
    showFlashSelector = false,
    setFlashOn = { },
    setFlashOff = { },
    onAboutClick = onAboutClick,
    onBackClick = {},
    content = content
)

@Composable
fun ResultScreenScaffold(
    onBackClick: () -> Unit,
    onAboutClick: () -> Unit,
    content: @Composable () -> Unit,
) = AppScaffold(
    showBackButton = true,
    showToolbarActions = true,
    showFlashSelector = false,
    setFlashOn = { },
    setFlashOff = { },
    onAboutClick = onAboutClick,
    onBackClick = onBackClick,
    content = content
)

@Composable
private fun AppScaffold(
    showBackButton: Boolean,
    showFlashSelector: Boolean,
    showToolbarActions: Boolean,
    setFlashOn: () -> Unit,
    setFlashOff: () -> Unit,
    onAboutClick: () -> Unit,
    onBackClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    val isFlashActive = remember {
        mutableStateOf(false)
    }

    SecureQrReaderTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            color = MaterialTheme.colors.onPrimary,
                            style = MaterialTheme.typography.h6,
                        )
                    },
                    backgroundColor = toolbarColor(),
                    contentColor = MaterialTheme.colors.onPrimary,
                    navigationIcon = if (showBackButton) {
                        {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                    } else {
                        null
                    },
                    actions = {
                        if (showToolbarActions) {
                            if (showFlashSelector && hasFlash(context)) {
                                IconButton(
                                    onClick = {
                                        if (isFlashActive.value) {
                                            setFlashOff()
                                        } else {
                                            setFlashOn()
                                        }
                                        isFlashActive.value = !isFlashActive.value
                                    }
                                ) {
                                    if (isFlashActive.value) {
                                        Icon(
                                            Icons.Filled.FlashOff,
                                            contentDescription = "Turn off flash", // TODO: localize
                                        )
                                    } else {
                                        Icon(
                                            Icons.Filled.FlashOn,
                                            contentDescription = stringResource(id = R.string.turn_on_flash),
                                        )
                                    }
                                }
                            }

                            TopAppBarDropdownMenu(
                                onAboutClick = {
                                    onAboutClick()
                                }
                            )
                        }
                    }
                )
            },
            content = { content() }
        )
    }
}

