package io.github.christianfajardo.randomuser.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.christianfajardo.randomuser.BuildConfig
import io.github.christianfajardo.randomuser.data.sources.RemoteDataSource
import io.github.christianfajardo.randomuser.utils.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {


        val levelType: HttpLoggingInterceptor.Level =
            if (BuildConfig.BUILD_TYPE.contentEquals("debug"))
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE


        return HttpLoggingInterceptor()
            .apply {
                level = levelType
            }
    }


    @Singleton
    @Provides
    fun providesOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .readTimeout(10L, TimeUnit.SECONDS)
            .writeTimeout(10L, TimeUnit.SECONDS)
            .build()


    @Singleton
    @Provides
    fun provideRxJavaCallAdapterFactory(): RxJava3CallAdapterFactory =
        RxJava3CallAdapterFactory.create()


    @Singleton
    @Provides
    fun provideGsonConverter(): GsonConverterFactory =
        GsonConverterFactory.create()


    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        rxAdapter: RxJava3CallAdapterFactory,
        gsonConverter: GsonConverterFactory
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(gsonConverter)
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addCallAdapterFactory(rxAdapter)
        .build()


    @Singleton
    @Provides
    fun provideRemoteDataSource(retrofit: Retrofit): RemoteDataSource =
        retrofit.create(RemoteDataSource::class.java)

}