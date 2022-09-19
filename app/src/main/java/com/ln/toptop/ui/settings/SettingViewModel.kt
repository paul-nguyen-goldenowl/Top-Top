package com.ln.toptop.ui.settings

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ln.toptop.data.network.auth.AuthRepo
import com.ln.toptop.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authRepo: AuthRepo,
) : BaseViewModel() {

    val user = authRepo.liveUser
    val logOutState = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch {
            authRepo.checkAuthStatus()
        }
    }

    fun logOut(context: Context) {
        authRepo.signOut(context)
            .onEach {
                handleResult(it, onSuccess = { logOutState.postValue(true) })
            }
            .launchIn(viewModelScope)
    }

    fun checkAuthStatus() {
        viewModelScope.launch {
            authRepo.checkAuthStatus()
            Timber.d("currentUser.value=${currentUser.value}")
        }
    }

    val currentUser = authRepo.liveUser
}