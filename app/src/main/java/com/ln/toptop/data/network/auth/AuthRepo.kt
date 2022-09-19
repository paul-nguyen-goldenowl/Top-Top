package com.ln.toptop.data.network.auth

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ln.toptop.BuildConfig
import com.ln.toptop.data.datastore.AppDataStoreManager
import com.ln.toptop.model.User
import com.ln.toptop.util.safeAccess
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class AuthRepo(
    private val firebaseAuth: FirebaseAuth,
    private val appDataStore: AppDataStoreManager
) {
    var liveUser = MutableLiveData<User?>()

    private val _liveCredential = MutableLiveData<AuthCredential?>()
    val liveCredential: LiveData<AuthCredential?> = _liveCredential
    val googleAuthRepo = GoogleAuthRepo()

    fun signInWithCredential(credential: AuthCredential) = safeAccess {
        val authResult = firebaseAuth.signInWithCredential(credential).await()
        val user: User
        authResult.user!!.let {
            user = User(it.uid, it.displayName, it.photoUrl?.toString())
            appDataStore.setUser(user)
            liveUser.postValue(user)
        }
        user
    }

    fun signOut(context: Context) = safeAccess {
        firebaseAuth.signOut()
        googleAuthRepo.signOut(context)
        _liveCredential.postValue(null)
        liveUser.postValue(null)
        appDataStore.clear()
        Timber.d("sign out success.")
        true
    }

    suspend fun checkAuthStatus() {
        liveUser.value = appDataStore.getUser()
    }

    inner class GoogleAuthRepo {
        lateinit var googleSignInClient: GoogleSignInClient
        private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .requestProfile()
            .requestEmail()
            .build()

        fun doGoogleAuth(context: Context, launcher: ActivityResultLauncher<Intent>) {
            googleSignInClient = GoogleSignIn.getClient(context, gso)
            launcher.launch(googleSignInClient.signInIntent)
        }

        fun signOut(context: Context) {
            if (!this::googleSignInClient.isInitialized)
                googleSignInClient = GoogleSignIn.getClient(context, gso)
            googleSignInClient.signOut()
        }

        fun handleGoogleOnResult(data: Intent?) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                _liveCredential.value = credential
            } catch (apiException: ApiException) {
                Timber.e(apiException, "Caught exception")
            }
        }
    }
}

