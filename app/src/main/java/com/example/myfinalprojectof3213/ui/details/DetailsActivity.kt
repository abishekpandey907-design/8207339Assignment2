package com.example.myfinalprojectof3213.ui.details

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myfinalprojectof3213.R
import java.util.Locale

class DetailsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TITLE = "extra_book_title"
        const val EXTRA_AUTHOR = "extra_book_author"
        const val EXTRA_GENRE = "extra_book_genre"
        const val EXTRA_YEAR = "extra_book_year"
        const val EXTRA_DESCRIPTION = "extra_book_description"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details)

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

        val title = intent.getStringExtra(EXTRA_TITLE)
            ?: "Untitled book"

        val author = intent.getStringExtra(EXTRA_AUTHOR)
            ?: "Unknown author"

        val genre = intent.getStringExtra(EXTRA_GENRE)
            ?: "Unknown genre"

        val year = if (intent.hasExtra(EXTRA_YEAR)) {
            intent.getIntExtra(EXTRA_YEAR, 0).toString()
        } else {
            "Unknown year"
        }

        val description = intent.getStringExtra(EXTRA_DESCRIPTION)
            ?: "No description available."

        findViewById<ImageView>(R.id.ivDetailsCover)
            .setImageResource(getBookCover(title))

        findViewById<TextView>(R.id.tvDetailsTitle).text = title

        findViewById<TextView>(R.id.tvDetailsAuthor).text =
            "Author: $author"

        findViewById<TextView>(R.id.tvDetailsGenre).text =
            "Genre: $genre"

        findViewById<TextView>(R.id.tvDetailsYear).text =
            "Published: $year"

        findViewById<TextView>(R.id.tvDetailsDescription).text =
            description

        findViewById<Button>(R.id.btnBackToDashboard)
            .setOnClickListener {
                finish()
            }
    }

    private fun getBookCover(title: String?): Int {
        val normalizedTitle = title
            .orEmpty()
            .trim()
            .lowercase(Locale.ROOT)

        return when (normalizedTitle) {
            "to kill a mockingbird" ->
                R.drawable.cover_mockingbird

            "1984" ->
                R.drawable.cover_1984

            "pride and prejudice" ->
                R.drawable.cover_pride_prejudice

            "the great gatsby" ->
                R.drawable.cover_great_gatsby

            "one hundred years of solitude" ->
                R.drawable.cover_hundred_years

            "the catcher in the rye" ->
                R.drawable.cover_catcher_rye

            "the hobbit" ->
                R.drawable.cover_hobbit

            else ->
                R.drawable.ic_book
        }
    }
}