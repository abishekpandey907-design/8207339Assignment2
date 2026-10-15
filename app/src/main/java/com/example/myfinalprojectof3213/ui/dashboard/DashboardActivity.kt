package com.example.myfinalprojectof3213.ui.dashboard

import android.content.Intent
import com.example.myfinalprojectof3213.ui.details.DetailsActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinalprojectof3213.R
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardActivity : AppCompatActivity() {

    private val dashboardViewModel: DashboardViewModel by viewModel()

    companion object {
        const val EXTRA_KEYPASS = "extra_keypass"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val keypass = intent.getStringExtra(EXTRA_KEYPASS)

        if (keypass.isNullOrBlank()) {
            Toast.makeText(
                this,
                "Missing login key. Please log in again.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        val progressBar =
            findViewById<ProgressBar>(R.id.progressDashboard)

        val statusText =
            findViewById<TextView>(R.id.tvDashboardStatus)

        val errorText =
            findViewById<TextView>(R.id.tvDashboardError)

        val retryButton =
            findViewById<Button>(R.id.btnRetryDashboard)

        val booksRecyclerView =
            findViewById<RecyclerView>(R.id.rvBooks)

        val bookAdapter = BookAdapter { book ->
            val detailsIntent = Intent(
                this,
                DetailsActivity::class.java
            ).apply {
                putExtra(DetailsActivity.EXTRA_TITLE, book.title)
                putExtra(DetailsActivity.EXTRA_AUTHOR, book.author)
                putExtra(DetailsActivity.EXTRA_GENRE, book.genre)
                putExtra(DetailsActivity.EXTRA_DESCRIPTION, book.description)

                book.publicationYear?.let { year ->
                    putExtra(DetailsActivity.EXTRA_YEAR, year)
                }
            }

            startActivity(detailsIntent)
        }

        bookAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        booksRecyclerView.layoutManager = LinearLayoutManager(this)
        booksRecyclerView.adapter = bookAdapter

        retryButton.setOnClickListener {
            dashboardViewModel.loadDashboard(
                keypass = keypass,
                forceRefresh = true
            )
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                dashboardViewModel.uiState.collect { state ->
                    progressBar.isVisible = state.isLoading

                    errorText.text = state.errorMessage.orEmpty()
                    errorText.isVisible = state.errorMessage != null

                    retryButton.isVisible =
                        state.errorMessage != null && !state.isLoading

                    statusText.text = when {
                        state.isLoading ->
                            "Loading dashboard..."

                        state.hasLoaded && state.entities.isEmpty() ->
                            "No books found."

                        state.hasLoaded ->
                            "${state.entities.size} books"

                        else -> ""
                    }

                    bookAdapter.submitList(state.entities)
                }
            }
        }

        dashboardViewModel.loadDashboard(keypass)
    }
}