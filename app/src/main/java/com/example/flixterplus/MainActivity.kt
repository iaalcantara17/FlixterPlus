package com.example.flixterplus

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.example.flixterplus.databinding.ActivityMainBinding
import okhttp3.Headers

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val popularMovies = mutableListOf<Movie>()
    private val topRatedMovies = mutableListOf<Movie>()

    private lateinit var popularAdapter: MoviesAdapter
    private lateinit var topRatedAdapter: MoviePosterAdapter

    private val client = AsyncHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        popularAdapter = MoviesAdapter(popularMovies) { movie, posterView -> openDetails(movie, posterView) }
        topRatedAdapter = MoviePosterAdapter(topRatedMovies) { movie, posterView -> openDetails(movie, posterView) }

        binding.popularRecyclerView.adapter = popularAdapter
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        binding.popularRecyclerView.layoutManager =
            if (isLandscape) GridLayoutManager(this, 2) else LinearLayoutManager(this)

        binding.topRatedRecyclerView.adapter = topRatedAdapter
        binding.topRatedRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        fetchMovies("https://api.themoviedb.org/3/movie/popular", popularMovies, popularAdapter)
        fetchMovies("https://api.themoviedb.org/3/movie/top_rated", topRatedMovies, topRatedAdapter)
    }

    private fun openDetails(movie: Movie, posterView: ImageView) {
        val transitionName = ViewCompat.getTransitionName(posterView) ?: "poster_${movie.id}"
        val intent = Intent(this, MovieDetailActivity::class.java)
        intent.putExtra(MovieDetailActivity.MOVIE_EXTRA, movie)
        intent.putExtra(MovieDetailActivity.POSTER_TRANSITION_NAME, transitionName)

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            posterView,
            transitionName
        )
        startActivity(intent, options.toBundle())
    }

    private fun fetchMovies(
        url: String,
        targetList: MutableList<Movie>,
        adapter: RecyclerView.Adapter<*>
    ) {
        val params = RequestParams()
        params["api_key"] = BuildConfig.TMDB_API_KEY

        client.get(url, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                val results = json.jsonObject.optJSONArray("results")
                if (results == null) {
                    Toast.makeText(this@MainActivity, "No results returned", Toast.LENGTH_SHORT).show()
                    return
                }

                val parsed = Movie.fromJsonArray(results)
                targetList.clear()
                targetList.addAll(parsed)
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(statusCode: Int, headers: Headers?, response: String, throwable: Throwable?) {
                Toast.makeText(this@MainActivity, "Request failed: $statusCode", Toast.LENGTH_SHORT).show()
            }
        })
    }
}