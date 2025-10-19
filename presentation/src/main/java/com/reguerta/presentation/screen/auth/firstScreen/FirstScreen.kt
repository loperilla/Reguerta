package com.reguerta.presentation.screen.auth.firstScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.reguerta.presentation.R
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.ui.Dimens
import com.reguerta.presentation.composables.ReguertaScaffold
import com.reguerta.presentation.navigation.Routes
import androidx.compose.material3.MaterialTheme

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen
 * Created By Manuel Lopera on 24/1/24 at 16:08
 * All rights reserved 2024
 */

@Composable
fun firstScreen(
    navigateTo: (String) -> Unit
) {
    Screen {
        ReguertaScaffold {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    modifier = Modifier.height(Dimens.Spacing.xl)
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
                        .weight(1.5f)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(
                    modifier = Modifier.weight(0.5f)
                )

                ReguertaButton(
                    textButton = "Entrar a la app",
                    onClick = { navigateTo(Routes.AUTH.LOGIN.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.Spacing.xl)
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
                    modifier = Modifier.height(Dimens.Spacing.lg)
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
            "Bienvenido a",
            textSize = MaterialTheme.typography.headlineSmall.fontSize,
            textColor = MaterialTheme.colorScheme.onSurface,
        )
        TextTitle(
            "La RegÜerta",
            textSize = MaterialTheme.typography.displaySmall.fontSize,
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
            "¿No estás registrado?",
            textSize = MaterialTheme.typography.bodyLarge.fontSize,
            textColor = MaterialTheme.colorScheme.onSurface,
        )
        TextBody(
            "Regístrate",
            textSize = MaterialTheme.typography.labelLarge.fontSize,
            textColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(Dimens.Spacing.xs)
                .clickable { onClick() }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun FirstScreenPreview() {
    Screen {
        firstScreen {

        }
    }
}
