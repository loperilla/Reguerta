package com.reguerta.presentation.screen.auth.firstScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.reguerta.presentation.R
import com.reguerta.presentation.composables.ReguertaFullButton
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.ui.Dimens
import com.reguerta.presentation.composables.ReguertaScaffold
import com.reguerta.presentation.navigation.Routes
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.reguerta.presentation.getWidthDeviceDp

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen
 * Created By Manuel Lopera on 24/1/24 at 16:08
 * All rights reserved 2024
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun firstScreen(
    navigateTo: (String) -> Unit
) {
    getWidthDeviceDp()
    Screen {
        ReguertaScaffold {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    modifier = Modifier.height(Dimens.Spacing.xxl)
                )

                FirstScreenTitle(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.weight(0.5f)
                )
                Image(
                    painter = painterResource(id = R.mipmap.firstscreenn),
                    contentDescription = null,
                    modifier = Modifier
                        .size(Dimens.Size.dp188)
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Fit
                )
                Spacer(
                    modifier = Modifier.weight(0.5f)
                )
                ReguertaFullButton(
                    textButton = "Entrar a la app",
                    onClick = { navigateTo(Routes.AUTH.LOGIN.route) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                FirstScreenTextBottom(
                    onClick = {
                        navigateTo(Routes.AUTH.REGISTER.route)
                    },
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(Dimens.Spacing.xl)
                )
            }
        }
    }
}

@Composable
private fun FirstScreenTitle(modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        TextTitle(
            text = "Bienvenido a",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Normal),
            textColor = MaterialTheme.colorScheme.onSurface,
        )
        TextTitle(
            text = "La RegÜerta",
            style = MaterialTheme.typography.displayMedium,
            textColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(Dimens.Spacing.sm)
        )
    }
}

@Composable
private fun FirstScreenTextBottom(
    onClick: () -> Unit,
    modifier: Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        TextBody(
            text = "¿No estás registrado?",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
            textColor = MaterialTheme.colorScheme.onSurface,
        )
        TextBody(
            text = "Regístrate",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.Normal),
            textColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(Dimens.Spacing.md)
                .clickable { onClick() }
        )
    }
}
