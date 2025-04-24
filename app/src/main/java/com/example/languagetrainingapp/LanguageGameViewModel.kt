package com.example.languagetrainingapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.languagetrainingapp.database.AppDatabase
import com.example.languagetrainingapp.database.QuestionEntity
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class LanguageGameViewModel(application: Application) : AndroidViewModel(application) {
    private val questionDao = AppDatabase.getDatabase(application).questionDao()
    private var lastRequestTime = 0L
    private val MIN_REQUEST_INTERVAL = 2000L // 2 seconds minimum between requests
    private val settingsRepository = SettingsRepository(application)
    private var generativeModel: GenerativeModel? = null
    
    private var currentApiKey: String? = null
    private var currentModel: String? = null

    init {
        viewModelScope.launch {
            // Collect API key changes
            settingsRepository.apiKey.collect { apiKey ->
                currentApiKey = apiKey
                updateGenerativeModel()
            }
        }

        viewModelScope.launch {
            // Collect model changes
            settingsRepository.selectedModel.collect { model ->
                currentModel = model
                updateGenerativeModel()
            }
        }
    }

    private fun updateGenerativeModel() {
        val apiKey = currentApiKey
        val model = currentModel

        if (apiKey != null && model != null) {
            generativeModel = GenerativeModel(
                modelName = model,
                apiKey = apiKey
            )
        }
    }

    private val _gameState = MutableStateFlow(LanguageGameState())
    val gameState: StateFlow<LanguageGameState> = _gameState.asStateFlow()

    init {
        generateNewQuestion()
    }

    fun toggleChat() {
        _gameState.update { it.copy(showChat = !it.showChat) }
    }

    private fun logMessage(message: String) {
        _gameState.update { 
            it.copy(chatMessages = it.chatMessages + message)
        }
    }

    fun generateNewQuestion() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastRequestTime < MIN_REQUEST_INTERVAL) {
            _gameState.update { 
                it.copy(error = "Please wait a moment before generating a new question.")
            }
            return
        }
        lastRequestTime = currentTime
        
        viewModelScope.launch {
            _gameState.update { it.copy(isLoading = true, error = null) }
            try {
                val currentLevel = calculateLevel(_gameState.value.score)
                
                // Try to get a question from the database first
                var question = questionDao.getRandomQuestionExcludingRecent(
                    type = _gameState.value.questionType,
                    level = currentLevel
                )
                
                // If no question in database or we have too few questions, generate a new one
                if (question == null || questionDao.getQuestionCount(_gameState.value.questionType, currentLevel) < 10) {
                    val prompt = GeminiPrompt(
                        questionType = _gameState.value.questionType,
                        level = currentLevel
                    )

                    val model = generativeModel ?: throw Exception("Please set your API key in settings")
                logMessage("Generating new question...")
                    val response = model.generateContent(prompt.toPromptString())
                    val content = response.text?.trim() ?: throw Exception("Empty response from Gemini")

                    // Parse the response
                    val lines = content.lines()
                    val finnish = lines[0].substringAfter(": ")
                    val options = lines.subList(1, 5).map { it.substringAfter(". ") }
                    val correctIndex = lines.last().substringAfter("Correct: ").toInt() - 1
                    
                    // Save to database
                    question = QuestionEntity(
                        finnish = finnish,
                        correctAnswer = options[correctIndex],
                        wrongAnswers = options.filterIndexed { index, _ -> index != correctIndex },
                        type = _gameState.value.questionType,
                        difficulty = currentLevel
                    )
                    questionDao.insertQuestion(question)
                    logMessage("Generated and saved new question")
                } else {
                    // Update usage statistics
                    questionDao.updateQuestionUsage(question.id)
                    logMessage("Using existing question from database")
                }
                
                // Shuffle wrong answers with correct answer
                val allOptions = (question.wrongAnswers + question.correctAnswer).toList().shuffled()
                
                _gameState.update {
                    it.copy(
                        currentQuestion = question.finnish,
                        options = allOptions,
                        correctAnswer = question.correctAnswer,
                        isLoading = false,
                        level = currentLevel
                    )
                }
            } catch (e: Exception) {
                Log.e("LanguageGame", "Error generating question", e)
                _gameState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to generate question: ${e.message}"
                    )
                }
            }
        }
    }

    fun checkAnswer(selectedAnswer: String) {
        val isCorrect = selectedAnswer == _gameState.value.correctAnswer
        _gameState.update { 
            it.copy(
                selectedAnswer = selectedAnswer,
                showFeedback = true,
                score = if (isCorrect) it.score + calculatePoints(it.level, it.questionType) else it.score
            )
        }
        
        // Wait for 1.5 seconds to show feedback before moving to next question
        viewModelScope.launch {
            delay(1500)
            generateNewQuestion()
        }
    }

    fun setQuestionType(type: QuestionType) {
        _gameState.update { it.copy(questionType = type) }
        generateNewQuestion()
    }

    private fun calculateLevel(score: Int): Int = when {
        score < 100 -> 1
        score < 300 -> 2
        score < 600 -> 3
        score < 1000 -> 4
        else -> 5
    }

    private fun calculatePoints(level: Int, type: QuestionType): Int {
        val basePoints = when (type) {
            QuestionType.WORD -> 10
            QuestionType.PHRASE -> 20
            QuestionType.SENTENCE -> 30
        }
        return basePoints * level
    }
}
