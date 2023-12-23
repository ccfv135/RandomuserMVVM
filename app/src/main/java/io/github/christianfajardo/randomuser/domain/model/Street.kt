package io.github.christianfajardo.randomuser.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Street(
    @SerializedName("number") val number: Int,
    @SerializedName("name") val name: String
) : Parcelable
