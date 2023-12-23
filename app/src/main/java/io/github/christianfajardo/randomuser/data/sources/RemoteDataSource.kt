package io.github.christianfajardo.randomuser.data.sources

import io.github.christianfajardo.randomuser.domain.model.ApiResponse
import io.reactivex.rxjava3.core.Flowable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * remove data source for the random friends list
 *
 * that will fetch the data from the API,
 *
 * in the [ApiResponse] format
 *
 * wrapped in a [Call] class
 */
interface RemoteDataSource {

    @GET(".")
    fun getResponse(
        @Query("results") resultAmount: Int = 100
    ): Flowable<ApiResponse>
}