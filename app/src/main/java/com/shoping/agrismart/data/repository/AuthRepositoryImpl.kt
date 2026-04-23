package com.shoping.agrismart.data.repository

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.shoping.agrismart.domain.model.User
import com.shoping.agrismart.domain.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    private var verificationId: String? = null

    override val currentUid: String?
        get() = auth.currentUser?.uid

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.flatMapLatest { firebaseUid ->
        if (firebaseUid != null) {
            callbackFlow {
                val subscription = firestore.collection("users").document(firebaseUid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            // On error (e.g. Permission Denied), provide a basic user object from Auth
                            trySend(User(uid = firebaseUid, email = auth.currentUser?.email ?: "", phoneNumber = auth.currentUser?.phoneNumber ?: ""))
                            return@addSnapshotListener
                        }
                        val user = snapshot?.toObject(User::class.java)
                            ?: User(uid = firebaseUid, email = auth.currentUser?.email ?: "", phoneNumber = auth.currentUser?.phoneNumber ?: "")
                        
                        // Ensure email is always present from Firebase Auth if missing in Firestore
                        val finalUser = if (user.email.isBlank() && auth.currentUser?.email != null) {
                            user.copy(email = auth.currentUser?.email!!)
                        } else {
                            user
                        }
                        trySend(finalUser)
                    }
                awaitClose { subscription.remove() }
            }
        } else {
            flowOf(null)
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user!!
            val user = User(uid = firebaseUser.uid, email = email)
            // Ensure we wait for the profile to be saved before succeeding
            val saveResult = saveUserProfile(user)
            if (saveResult.isSuccess) {
                Result.success(user)
            } else {
                Result.failure(saveResult.exceptionOrNull() ?: Exception("Failed to initialize user profile in database"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user!!
            val user = getUserProfile(firebaseUser.uid).getOrNull() ?: User(uid = firebaseUser.uid, email = email)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendOtp(phoneNumber: String, activity: Activity): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Auto-verification handling could be added here
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                this@AuthRepositoryImpl.verificationId = verificationId
                if (continuation.isActive) continuation.resume(Result.success(Unit))
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override suspend fun verifyOtp(otpCode: String): Result<User> {
        val id = verificationId ?: return Result.failure(Exception("Verification ID is null"))
        return try {
            val credential = PhoneAuthProvider.getCredential(id, otpCode)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user!!
            
            // Check if profile exists, if not, create it
            val existingProfile = getUserProfile(firebaseUser.uid).getOrNull()
            if (existingProfile == null) {
                val newUser = User(uid = firebaseUser.uid, phoneNumber = firebaseUser.phoneNumber ?: "")
                saveUserProfile(newUser)
                Result.success(newUser)
            } else {
                Result.success(existingProfile)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun saveUserProfile(user: User): Result<Unit> {
        return try {
            firestore.collection("users").document(user.uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(uid: String): Result<User?> {
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            val user = snapshot.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
