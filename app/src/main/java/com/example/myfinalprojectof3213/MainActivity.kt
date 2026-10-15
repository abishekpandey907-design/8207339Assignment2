package com.example.myfinalprojectof3213

import android.content.Intent
import com.example.myfinalprojectof3213.ui.dashboard.DashboardActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myfinalprojectof3213.ui.login.LoginViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->
            val safeInsets = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.ime()
            )

            view.setPadding(
                safeInsets.left,
                safeInsets.top,
                safeInsets.right,
                safeInsets.bottom
            )

            insets
        }

        val studentIdInput = findViewById<EditText>(R.id.etStudentId)
        val firstNameInput = findViewById<EditText>(R.id.etFirstName)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val progressBar = findViewById<ProgressBar>(R.id.progressLogin)
        val errorText = findViewById<TextView>(R.id.tvLoginError)

        loginButton.setOnClickListener {
            loginViewModel.login(
                username = studentIdInput.text.toString(),
                password = firstNameInput.text.toString()
            )
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.uiState.collect { state ->
                    progressBar.isVisible = state.isLoading

                    loginButton.isEnabled = !state.isLoading
                    studentIdInput.isEnabled = !state.isLoading
                    firstNameInput.isEnabled = !state.isLoading

                    errorText.text = state.errorMessage.orEmpty()
                    errorText.isVisible = state.errorMessage != null

                    val keypass = state.keypass

                    if (keypass != null) {
                        val dashboardIntent = Intent(
                            this@MainActivity,
                            DashboardActivity::class.java
                        ).apply {
                            putExtra(DashboardActivity.EXTRA_KEYPASS, keypass)
                        }

                        startActivity(dashboardIntent)
                        loginViewModel.clearLoginResult()
                    }
                }
            }
        }
    }
}