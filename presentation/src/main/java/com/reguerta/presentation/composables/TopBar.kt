package com.reguerta.presentation.composables

import com.reguerta.presentation.ui.Dimens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    topBarTextColor: Color = MaterialTheme.colorScheme.onSurface,
    navIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
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
        actions = actions
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
                    contentDescription = "Back"
                )
            }
        }
    )
}


@Preview
@Composable
fun ReguertaTopBarPreview() {
    Screen {
        ReguertaScaffold(
            topBar = {
                ReguertaTopBar(
                    topBarText = "Reguerta",
                    navActionClick = {},
                    navIcon = Icons.AutoMirrored.Filled.ArrowBack
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                ReguertaButton(
                    textButton = "Button",
                    onClick = {}
                )
            }
        }
    }
}

@Preview
@Composable
fun ReguertaHomeTopBarPreview() {
    Screen {
        ReguertaScaffold(
            topBar = {
                ReguertaHomeTopBar(
                    navActionClick = {},
                    navIcon = Icons.AutoMirrored.Filled.ArrowBack
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                ReguertaButton(
                    textButton = "Button",
                    onClick = {}
                )
            }
        }
    }
}