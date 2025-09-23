package com.example.engpu.data

data class SignUpData(
    val name: String = "",
    val jobPosition: String = "", // 희망직무
    val email: String = "",
    val verificationCode: String = "",
    val interviewTime: String = "", // 면접 질문 시간
    val password: String = "",
    val confirmPassword: String = ""
)

enum class SignUpStep {
    NAME,           // 이름 입력
    JOB_POSITION,   // 희망직무 입력
    EMAIL,          // 이메일 입력
    VERIFICATION,   // 확인코드 입력
    INTERVIEW_TIME, // 면접 질문 시간 설정
    PASSWORD        // 비밀번호 설정
}
