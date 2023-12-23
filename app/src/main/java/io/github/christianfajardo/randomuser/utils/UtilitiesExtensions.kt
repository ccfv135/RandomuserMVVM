package io.github.christianfajardo.randomuser.utils

import android.util.Log
import io.github.christianfajardo.randomuser.BuildConfig

fun Exception.print(tag: String, functionName: String) {
    if (BuildConfig.DEBUG) {
        Log.e(
            tag, "$functionName: ${
                this.message ?: "An unknown error has occurred"
            }", this
        )

    }
}

