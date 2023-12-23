package io.github.christianfajardo.randomuser.domain.repository

import io.github.christianfajardo.randomuser.domain.model.User
import io.github.christianfajardo.randomuser.utils.Resource
import io.reactivex.rxjava3.core.Flowable


interface RandomUserRepository {

    fun getUserList(): Flowable<Resource<List<User>>>

}