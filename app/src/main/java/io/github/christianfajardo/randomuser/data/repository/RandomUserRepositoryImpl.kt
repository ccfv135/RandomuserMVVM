package io.github.christianfajardo.randomuser.data.repository

import io.github.christianfajardo.randomuser.data.sources.RemoteDataSource
import io.github.christianfajardo.randomuser.domain.model.User
import io.github.christianfajardo.randomuser.domain.repository.RandomUserRepository
import io.github.christianfajardo.randomuser.utils.Resource
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









