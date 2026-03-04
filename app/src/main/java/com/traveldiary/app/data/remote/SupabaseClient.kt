package com.traveldiary.app.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseManager {

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://ixtseebawqyybnjwtkkt.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml4dHNlZWJhd3F5eWJuand0a2t0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzI0MzE2ODcsImV4cCI6MjA4ODAwNzY4N30.aLqwEG0AcxxfKwN0BUjtiRPGugOG6UJt2ij4YOZ8P-o"
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }
}
