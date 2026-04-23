package com.shoping.agrismart.presentation.auth

import com.shoping.agrismart.domain.model.User

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val email: String = "",
    val password: String = "",
    val phoneNumber: String = "",
    val otpCode: String = "",
    val isOtpSent: Boolean = false,
    val isVerified: Boolean = false,
    val isRegistered: Boolean = false,
    val isProfileSaved: Boolean = false,
    val user: User? = null
)
