package com.example.flixterplus

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.example.flixterplus.databinding.ActivityMainBinding
import okhttp3.Headers

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val movies = mutableListOf<Movie>()
    private lateinit var moviesAdapter: MoviesAdapter

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

        moviesAdapter = MoviesAdapter(movies)
        binding.moviesRecyclerView.adapter = moviesAdapter

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        binding.moviesRecyclerView.layoutManager =
            if (isLandscape) GridLayoutManager(this, 2) else LinearLayoutManager(this)

        fetchNowPlaying()
    }

    private fun fetchNowPlaying() {
        val client = AsyncHttpClient()
        val params = RequestParams()
        params["api_key"] = BuildConfig.TMDB_API_KEY

        client.get("https://api.themoviedb.org/3/movie/now_playing", params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                val results = json.jsonObject.optJSONArray("results")
                if (results == null) {
                    Toast.makeText(this@MainActivity, "No results returned", Toast.LENGTH_SHORT).show()
                    return
                }

                val parsedMovies = Movie.fromJsonArray(results)
                movies.clear()
                movies.addAll(parsedMovies)
                moviesAdapter.notifyDataSetChanged()
            }

            override fun onFailure(statusCode: Int, headers: Headers?, response: String, throwable: Throwable?) {
                Toast.makeText(this@MainActivity, "Request failed: $statusCode", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
