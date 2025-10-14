package com.example.engpu.data.supabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import kotlinx.serialization.Serializable
import java.util.UUID

// Supabase Client 인스턴스
val supabase = createSupabaseClient(
    supabaseUrl = "https://danfonxiwappznsvoiik.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRhbmZvbnhpd2FwcHpuc3ZvaWlrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTg1MzUzMjUsImV4cCI6MjA3NDExMTMyNX0.RmpkDPfCQq3qOHxG8fRuHY8gkUUxXgeeCOWGHFq7SDc"
) {
    install(Auth)
    install(Postgrest)
    install(Realtime)
}

// Data Models
@Serializable
data class User(
    val id: String = "",
    val name: String,
    val category: String,
    val grade: String = "USER",
    val email: String,
    val password: String = "",
    val updated_at: String? = null,
    val created_at: String? = null,
    val alarm_at: String? = null
)

@Serializable
data class Question(
    val id: String = UUID.randomUUID().toString(),
    val question: String,
    val question_at: Int? = null,
    val company: String? = null,
    val category: String? = null,
    val updated_at: String? = null,
    val created_at: String? = null
)

@Serializable
data class Interview(
    val created_at: String,
    val user_id: String,
    val question_id: String,
    val answer: String? = null,
    val score: Int? = null,
    val group_id: Int? = null,
    val feedback: String? = null
)

// Auth Response Models
@Serializable
data class AuthResponse(
    val user: AuthUser? = null,
    val session: Session? = null,
    val error: String? = null
)

@Serializable
data class AuthUser(
    val id: String,
    val email: String,
    val created_at: String
)

@Serializable
data class Session(
    val access_token: String,
    val refresh_token: String,
    val expires_in: Int
)