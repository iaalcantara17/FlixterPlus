package com.example.flixterplus

import org.json.JSONArray
import org.json.JSONObject

data class Movie(
    val title: String,
    val overview: String,
    val posterPath: String?
) {
    val posterUrl: String?
        get() = posterPath?.let { "https://image.tmdb.org/t/p/w500/${it.trimStart('/')}" }

    companion object {
        fun fromJson(jsonObject: JSONObject): Movie {
            val title = jsonObject.optString("title", "")
            val overview = jsonObject.optString("overview", "")
            val posterPath = if (jsonObject.isNull("poster_path")) null else jsonObject.optString("poster_path", null)
            return Movie(title = title, overview = overview, posterPath = posterPath)
        }

        fun fromJsonArray(jsonArray: JSONArray): List<Movie> {
            val movies = ArrayList<Movie>(jsonArray.length())
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.optJSONObject(i) ?: continue
                movies.add(fromJson(obj))
            }
            return movies
        }
    }
}
