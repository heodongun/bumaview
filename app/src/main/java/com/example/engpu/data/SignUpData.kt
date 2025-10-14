package com.example.engpu.data

data class SignUpData(
    val name: String = "",
    val jobPosition: String = "", // 희망직무 (카테고리)
    val email: String = "",
    val verificationCode: String = "",
    val interviewTime: String = "", // "14:30" 형식의 알림 시간
    val password: String = "",
    val confirmPassword: String = "",
    val isEmailVerified: Boolean = false // 이메일 인증 완료 여부
)