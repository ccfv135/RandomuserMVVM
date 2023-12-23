package io.github.christianfajardo.randomuser.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coordinates(

    @SerializedName("latitude") val latitude: String,
    @SerializedName("longitude") val longitude: String,

) : Parcelable