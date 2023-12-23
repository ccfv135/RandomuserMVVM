package io.github.kabirnayeem99.friends.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Registered(

    @SerializedName("date") val date: String,
    @SerializedName("age") val age: Int,

) : Parcelable