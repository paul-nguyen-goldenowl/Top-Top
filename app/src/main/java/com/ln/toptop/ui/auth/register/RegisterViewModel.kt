package com.ln.toptop.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.ln.toptop.R
import com.ln.toptop.data.network.auth.AuthRepo
import com.ln.toptop.data.network.user.UserRepo
import com.ln.toptop.ui.base.BaseViewModel
import com.ln.toptop.ui.base.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val userRepo: UserRepo
) : BaseViewModel() {
    val googleAuthRepo = authRepo.googleAuthRepo
    val liveCredential: LiveData<AuthCredential?>
        get() = authRepo.liveCredential
    val registerState = MutableLiveData<UiState>()

    fun registerWithCredential(credential: AuthCredential) {
        authRepo.signInWithCredential(credential)
            .onEach { resource ->
                registerState.value = UiState.LOADING
                handleResult(
                    resource,
                    onSuccess = {
                        userRepo.addUserToDatabase(it)
                        registerState.value = UiState.SUCCESS
                    },
                    onError = {
                        registerState.value = UiState.ERROR
                    },
                    message = R.string.fail_register
                )
            }.launchIn(viewModelScope)
    }
}