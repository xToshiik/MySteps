package com.example.mysteps.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    private const val SUPABASE_URL = "https://doaknswdtxqwoskpqusp.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRvYWtuc3dkdHhxd29za3BxdXNwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTA3MzEyMTQsImV4cCI6MjEwNjMwNzIxNH0.gXeGiLUf5TAgT635_oOxF5xYWxoJDitsKAumeVHe6Bk"
    val client = createSupabaseClient(SUPABASE_URL, SUPABASE_ANON_KEY) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }
}