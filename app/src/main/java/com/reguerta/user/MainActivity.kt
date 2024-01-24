package com.reguerta.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.reguerta.presentation.UiState
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
                val navController = rememberNavController()
                val splashState = viewModel.splashState.collectAsStateWithLifecycle().value

                splashScreen.setKeepOnScreenCondition {
                    when (splashState) {
                        UiState.Loading -> true
                        UiState.Success -> false
                        UiState.Error -> false
                    }
                }

                NavHost(
                    navController,
                    startDestination = if (splashState is UiState.Error) Routes.AUTH.route else Routes.HOME.route
                ) {
                    navigation(startDestination = Routes.AUTH.FIRST_SCREEN.route, route = Routes.AUTH.route) {
                        composable(Routes.AUTH.FIRST_SCREEN.route) {
                            Text(text = "FirstScreen")
                        }
                        composable(Routes.AUTH.LOGIN.route) {
//                            LoginScreen()
                        }
                        composable(Routes.AUTH.REGISTER.route) {
//                            RegisterScreen()
                        }
                    }
                    composable(Routes.HOME.route) {
                        Text(text = "Home")
//                            HomeScreen()
                    }
                }
            }
        }
    }
}
