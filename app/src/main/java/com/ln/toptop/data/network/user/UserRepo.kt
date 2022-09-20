package com.ln.toptop.data.network.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ln.toptop.model.User
import kotlinx.coroutines.tasks.await

class UserRepo(
    private val fireAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    companion object {
        const val USERS_PATH = "users"
    }

    private val userDb = firestore.collection(USERS_PATH)
    suspend fun addUserToDatabase(
        user: User
    ) {
        userDb.document(user.uid).apply {
            set(user).await()
        }
    }
}