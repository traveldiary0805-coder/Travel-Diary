package com.traveldiary.app.data.repository

import com.traveldiary.app.data.remote.SupabaseManager
import com.traveldiary.app.domain.repository.AuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken

class AuthRepositoryImpl : AuthRepository {

    override suspend fun loginWithGoogle() {
        SupabaseManager.client.auth.signInWith(
            Google,
            redirectUrl = "traveldiary://login-callback"
        )
    }

    override suspend fun loginWithGoogleToken(idToken: String) {
        SupabaseManager.client.auth.signInWith(IDToken) {
            provider = Google
            this.idToken = idToken
        }
    }

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