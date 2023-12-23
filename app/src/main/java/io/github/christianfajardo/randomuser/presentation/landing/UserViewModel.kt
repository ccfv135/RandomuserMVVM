package io.github.christianfajardo.randomuser.presentation.landing


import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.christianfajardo.randomuser.domain.model.User
import io.github.christianfajardo.randomuser.domain.repository.RandomUserRepository
import io.github.christianfajardo.randomuser.utils.NetworkUtilities
import io.github.christianfajardo.randomuser.utils.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject








@HiltViewModel
class UserViewModel
@Inject constructor(private var repo: RandomUserRepository) : ViewModel() {



    private var userListFlowablePrivate: Flowable<Resource<List<User>>> =
        repo.getUserList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .cache()




    fun getUsers(){
        userListFlowablePrivate=repo.getUserList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .cache()
    }


    private var internetStatusPrivate: Observable<Boolean> = NetworkUtilities
        .observerNetwork()
        .observeOn(AndroidSchedulers.mainThread())



    var userListFlowable: Flowable<Resource<List<User>>> = userListFlowablePrivate

    var internetStatus: Observable<Boolean> = internetStatusPrivate





}