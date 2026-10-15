package com.example.myfinalprojectof3213

import com.example.myfinalprojectof3213.data.model.DashboardResponse
import com.example.myfinalprojectof3213.data.model.LoginRequest
import com.example.myfinalprojectof3213.data.model.LoginResponse
import com.example.myfinalprojectof3213.data.remote.ApiService
import com.example.myfinalprojectof3213.data.repository.LoginRepository
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class LoginRepositoryTest {

    @Test
    fun `successful login returns keypass and sends credentials`() =
        runBlocking<Unit> {
            var capturedRequest: LoginRequest? = null

            val api = FakeApiService { request ->
                capturedRequest = request

                Response.success(
                    LoginResponse(keypass = "books")
                )
            }

            val repository = LoginRepository(apiService = api)

            val result = repository.login(
                username = "12345678",
                password = "TestName"
            )

            assertEquals("books", result)
            assertEquals("12345678", capturedRequest?.username)
            assertEquals("TestName", capturedRequest?.password)
        }

    @Test
    fun `unauthorized login throws rejection message`() =
        runBlocking<Unit> {
            val api = FakeApiService {
                Response.error<LoginResponse>(
                    401,
                    """{"message":"Unauthorized"}""".toResponseBody()
                )
            }

            val repository = LoginRepository(apiService = api)

            try {
                repository.login(
                    username = "12345678",
                    password = "WrongName"
                )

                fail("Expected an IOException for rejected login")
            } catch (exception: IOException) {
                assertEquals(
                    "Login was rejected. Check your student ID and first name.",
                    exception.message
                )
            }
        }

    @Test
    fun `blank keypass throws empty login key message`() =
        runBlocking<Unit> {
            val api = FakeApiService {
                Response.success(
                    LoginResponse(keypass = "")
                )
            }

            val repository = LoginRepository(apiService = api)

            try {
                repository.login(
                    username = "12345678",
                    password = "TestName"
                )

                fail("Expected an IOException for an empty keypass")
            } catch (exception: IOException) {
                assertEquals(
                    "The server returned an empty login key.",
                    exception.message
                )
            }
        }

    @Test
    fun `network failure is propagated`() =
        runBlocking<Unit> {
            val networkError = IOException("No internet connection")

            val api = FakeApiService {
                throw networkError
            }

            val repository = LoginRepository(apiService = api)

            try {
                repository.login(
                    username = "12345678",
                    password = "TestName"
                )

                fail("Expected the network IOException")
            } catch (exception: IOException) {
                assertSame(networkError, exception)
            }
        }

    private class FakeApiService(
        private val loginHandler:
        suspend (LoginRequest) -> Response<LoginResponse>
    ) : ApiService {

        override suspend fun login(
            request: LoginRequest
        ): Response<LoginResponse> {
            return loginHandler(request)
        }

        override suspend fun getDashboard(
            keypass: String
        ): Response<DashboardResponse> {
            error(
                "Dashboard should not be called in login repository tests"
            )
        }
    }
}