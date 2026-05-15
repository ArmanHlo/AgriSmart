package com.shoping.agrismartapp.domain.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val location: String = "",
    val farmSize: String = "",
    val primaryCrop: String = "",
    val profileImageUrl: String? = null
)
