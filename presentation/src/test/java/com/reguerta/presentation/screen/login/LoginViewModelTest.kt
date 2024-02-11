package com.reguerta.presentation.screen.login

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.reguerta.data.AuthState
import com.reguerta.data.firebase.auth.AuthService
import com.reguerta.presentation.badEmail
import com.reguerta.presentation.badPassword
import com.reguerta.presentation.goodEmail
import com.reguerta.presentation.goodPassword
import com.reguerta.testutils.MainCoroutineExtension
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.login
 * Created By Manuel Lopera on 1/2/24 at 11:42
 * All rights reserved 2024
 */

@ExtendWith(MainCoroutineExtension::class)
class LoginViewModelTest {
    private lateinit var viewModel: LoginViewModel
    private lateinit var authService: AuthService

    @BeforeEach
    fun setUp() {
        authService = mockk()
        viewModel = LoginViewModel(authService)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `When email input change, then check state change success`() = runTest {
        // GIVEN

        viewModel.state.test {
            val firstEmission = awaitItem()
            assertThat(firstEmission.emailInput).isEmpty()

            // ACTION
            viewModel.onEvent(LoginEvent.OnEmailChanged(goodEmail))
            val secondEmission = awaitItem()
            // ASSERTION
            assertThat(secondEmission.emailInput).isEqualTo(goodEmail)
            assertThat(secondEmission.enabledButton).isFalse()
        }
    }

    @Test
    fun `When password input change, then check state change success`() = runTest {
        // GIVEN
        viewModel.state.test {
            // ignored emission
            awaitItem()

            // ACTION
            viewModel.onEvent(LoginEvent.OnPasswordChanged(goodPassword))
            val secondEmission = awaitItem()
            // ASSERTION
            assertThat(secondEmission.passwordInput).isEqualTo(goodPassword)
            assertThat(secondEmission.enabledButton).isFalse()
        }
    }

    @Test
    fun `When user write invalid email and password, then check that button not enabled`() = runTest {
        // GIVEN
        viewModel.state.test {
            // ignored emission
            awaitItem()

            // ACTION
            viewModel.onEvent(LoginEvent.OnEmailChanged(badEmail))
            // ignored emission
            awaitItem()
            viewModel.onEvent(LoginEvent.OnPasswordChanged(badPassword))

            val secondEmission = awaitItem()
            // ASSERTION
            assertThat(secondEmission.enabledButton).isFalse()
        }
    }

    @Test
    fun `When user write valid email and password, then check that button enabled`() = runTest {
        // GIVEN
        viewModel.state.test {
            // ignored emission
            awaitItem()

            // ACTION
            viewModel.onEvent(LoginEvent.OnEmailChanged(goodEmail))
            // ignored emission
            awaitItem()
            viewModel.onEvent(LoginEvent.OnPasswordChanged(goodPassword))
            // ignored emission
            awaitItem()
            val finalState = awaitItem()
            // ASSERTION
            assertThat(finalState.enabledButton).isTrue()
        }
    }

    @Test
    fun `When user login successfully, then check that go out`() = runTest {
        // GIVEN
        coEvery {
            authService.logInWithUserPassword(goodEmail, goodPassword)
        } returns AuthState.LoggedIn
        viewModel.state.test {
            // ignored emission
            awaitItem()

            // ACTION
            viewModel.onEvent(LoginEvent.OnEmailChanged(goodEmail))
            // ignored emission
            awaitItem()
            viewModel.onEvent(LoginEvent.OnPasswordChanged(goodPassword))
            // ignored emission
            awaitItem()
            assertThat(awaitItem().enabledButton).isTrue()

            viewModel.onEvent(LoginEvent.OnLoginClick)
            awaitItem()

            val finalState = awaitItem()
            // ASSERTION
            assertThat(finalState.goOut).isTrue()
        }
    }

    @Test
    fun `When user login fail, then check that button is disabled`() = runTest {
        // GIVEN
        coEvery {
            authService.logInWithUserPassword(goodEmail, goodPassword)
        } returns AuthState.Error("error")
        // ACTION
        viewModel.state.test {
            // ignored emission
            awaitItem()

            // ACTION
            viewModel.onEvent(LoginEvent.OnEmailChanged(goodEmail))
            // ignored emission
            awaitItem()
            viewModel.onEvent(LoginEvent.OnPasswordChanged(goodPassword))
            // ignored emission
            awaitItem()
            val loginCredentialState = awaitItem()
            // ASSERTION
            assertThat(loginCredentialState.enabledButton).isTrue()

            viewModel.onEvent(LoginEvent.OnLoginClick)
            awaitItem()
            val finalState = awaitItem()
            // ASSERTION
            assertThat(finalState.isLoading).isFalse()
            assertThat(finalState.errorMessage).isEqualTo("error")
        }
    }
}
