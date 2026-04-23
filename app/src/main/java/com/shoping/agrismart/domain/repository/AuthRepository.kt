package com.shoping.agrismart.domain.repository

import android.app.Activity
import com.shoping.agrismart.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    val currentUid: String?
    
    // Email/Password Authentication
    suspend fun signUpWithEmail(email: String, password: String): Result<User>
    suspend fun signInWithEmail(email: String, password: String): Result<User>

    // Phone Authentication
    suspend fun sendOtp(phoneNumber: String, activity: Activity): Result<Unit>
    suspend fun verifyOtp(otpCode: String): Result<User>

    suspend fun logout()
    suspend fun saveUserProfile(user: User): Result<Unit>
    suspend fun getUserProfile(uid: String): Result<User?>
}
