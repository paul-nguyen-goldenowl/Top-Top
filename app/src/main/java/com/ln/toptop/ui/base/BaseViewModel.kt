package com.ln.toptop.ui.base

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.ln.toptop.util.Resource
import timber.log.Timber

open class BaseViewModel : ViewModel() {
    private val _snackBarMessageRes = MutableLiveData<Int?>()
    val snackBarMessageRes: LiveData<Int?> = _snackBarMessageRes.distinctUntilChanged()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading.distinctUntilChanged()

    fun showMessage(@StringRes messageRes: Int) {
        _snackBarMessageRes.value = messageRes
    }

    fun clearMessage() {
        _snackBarMessageRes.value = null
    }

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    suspend fun <T> handleResult(
        resource: Resource<T>,
        onSuccess: (suspend (data: T) -> Unit)? = null,
        onError: (suspend (exception: Exception) -> Unit)? = null,
        @StringRes message: Int? = null
    ) {
        when (resource) {
            is Resource.Loading -> {
                setLoading(true)
            }
            is Resource.Success -> {
                setLoading(false)
                onSuccess?.invoke(resource.data)
            }
            is Resource.Error -> {
                setLoading(false)
                Timber.e(resource.exception, "Error happened.")
                onError?.invoke(resource.exception)
            }
        }
        message?.let { showMessage(it) }
    }
}