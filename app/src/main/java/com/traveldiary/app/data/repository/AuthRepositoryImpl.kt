package com.traveldiary.app.data.repository

import com.traveldiary.app.data.remote.SupabaseManager
import com.traveldiary.app.domain.repository.AuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepositoryImpl : AuthRepository {

//    override suspend fun loginWithGoogle() {
//        SupabaseManager.client.auth.signInWith(Google)
//    }

    override suspend fun login(email: String, password: String) {
        SupabaseManager.client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun register(email: String, password: String) {
        SupabaseManager.client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun logout() {
        SupabaseManager.client.auth.signOut()
    }

    override fun isUserLoggedIn(): Boolean {
        return SupabaseManager.client.auth.currentSessionOrNull() != null
    }
}