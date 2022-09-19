package com.ln.toptop.ui.auth.login

import androidx.lifecycle.LiveData
import com.google.firebase.auth.AuthCredential
import com.ln.toptop.data.network.auth.AuthRepo
import com.ln.toptop.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val authRepo: AuthRepo
) : BaseViewModel() {
    val googleAuthRepo = authRepo.GoogleAuthRepo()
    val liveCredential: LiveData<AuthCredential?>
        get() = authRepo.liveCredential
}