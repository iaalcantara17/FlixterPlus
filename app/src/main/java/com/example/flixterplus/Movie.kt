package com.example.flixterplus

import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val originalLanguage: String
) : Serializable {

    val posterUrl: String?
        get() = posterPath?.let { "https://image.tmdb.org/t/p/w500/${it.trimStart('/')}" }

    val backdropUrl: String?
        get() = backdropPath?.let { "https://image.tmdb.org/t/p/w500/${it.trimStart('/')}" }

    companion object {
        fun fromJson(jsonObject: JSONObject): Movie {
            return Movie(
                id = jsonObject.optInt("id", -1),
                title = jsonObject.optString("title", ""),
                overview = jsonObject.optString("overview", ""),
                posterPath = if (jsonObject.isNull("poster_path")) null else jsonObject.optString("poster_path", null),
                backdropPath = if (jsonObject.isNull("backdrop_path")) null else jsonObject.optString("backdrop_path", null),
                releaseDate = if (jsonObject.isNull("release_date")) null else jsonObject.optString("release_date", null),
                voteAverage = jsonObject.optDouble("vote_average", 0.0),
                voteCount = jsonObject.optInt("vote_count", 0),
                popularity = jsonObject.optDouble("popularity", 0.0),
                originalLanguage = jsonObject.optString("original_language", "")
            )
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