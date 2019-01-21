package com.kshitijchauhan.haroldadmin.moviedb.remote.service.movie

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface MovieService {

    @GET("movie/{movieId}")
    fun getMovieDetails(@Path("movieId") movieId: Int): Single<Movie>

    @GET("movie/{movieId}/account_states")
    fun getAccountStatesForMovie(@Path("movieId") movieId: Int): Single<MovieStatesResponse>

    @GET("movie/{movieId}/videos")
    fun getVideosForMovie(@Path("movieId") movieId: Int): Single<MovieVideosResponse>

    @GET("movie/{movieId}/credits")
    fun getCreditsForMovie(@Path("movieId") movieId: Int): Single<MovieCreditsResponse>
}