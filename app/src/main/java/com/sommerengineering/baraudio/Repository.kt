package com.sommerengineering.baraudio

import android.net.Uri
import org.koin.core.annotation.Single

data class User(
    val idToken: String,
    val profilePictureUri: Uri
)

@Single
class Repository {

    lateinit var user: User

}