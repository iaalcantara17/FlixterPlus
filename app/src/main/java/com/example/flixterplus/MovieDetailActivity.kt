package com.example.flixterplus

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.flixterplus.databinding.ActivityMovieDetailBinding

class MovieDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        postponeEnterTransition()

        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movie = if (Build.VERSION.SDK_INT >= 33) {
            intent.getSerializableExtra(MOVIE_EXTRA, Movie::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(MOVIE_EXTRA) as? Movie
        }

        if (movie == null) {
            finish()
            return
        }

        val transitionName = intent.getStringExtra(POSTER_TRANSITION_NAME) ?: "poster_${movie.id}"
        ViewCompat.setTransitionName(binding.posterImageView, transitionName)

        binding.titleTextView.text = movie.title
        binding.overviewTextView.text = movie.overview

        val meta = buildString {
            append("Release: ")
            append(movie.releaseDate ?: "N/A")
            append("\nRating: ")
            append(String.format("%.1f", movie.voteAverage))
            append(" (")
            append(movie.voteCount)
            append(" votes)")
            append("\nPopularity: ")
            append(String.format("%.1f", movie.popularity))
            append("\nLanguage: ")
            append(movie.originalLanguage.uppercase())
        }
        binding.metaTextView.text = meta

        Glide.with(this)
            .load(movie.backdropUrl ?: movie.posterUrl)
            .placeholder(R.drawable.poster_placeholder)
            .error(R.drawable.poster_error)
            .transform(CenterCrop(), RoundedCorners(24))
            .into(binding.backdropImageView)

        Glide.with(this)
            .load(movie.posterUrl)
            .placeholder(R.drawable.poster_placeholder)
            .error(R.drawable.poster_error)
            .transform(CenterCrop(), RoundedCorners(24))
            .into(binding.posterImageView)

        binding.posterImageView.post { startPostponedEnterTransition() }
    }

    companion object {
        const val MOVIE_EXTRA = "MOVIE_EXTRA"
        const val POSTER_TRANSITION_NAME = "POSTER_TRANSITION_NAME"
    }
}