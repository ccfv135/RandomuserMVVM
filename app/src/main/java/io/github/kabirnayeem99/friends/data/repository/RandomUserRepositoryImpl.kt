package io.github.kabirnayeem99.friends.data.repository

import io.github.kabirnayeem99.friends.data.sources.RemoteDataSource
import io.github.kabirnayeem99.friends.domain.model.ApiResponse
import io.github.kabirnayeem99.friends.domain.model.User
import io.github.kabirnayeem99.friends.domain.repository.RandomUserRepository
import io.github.kabirnayeem99.friends.utils.Resource
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject


class RandomUserRepositoryImpl @Inject constructor(private var remoteDataSource: RemoteDataSource) :
    RandomUserRepository {

    override fun getUserList(): Flowable<Resource<List<User>>> {
        return remoteDataSource.getResponse()
            .retry(3)
            .observeOn(Schedulers.io())
            .map { apiResponse ->
                Resource.Success(apiResponse.userList) as Resource<List<User>>
            }
            .onErrorReturnItem(Resource.Error("An error has occurred"))
    }
}









