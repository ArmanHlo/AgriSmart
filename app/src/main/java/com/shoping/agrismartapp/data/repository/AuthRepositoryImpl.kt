package com.shoping.agrismartapp.data.repository

import android.app.Activity
import android.net.Uri
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.shoping.agrismartapp.domain.model.User
import com.shoping.agrismartapp.domain.repository.AuthRepository
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
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
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
                            trySend(User(uid = firebaseUid, email = auth.currentUser?.email ?: "", phoneNumber = auth.currentUser?.phoneNumber ?: ""))
                            return@addSnapshotListener
                        }
                        val user = snapshot?.toObject(User::class.java)
                            ?: User(uid = firebaseUid, email = auth.currentUser?.email ?: "", phoneNumber = auth.currentUser?.phoneNumber ?: "")
                        
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
            val saveResult = saveUserProfile(user)
            if (saveResult.isSuccess) {
                Result.success(user)
            } else {
                Result.failure(saveResult.exceptionOrNull() ?: Exception("Failed to initialize user profile"))
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
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}
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
            firestore.collection("users").document(user.uid).set(user, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(uid: String): Result<User?> {
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            Result.success(snapshot.toObject(User::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadProfileImage(uri: Uri): Result<String> {
        val uid = currentUid ?: return Result.failure(Exception("User not logged in"))
        val ref = storage.reference.child("profile_images/$uid.jpg")
        return try {
            // Using continueWithTask to ensure downloadUrl is only fetched after successful upload
            val downloadUrl = ref.putFile(uri).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                ref.downloadUrl
            }.await().toString()
            
            val updates = mapOf("profileImageUrl" to downloadUrl)
            firestore.collection("users").document(uid).set(updates, SetOptions.merge()).await()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(Exception("Upload Failed: ${e.localizedMessage}"))
        }
    }
}
