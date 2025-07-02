package com.prof18.secureqrreader.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.Icons.Outlined
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.prof18.secureqrreader.R
import com.prof18.secureqrreader.hasFlash

@Composable
internal fun ScanScreenNavigationBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    usePrimaryColor: Boolean = false,
    setFlashOff: () -> Unit,
    setFlashOn: () -> Unit,
    onAboutClick: () -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        title = stringResource(id = R.string.app_name),
        backgroundColor = backgroundColor,
        showBackButton = false,
        showToolbarActions = true,
        showFlashSelector = true,
        usePrimaryColor = usePrimaryColor,
        setFlashOff = setFlashOff,
        setFlashOn = setFlashOn,
        onAboutClick = onAboutClick
    )
}

@Composable
internal fun NavigationBar(
    modifier: Modifier = Modifier,
    title: String,
    backgroundColor: Color,
    showBackButton: Boolean,
    onBackClick: () -> Unit = {},
    showToolbarActions: Boolean,
    showFlashSelector: Boolean,
    usePrimaryColor: Boolean = false,
    setFlashOff: () -> Unit,
    setFlashOn: () -> Unit,
    onAboutClick: () -> Unit,
) {

    val context = LocalContext.current

    val isFlashActive = remember {
        mutableStateOf(false)
    }

    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                color = if (usePrimaryColor) {
                    MaterialTheme.colors.onPrimary
                } else {
                    MaterialTheme.colors.onBackground
                },
                style = MaterialTheme.typography.h6,
            )
        },
        backgroundColor = backgroundColor,
        elevation = 0.dp,
        contentColor = if (usePrimaryColor) {
            MaterialTheme.colors.onPrimary
        } else {
            MaterialTheme.colors.onBackground
        },
        navigationIcon = if (showBackButton) {
            {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                                Filled.FlashOff,
                                contentDescription = stringResource(R.string.turn_off_flash),
                            )
                        } else {
                            Icon(
                                Filled.FlashOn,
                                contentDescription = stringResource(id = R.string.turn_on_flash),
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onAboutClick
                ) {
                    Icon(
                        Outlined.Info,
                        contentDescription = null,
                        tint = if (usePrimaryColor) {
                            Color.White
                        } else {
                            MaterialTheme.colors.onBackground
                        },
                    )
                }
            }
        }
    )
}
