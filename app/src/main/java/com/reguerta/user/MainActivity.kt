package com.reguerta.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import com.reguerta.presentation.MainNavigation
import com.reguerta.presentation.UiState
import com.reguerta.presentation.sync.ForegroundSyncManager
import com.reguerta.presentation.ui.ReguertaTheme
import com.reguerta.presentation.ui.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            ReguertaTheme {
                val viewModel: MainActivityViewModel = hiltViewModel()
                val splashState = viewModel.splashState.collectAsStateWithLifecycle().value

                LaunchedEffect(Unit) {
                    viewModel.onAppForegrounded()
                }

                LaunchedEffect(splashState) {
                    if (splashState == UiState.Success) {
                        ForegroundSyncManager.requestSyncFromAppLifecycle()
                    }
                }

                splashScreen.setKeepOnScreenCondition {
                    when (splashState) {
                        UiState.Loading -> true
                        UiState.Success -> false
                        UiState.Error -> false
                    }
                }

                MainNavigation(
                    startDestination = if (splashState is UiState.Error) Routes.AUTH.route else Routes.HOME.route
                )
            }
        }
    }
}
