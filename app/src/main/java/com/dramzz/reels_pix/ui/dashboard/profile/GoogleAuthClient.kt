package com.dramzz.reels_pix.ui.dashboard.profile

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

class GoogleAuthClient(
    private val context: Context
) {
    private val credentialManager = CredentialManager.create(context)
    private val auth = FirebaseAuth.getInstance()
    
    // The Web Client ID from google-services.json
    private val webClientId = "863267296983-nquocvnb2811a59fhnulq9evheaoo2g1.apps.googleusercontent.com"

    suspend fun signIn(): Result<GoogleUser> {
        return try {
            val rawNonce = UUID.randomUUID().toString()
            val bytes = rawNonce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setNonce(hashedNonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            handleSignInResult(result)
            val user = auth.currentUser
            if (user != null) {
                val names = user.displayName?.split(" ") ?: listOf("", "")
                val firstName = names.firstOrNull() ?: ""
                val lastName = if (names.size > 1) names.drop(1).joinToString(" ") else ""
                val googleUser = GoogleUser(
                    id = user.uid,
                    email = user.email ?: "",
                    firstName = firstName,
                    lastName = lastName
                )
                Result.success(googleUser)
            } else {
                Result.failure(Exception("Firebase user is null"))
            }
        } catch (e: GetCredentialCancellationException) {
            Log.d("GoogleAuthClient", "Sign-in was canceled")
            Result.failure(e)
        } catch (e: GetCredentialException) {
            Log.e("GoogleAuthClient", "Sign-in failed", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("GoogleAuthClient", "Sign-in error", e)
            Result.failure(e)
        }
    }

    private suspend fun handleSignInResult(result: GetCredentialResponse) {
        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken
            
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(firebaseCredential).await()
        } else {
            throw RuntimeException("Received an unexpected credential type.")
        }
    }
}
