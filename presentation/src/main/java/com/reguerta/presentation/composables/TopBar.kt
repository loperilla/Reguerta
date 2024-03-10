package com.reguerta.presentation.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.reguerta.presentation.ui.TEXT_TOPBAR
import com.reguerta.presentation.ui.Text

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables
 * Created By Manuel Lopera on 10/3/24 at 10:31
 * All rights reserved 2024
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReguertaTopBar(
    topBarText: String,
    navActionClick: () -> Unit,
    topBarTextColor: Color = Text,
    navIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
) {
    MediumTopAppBar(
        title = {
            TextTitle(
                text = topBarText,
                textSize = TEXT_TOPBAR,
                textColor = topBarTextColor
            )
        },
        navigationIcon = {
            IconButton(onClick = navActionClick) {
                Icon(
                    imageVector = navIcon,
                    contentDescription = "Back"
                )
            }
        }
    )
}