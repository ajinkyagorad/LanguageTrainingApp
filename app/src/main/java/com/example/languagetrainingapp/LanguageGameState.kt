package com.example.languagetrainingapp

data class LanguageGameState(
    val score: Int = 0,
    val level: Int = 1,
    val currentQuestion: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: String = "",
    val selectedAnswer: String? = null,
    val showFeedback: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val questionType: QuestionType = QuestionType.WORD,
    val showChat: Boolean = false,
    val chatMessages: List<String> = emptyList()
)

enum class QuestionType {
    WORD,
    PHRASE,
    SENTENCE
}

data class GeminiPrompt(
    val language: String = "Finnish",
    val questionType: QuestionType,
    val level: Int
) {
    fun toPromptString(): String = """
        Act as a language tutor. Generate a language learning question in $language with the following parameters:
        - Type: ${questionType.name.lowercase()}
        - Difficulty level: $level (1-5, where 5 is most difficult)
        
        Please provide:
        1. A ${questionType.name.lowercase()} in $language
        2. Four possible translations in English, with exactly one correct answer
        3. Mark which answer is correct (number 1-4)
        
        Format your response exactly like this example:
        Word: talo
        1. house
        2. car
        3. book
        4. tree
        Correct: 1
        
        Make the incorrect options more similar/confusing as the level increases.
    """.trimIndent()
}
