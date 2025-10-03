package com.reguerta.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import com.reguerta.presentation.MainNavigation
import com.reguerta.presentation.UiState
import com.reguerta.presentation.sync.ForegroundSyncManager
import com.reguerta.presentation.ui.ReguertaTheme
import com.reguerta.presentation.ui.Routes
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("SYNC_MainActivity: onCreate lanzado")
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ReguertaTheme {
                Timber.i("SYNC_MainActivity: Entrando en setContent/Composable raíz")
                val viewModel: MainActivityViewModel = hiltViewModel()
                Timber.i("SYNC_MainActivity: viewModel obtenido via hiltViewModel()")
                val splashState = viewModel.splashState.collectAsStateWithLifecycle().value

                LaunchedEffect(Unit) {
                    Timber.i("SYNC_MainActivity: LaunchedEffect(Unit) ejecutado, llamando onAppForegrounded()")
                    viewModel.onAppForegrounded()
                }

                LaunchedEffect(splashState) {
                    Timber.i("SYNC_MainActivity: LaunchedEffect(splashState=$splashState) ejecutado")
                    if (splashState == UiState.Success) {
                        Timber.i("SYNC_MainActivity: SplashState es Success, pidiendo sync lifecycle")
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

                val sessionExpired = viewModel.sessionExpired.collectAsStateWithLifecycle().value
                val context = LocalContext.current
                LaunchedEffect(sessionExpired) {
                    Timber.i("SYNC_MainActivity: LaunchedEffect(sessionExpired=$sessionExpired) ejecutado")
                    if (sessionExpired) {
                        Toast.makeText(
                            context,
                            "Debes iniciar sesión para continuar.",
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.resetSessionExpired()
                    }
                }

                val startDestination = when (splashState) {
                    UiState.Success -> Routes.HOME.route
                    else -> Routes.AUTH.route // Incluye Loading y Error
                }

                Timber.i("SYNC_MainActivity: Lanzando MainNavigation con startDestination: $startDestination (splashState=$splashState)")

                MainNavigation(
                    startDestination = startDestination
                )
            }
        }
    }
}