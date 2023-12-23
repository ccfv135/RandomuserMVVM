package io.github.christianfajardo.randomuser.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    @SerializedName("name") val name: Name,
    @SerializedName("gender") val gender: String,
    @SerializedName("location") val location: Location,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("cell") val cell: String,
    @SerializedName("picture") val picture: Picture,
    @SerializedName("registered") val registered: Registered,
    @SerializedName("coordinates") val coordinates: Coordinates?

) : Parcelable