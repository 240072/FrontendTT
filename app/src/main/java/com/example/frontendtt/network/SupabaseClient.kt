package com.example.traveltogethersupabase.network

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest


object SupabaseClient {
    val supabase = createSupabaseClient(
        supabaseUrl = "https://utfvicgpgyhcuqgeagcf.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InV0ZnZpY2dwZ3loY3VxZ2VhZ2NmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjgyMzkwMTYsImV4cCI6MjA4MzgxNTAxNn0.RZ3eaYNRL3h-TzzwyTv3Y43Ta6uyQGxPQcmhmAvf0kE"
    ) {
        install(Auth)
        install(Postgrest)
    }

}