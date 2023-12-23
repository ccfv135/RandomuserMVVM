package io.github.christianfajardo.randomuser.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import io.github.christianfajardo.randomuser.presentation.landing.UserAdapter
import io.reactivex.rxjava3.disposables.CompositeDisposable


@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {

    @Provides
    fun provideUserAdapter(): UserAdapter = UserAdapter()

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

}