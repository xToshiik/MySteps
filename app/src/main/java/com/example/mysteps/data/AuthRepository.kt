package com.example.mysteps.data

import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {
    suspend fun signUpWithEmail(email: String, password: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                SupabaseClient.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                Unit
            }
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                SupabaseClient.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                Unit
            }
        }
    }
    suspend fun saveUserToTable(user: UserDto): Result<Unit> {
        return runCatching {
            SupabaseClient.client.postgrest["users"].insert(user)
        }
    }


    fun getCurrentUserOd(): String? {
        return SupabaseClient.client.auth.currentSessionOrNull()?.user?.id
    }
}