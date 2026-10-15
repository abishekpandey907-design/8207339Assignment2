package com.example.myfinalprojectof3213

import com.example.myfinalprojectof3213.data.model.DashboardEntity
import com.example.myfinalprojectof3213.data.model.DashboardResponse
import com.example.myfinalprojectof3213.data.model.LoginRequest
import com.example.myfinalprojectof3213.data.model.LoginResponse
import com.example.myfinalprojectof3213.data.remote.ApiService
import com.example.myfinalprojectof3213.data.repository.DashboardRepository
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class DashboardRepositoryTest {

    @Test
    fun `successful response returns dashboard and forwards keypass`() =
        runTest {
            val expected = DashboardResponse(
                entities = listOf(
                    DashboardEntity(
                        title = "Test Book",
                        author = "Test Author",
                        genre = "Fiction",
                        publicationYear = 2000,
                        description = "Test description"
                    )
                ),
                entityTotal = 1
            )

            var capturedKeypass: String? = null

            val api = FakeApiService { keypass ->
                capturedKeypass = keypass
                Response.success(expected)
            }

            val repository = DashboardRepository(apiService = api)

            val actual = repository.getDashboard("books")

            assertEquals(expected, actual)
            assertEquals("books", capturedKeypass)
            assertEquals(1, api.dashboardCalls)
        }

    @Test
    fun `blank keypass is rejected without calling API`() =
        runTest {
            val api = FakeApiService {
                error("API should not be called for a blank keypass")
            }

            val repository = DashboardRepository(apiService = api)

            try {
                repository.getDashboard("   ")
                fail("Expected an IOException for a blank keypass")
            } catch (exception: IOException) {
                assertEquals(
                    "Missing login keypass. Please log in again.",
                    exception.message
                )
            }

            assertEquals(0, api.dashboardCalls)
        }

    @Test
    fun `HTTP error includes status code in message`() =
        runTest {
            val api = FakeApiService {
                Response.error<DashboardResponse>(
                    500,
                    """{"message":"Server error"}""".toResponseBody()
                )
            }

            val repository = DashboardRepository(apiService = api)

            try {
                repository.getDashboard("books")
                fail("Expected an IOException for an HTTP error")
            } catch (exception: IOException) {
                assertEquals(
                    "Unable to load dashboard (HTTP 500). Please try again.",
                    exception.message
                )
            }
        }

    @Test
    fun `successful response without body throws empty response error`() =
        runTest {
            val api = FakeApiService {
                Response.success<DashboardResponse>(null)
            }

            val repository = DashboardRepository(apiService = api)

            try {
                repository.getDashboard("books")
                fail("Expected an IOException for a missing response body")
            } catch (exception: IOException) {
                assertEquals(
                    "The server returned an empty dashboard response.",
                    exception.message
                )
            }
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
            error("Login should not be called in dashboard repository tests")
        }

        override suspend fun getDashboard(
            keypass: String
        ): Response<DashboardResponse> {
            dashboardCalls++
            return dashboardHandler(keypass)
        }
    }
}