package com.example.myfinalprojectof3213.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinalprojectof3213.R
import com.example.myfinalprojectof3213.data.model.DashboardEntity
import java.util.Locale

class BookAdapter(
    private val onBookClick: (DashboardEntity) -> Unit
) : ListAdapter<DashboardEntity, BookAdapter.BookViewHolder>(
    BookDiffCallback
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)

        return BookViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: BookViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position), onBookClick)
    }

    class BookViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val coverImage =
            itemView.findViewById<ImageView>(R.id.ivBookCover)

        private val titleText =
            itemView.findViewById<TextView>(R.id.tvBookTitle)

        private val authorText =
            itemView.findViewById<TextView>(R.id.tvBookAuthor)

        private val metadataText =
            itemView.findViewById<TextView>(R.id.tvBookMetadata)

        fun bind(
            book: DashboardEntity,
            onBookClick: (DashboardEntity) -> Unit
        ) {
            titleText.text = book.title ?: "Untitled book"
            authorText.text = book.author ?: "Unknown author"

            val genre = book.genre ?: "Unknown genre"

            val year = book.publicationYear?.toString()
                ?: "Unknown year"

            metadataText.text = itemView.context.getString(
                R.string.book_metadata,
                genre,
                year
            )

            coverImage.setImageResource(
                getBookCover(book.title)
            )

            itemView.setOnClickListener {
                onBookClick(book)
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

    private object BookDiffCallback :
        DiffUtil.ItemCallback<DashboardEntity>() {

        override fun areItemsTheSame(
            oldItem: DashboardEntity,
            newItem: DashboardEntity
        ): Boolean {
            return oldItem.title == newItem.title &&
                    oldItem.author == newItem.author &&
                    oldItem.publicationYear == newItem.publicationYear
        }

        override fun areContentsTheSame(
            oldItem: DashboardEntity,
            newItem: DashboardEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}