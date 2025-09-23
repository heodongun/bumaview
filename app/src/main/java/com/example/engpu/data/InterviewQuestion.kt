package com.example.engpu.data

data class InterviewQuestion(
    val id: Int,
    val question: String,
    val category: String = "일반",
    val isBookmarked: Boolean = false
)

object InterviewQuestionRepository {
    val questions = listOf(
        InterviewQuestion(1, "자기소개를 해주세요"),
        InterviewQuestion(2, "지원 동기는 무엇인가요?"),
        InterviewQuestion(3, "본인의 장점과 단점을 말해주세요"),
        InterviewQuestion(4, "5년 후 자신의 모습은?"),
        InterviewQuestion(5, "왜 이 회사를 선택했나요?"),
        InterviewQuestion(6, "팀워크 경험에 대해 말해주세요"),
        InterviewQuestion(7, "스트레스 관리 방법은?"),
        InterviewQuestion(8, "실패 경험과 극복 과정은?"),
        InterviewQuestion(9, "리더십 경험이 있나요?"),
        InterviewQuestion(10, "회사에 기여할 수 있는 부분은?"),
        InterviewQuestion(11, "가장 성취감을 느꼈던 경험은?"),
        InterviewQuestion(12, "갈등 상황을 해결한 경험이 있나요?"),
        InterviewQuestion(13, "새로운 기술을 배우는 방법은?"),
        InterviewQuestion(14, "시간 관리는 어떻게 하시나요?"),
        InterviewQuestion(15, "도전적인 프로젝트 경험은?")
    )
    
    fun getRandomQuestion(): InterviewQuestion {
        return questions.random()
    }
    
    fun searchQuestions(query: String): List<InterviewQuestion> {
        return questions.filter { 
            it.question.contains(query, ignoreCase = true) 
        }
    }
}
