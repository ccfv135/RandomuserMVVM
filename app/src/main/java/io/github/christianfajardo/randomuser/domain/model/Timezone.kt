package io.github.christianfajardo.randomuser.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Timezone(
    @SerializedName("offset") val offset: String,
    @SerializedName("description") val description: String
) : Parcelable

