package io.github.christianfajardo.randomuser.domain.model

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("results") val userList: List<User>
)
