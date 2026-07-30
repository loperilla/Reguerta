package com.reguerta.presentation.composables

import com.reguerta.presentation.ui.Dimens

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

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
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    topBarTextColor: Color = Unspecified,
    navIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    MediumTopAppBar(
        title = {
            if (topBarText.isNotEmpty()) {
                TextTitle(
                    text = topBarText,
                    style = Dimens.Components.TopBar.titleStyle,
                    textColor = topBarTextColor
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = navActionClick) {
                Icon(
                    imageVector = navIcon,
                    contentDescription = "Back",
                    modifier = Modifier.size(Dimens.Components.TopBar.iconSize)
                )
            }
        },
        modifier = modifier,
        actions = actions,
        colors = Dimens.Components.TopBar.colors(),
        scrollBehavior = scrollBehavior,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReguertaHomeTopBar(
    navActionClick: () -> Unit,
    navIcon: ImageVector
) {
    TopAppBar(
        title = {
        },
        navigationIcon = {
            IconButton(onClick = navActionClick) {
                Icon(
                    imageVector = navIcon,
                    contentDescription = "Back",
                    modifier = Modifier.size(Dimens.Components.TopBar.iconSize)
                )
            }
        },
        colors = Dimens.Components.TopBar.colors(),
    )
}
