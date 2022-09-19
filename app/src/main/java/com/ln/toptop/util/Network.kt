package com.ln.toptop.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

fun <T> safeAccess(block: suspend () -> T): Flow<Resource<T>> = flow {
    emit(Resource.Loading)
    emit(Resource.Success(block()))
}
    .catch { e ->
        Timber.e(e as Exception)
        emit(Resource.Error(e))
    }
    .flowOn(Dispatchers.IO)