package com.prof18.secureqrreader

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.prof18.secureqrreader.components.LibrariesScreenScaffold
import com.prof18.secureqrreader.style.Margins
import com.prof18.secureqrreader.style.SecureQrReaderTheme

@Composable
fun LibrariesScreen(
    onBackClick: () -> Unit,
) {
    SecureQrReaderTheme {
        LibrariesScreenScaffold(
            onBackClick = onBackClick
        ) {
            Column {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Margins.regular)
                        .wrapContentWidth(align = Alignment.CenterHorizontally),
                    text = "Vectors from freepik - it.freepik.com",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onBackground,
                )

                LibrariesContainer(
                    Modifier.fillMaxSize()
                )

            }
        }
    }

}