package com.example.myfinalprojectof3213

import com.example.myfinalprojectof3213.data.model.DashboardEntity
import com.example.myfinalprojectof3213.data.model.DashboardResponse
import com.example.myfinalprojectof3213.data.model.LoginRequest
import com.example.myfinalprojectof3213.data.model.LoginResponse
import com.example.myfinalprojectof3213.data.remote.ApiService
import com.example.myfinalprojectof3213.data.repository.DashboardRepository
import com.example.myfinalprojectof3213.ui.dashboard.DashboardViewModel
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
class DashboardViewModelTest {

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
    fun `successful load updates loading data and total`() =
        runTest(testDispatcher) {
            val response = sampleDashboard()
            var capturedKeypass: String? = null

            val api = FakeApiService { keypass ->
                capturedKeypass = keypass
                Response.success(response)
            }

            val viewModel = createViewModel(api)

            viewModel.loadDashboard("books")

            assertTrue(viewModel.uiState.value.isLoading)
            assertFalse(viewModel.uiState.value.hasLoaded)

            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertFalse(state.isLoading)
            assertTrue(state.hasLoaded)
            assertNull(state.errorMessage)
            assertEquals(response.entities, state.entities)
            assertEquals(response.entityTotal, state.entityTotal)
            assertEquals("books", capturedKeypass)
            assertEquals(1, api.dashboardCalls)
        }

    @Test
    fun `network failure shows error and stops loading`() =
        runTest(testDispatcher) {
            val api = FakeApiService {
                throw IOException("No internet connection")
            }

            val viewModel = createViewModel(api)

            viewModel.loadDashboard("books")

            assertTrue(viewModel.uiState.value.isLoading)

            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertFalse(state.isLoading)
            assertFalse(state.hasLoaded)
            assertTrue(state.entities.isEmpty())
            assertEquals(
                "No internet connection",
                state.errorMessage
            )
        }

    @Test
    fun `normal load after success does not request again`() =
        runTest(testDispatcher) {
            val api = FakeApiService {
                Response.success(sampleDashboard())
            }

            val viewModel = createViewModel(api)

            viewModel.loadDashboard("books")
            advanceUntilIdle()

            viewModel.loadDashboard("books")
            advanceUntilIdle()

            assertEquals(1, api.dashboardCalls)
            assertTrue(viewModel.uiState.value.hasLoaded)
        }

    @Test
    fun `forced refresh requests again after success`() =
        runTest(testDispatcher) {
            val api = FakeApiService {
                Response.success(sampleDashboard())
            }

            val viewModel = createViewModel(api)

            viewModel.loadDashboard("books")
            advanceUntilIdle()

            viewModel.loadDashboard(
                keypass = "books",
                forceRefresh = true
            )

            assertTrue(viewModel.uiState.value.isLoading)

            advanceUntilIdle()

            assertEquals(2, api.dashboardCalls)
            assertFalse(viewModel.uiState.value.isLoading)
            assertTrue(viewModel.uiState.value.hasLoaded)
        }

    @Test
    fun `second load while loading does not duplicate request`() =
        runTest(testDispatcher) {
            val api = FakeApiService {
                Response.success(sampleDashboard())
            }

            val viewModel = createViewModel(api)

            viewModel.loadDashboard("books")
            viewModel.loadDashboard("books")

            advanceUntilIdle()

            assertEquals(1, api.dashboardCalls)
            assertTrue(viewModel.uiState.value.hasLoaded)
        }

    @Test
    fun `load can retry successfully after failure`() =
        runTest(testDispatcher) {
            var attempts = 0

            val api = FakeApiService {
                attempts++

                if (attempts == 1) {
                    throw IOException("Temporary failure")
                }

                Response.success(sampleDashboard())
            }

            val viewModel = createViewModel(api)

            viewModel.loadDashboard("books")
            advanceUntilIdle()

            assertEquals(
                "Temporary failure",
                viewModel.uiState.value.errorMessage
            )

            viewModel.loadDashboard("books")

            assertTrue(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.errorMessage)

            advanceUntilIdle()

            assertEquals(2, api.dashboardCalls)
            assertTrue(viewModel.uiState.value.hasLoaded)
            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.errorMessage)
        }

    @Test
    fun `successful empty response is marked as loaded`() =
        runTest(testDispatcher) {
            val api = FakeApiService {
                Response.success(
                    DashboardResponse(
                        entities = emptyList(),
                        entityTotal = 0
                    )
                )
            }

            val viewModel = createViewModel(api)

            viewModel.loadDashboard("books")
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertTrue(state.hasLoaded)
            assertFalse(state.isLoading)
            assertTrue(state.entities.isEmpty())
            assertEquals(0, state.entityTotal)
            assertNull(state.errorMessage)
        }

    private fun sampleDashboard(): DashboardResponse {
        return DashboardResponse(
            entities = listOf(
                DashboardEntity(
                    title = "Test Book",
                    author = "Test Author",
                    genre = "Fiction",
                    publicationYear = 2000,
                    description = "A description used only for testing."
                )
            ),
            entityTotal = 1
        )
    }

    private fun createViewModel(
        api: ApiService
    ): DashboardViewModel {
        return DashboardViewModel(
            dashboardRepository = DashboardRepository(
                apiService = api
            )
        )
    }

    private class FakeApiService(
        private val dashboardHandler:
        suspend (String) -> Response<DashboardResponse>
    ) : ApiService {

        var dashboardCalls: Int = 0
            private set

        override suspend fun login(
            request: LoginRequest
        ): Response<LoginResponse> {
            error("Login should not be called in dashboard tests")
        }

        override suspend fun getDashboard(
            keypass: String
        ): Response<DashboardResponse> {
            dashboardCalls++
            return dashboardHandler(keypass)
        }
    }
}