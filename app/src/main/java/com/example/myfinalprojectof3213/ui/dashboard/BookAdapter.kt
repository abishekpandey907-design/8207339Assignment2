package com.example.myfinalprojectof3213.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinalprojectof3213.R
import com.example.myfinalprojectof3213.data.model.DashboardEntity

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

            metadataText.text = "$genre · $year"

            itemView.setOnClickListener {
                onBookClick(book)
            }
        }
    }

    private object BookDiffCallback : DiffUtil.ItemCallback<DashboardEntity>() {

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