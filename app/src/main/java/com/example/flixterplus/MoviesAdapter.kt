package com.example.flixterplus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MoviesAdapter(
    private val movies: List<Movie>
) : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val posterImageView: ImageView = itemView.findViewById(R.id.posterImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val overviewTextView: TextView = itemView.findViewById(R.id.overviewTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.titleTextView.text = movie.title
        holder.overviewTextView.text = movie.overview

        Glide.with(holder.itemView.context)
            .load(movie.posterUrl)
            .placeholder(R.drawable.poster_placeholder)
            .error(R.drawable.poster_error)
            .into(holder.posterImageView)
    }

    override fun getItemCount(): Int = movies.size
}
