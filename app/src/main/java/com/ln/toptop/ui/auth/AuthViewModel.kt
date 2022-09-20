package com.ln.toptop.ui.auth

import androidx.lifecycle.viewModelScope
import com.ln.toptop.data.network.auth.AuthRepo
import com.ln.toptop.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepo,
) : BaseViewModel() {

    val user = authRepo.liveUser

    init {
        viewModelScope.launch {
            authRepo.checkAuthStatus()
        }
    }

    fun checkAuthStatus() {
        viewModelScope.launch {
            authRepo.checkAuthStatus()
        }
    }

    val currentUser = authRepo.liveUser
}