package com.example.myfinalprojectof3213

import com.example.myfinalprojectof3213.data.model.DashboardResponse
import com.example.myfinalprojectof3213.data.model.LoginRequest
import com.example.myfinalprojectof3213.data.model.LoginResponse
import com.example.myfinalprojectof3213.data.remote.ApiService
import com.example.myfinalprojectof3213.data.repository.LoginRepository
import com.example.myfinalprojectof3213.ui.login.LoginUiState
import com.example.myfinalprojectof3213.ui.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `empty fields show error without calling API`() =
        runTest(testDispatcher) {
            val api = FakeApiService {
                error("API should not be called for empty input")
            }

            val viewModel = createViewModel(api)

            viewModel.login(username = "", password = "")
            advanceUntilIdle()

            assertEquals(
                "Enter your student ID and first name.",
                viewModel.uiState.value.errorMessage
            )
            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.keypass)
            assertEquals(0, api.loginCalls)
        }

    @Test
    fun `nonnumeric student ID shows error without calling API`() =
        runTest(testDispatcher) {
            val api = FakeApiService {
                error("API should not be called for invalid student ID")
            }

            val viewModel = createViewModel(api)

            viewModel.login(
                username = "s12345678",
                password = "TestName"
            )
            advanceUntilIdle()

            assertEquals(
                "Enter your student ID using digits only, without s.",
                viewModel.uiState.value.errorMessage
            )
            assertFalse(viewModel.uiState.value.isLoading)
            assertEquals(0, api.loginCalls)
        }

    @Test
    fun `successful login changes loading state to success`() =
        runTest(testDispatcher) {
            var capturedRequest: LoginRequest? = null

            val api = FakeApiService { request ->
                capturedRequest = request
                Response.success(LoginResponse(keypass = "books"))
            }

            val viewModel = createViewModel(api)

            viewModel.login(
                username = " 12345678 ",
                password = "TestName"
            )

            assertTrue(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.keypass)

            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertEquals("books", viewModel.uiState.value.keypass)
            assertNull(viewModel.uiState.value.errorMessage)
            assertEquals("12345678", capturedRequest?.username)
            assertEquals("TestName", capturedRequest?.password)
            assertEquals(1, api.loginCalls)
        }

    @Test
    fun `network failure changes loading state to error`() =
        runTest(testDispatcher) {
            val api = FakeApiService {
                throw IOException("No internet connection")
            }

            val viewModel = createViewModel(api)

            viewModel.login(
                username = "12345678",
                password = "TestName"
            )

            assertTrue(viewModel.uiState.value.isLoading)

            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.keypass)
            assertEquals(
                "No internet connection",
                viewModel.uiState.value.errorMessage
            )
        }

    @Test
    fun `clear login result restores initial state`() =
        runTest(testDispatcher) {
            val api = FakeApiService {
                Response.success(LoginResponse(keypass = "books"))
            }

            val viewModel = createViewModel(api)

            viewModel.login(
                username = "12345678",
                password = "TestName"
            )
            advanceUntilIdle()

            assertEquals("books", viewModel.uiState.value.keypass)

            viewModel.clearLoginResult()

            assertEquals(
                LoginUiState(),
                viewModel.uiState.value
            )
        }

    private fun createViewModel(api: ApiService): LoginViewModel {
        val repository = LoginRepository(apiService = api)

        return LoginViewModel(loginRepository = repository)
    }

    private class FakeApiService(
        private val loginHandler:
        suspend (LoginRequest) -> Response<LoginResponse>
    ) : ApiService {

        var loginCalls: Int = 0
            private set

        override suspend fun login(
            request: LoginRequest
        ): Response<LoginResponse> {
            loginCalls++
            return loginHandler(request)
        }

        override suspend fun getDashboard(
            keypass: String
        ): Response<DashboardResponse> {
            error("Dashboard should not be called in login ViewModel tests")
        }
    }
}