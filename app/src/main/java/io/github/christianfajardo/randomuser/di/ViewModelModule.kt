package io.github.christianfajardo.randomuser.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.github.christianfajardo.randomuser.data.repository.RandomUserRepositoryImpl
import io.github.christianfajardo.randomuser.data.sources.RemoteDataSource
import io.github.christianfajardo.randomuser.domain.repository.RandomUserRepository

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

    @Provides
    fun providesRepository(remoteDataSource: RemoteDataSource): RandomUserRepository =
        RandomUserRepositoryImpl(remoteDataSource)

}