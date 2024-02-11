package com.reguerta.presentation.screen.register

import app.cash.turbine.test
import assertk.assertThat
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
 * From: com.reguerta.presentation.screen.register
 * Created By Manuel Lopera on 1/2/24 at 13:28
 * All rights reserved 2024
 */

@ExtendWith(MainCoroutineExtension::class)
class RegisterViewModelTest {
    private lateinit var viewModel: RegisterViewModel
    private lateinit var authService: AuthService

    @BeforeEach
    fun setUp() {
        authService = mockk()
        viewModel = RegisterViewModel(authService)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `When email input changes, then check state changes`() = runTest {
        // GIVEN
        viewModel.state.test {
            // ignored emission
            awaitItem()
            // ACTION
            viewModel.onEvent(RegisterEvent.OnEmailChanged(goodEmail))
            val firstEmission = awaitItem()

            // ASSERTION
            assertThat(firstEmission.emailInput).isEqualTo(goodEmail)
        }
    }

    @Test
    fun `When password input changes, then check state changes`() = runTest {
        // GIVEN
        viewModel.state.test {
            // ignored emission
            awaitItem()

            // ACTION
            viewModel.onEvent(RegisterEvent.OnPasswordChanged(goodPassword))
            val secondEmission = awaitItem()
            // ASSERTION
            assertThat(secondEmission.passwordInput).isEqualTo(goodPassword)
            assertThat(secondEmission.enabledButton).isFalse()
        }
    }

    @Test
    fun `When repeat password input changes, then check state changes`() = runTest {
        // GIVEN
        viewModel.state.test {
            // ignored emission
            awaitItem()

            // ACTION
            viewModel.onEvent(RegisterEvent.OnRepeatPasswordChanged(goodPassword))
            val secondEmission = awaitItem()
            // ASSERTION
            assertThat(secondEmission.repeatPasswordInput).isEqualTo(goodPassword)
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
            viewModel.onEvent(RegisterEvent.OnEmailChanged(badEmail))
            // ignored emission
            awaitItem()
            viewModel.onEvent(RegisterEvent.OnPasswordChanged(badPassword))
            awaitItem()
            viewModel.onEvent(RegisterEvent.OnRepeatPasswordChanged(badPassword))
            val secondEmission = awaitItem()
            // ASSERTION
            assertThat(secondEmission.enabledButton).isFalse()
        }
    }

    @Test
    fun `When user write different passwords, then check that button not enabled`() = runTest {
        // GIVEN
        viewModel.state.test {
            // ignored emission
            awaitItem()

            // ACTION
            viewModel.onEvent(RegisterEvent.OnEmailChanged(goodEmail))
            // ignored emission
            awaitItem()
            viewModel.onEvent(RegisterEvent.OnPasswordChanged(goodPassword))
            // ignored emission
            awaitItem()
            viewModel.onEvent(RegisterEvent.OnRepeatPasswordChanged("654321"))
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
            viewModel.onEvent(RegisterEvent.OnEmailChanged(goodEmail))
            // ignored emission
            awaitItem()
            viewModel.onEvent(RegisterEvent.OnPasswordChanged(goodPassword))
            // ignored emission
            awaitItem()
            viewModel.onEvent(RegisterEvent.OnRepeatPasswordChanged(goodPassword))
            awaitItem()
            val secondEmission = awaitItem()
            // ASSERTION
            assertThat(secondEmission.enabledButton).isTrue()
        }
    }

    @Test
    fun `When user clicks on register button, then check that register is success`() = runTest {
        coEvery {
            authService.createUserWithEmailAndPassword(goodEmail, goodPassword)
        } returns AuthState.LoggedIn
        // GIVEN
        viewModel.state.test {
            // ignored emission
            awaitItem()

            // ACTION
            viewModel.onEvent(RegisterEvent.OnEmailChanged(goodEmail))
            // ignored emission
            awaitItem()
            viewModel.onEvent(RegisterEvent.OnPasswordChanged(goodPassword))
            // ignored emission
            awaitItem()
            viewModel.onEvent(RegisterEvent.OnRepeatPasswordChanged(goodPassword))
            awaitItem()
            val formCompleteEmission = awaitItem()
            // ASSERTION
            assertThat(formCompleteEmission.enabledButton).isTrue()

            viewModel.onEvent(RegisterEvent.OnRegisterClick)
            awaitItem()
            val finalState = awaitItem()
            // ASSERTION
            assertThat(finalState.goOut).isTrue()
        }
    }

    @Test
    fun `When user clicks on register button, then check that register is fail`() = runTest {
        coEvery {
            authService.createUserWithEmailAndPassword(goodEmail, goodPassword)
        } returns AuthState.Error("error")
        // GIVEN
        viewModel.state.test {
            // ignored emission
            awaitItem()

            // ACTION
            viewModel.onEvent(RegisterEvent.OnEmailChanged(goodEmail))
            // ignored emission
            awaitItem()
            viewModel.onEvent(RegisterEvent.OnPasswordChanged(goodPassword))
            // ignored emission
            awaitItem()
            viewModel.onEvent(RegisterEvent.OnRepeatPasswordChanged(goodPassword))
            awaitItem()
            val formCompleteEmission = awaitItem()
            // ASSERTION
            assertThat(formCompleteEmission.enabledButton).isTrue()

            viewModel.onEvent(RegisterEvent.OnRegisterClick)
            awaitItem()
            val finalState = awaitItem()
            // ASSERTION
            assertThat(finalState.goOut).isFalse()
            assertThat(finalState.errorMessage).isEqualTo("error")
        }
    }
}
