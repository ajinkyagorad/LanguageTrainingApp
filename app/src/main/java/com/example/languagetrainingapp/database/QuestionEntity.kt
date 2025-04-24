package com.example.languagetrainingapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.languagetrainingapp.QuestionType

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val finnish: String,
    val correctAnswer: String,
    val wrongAnswers: List<String>,
    val type: QuestionType,
    val difficulty: Int,
    val lastUsed: Long = System.currentTimeMillis(),
    val timesUsed: Int = 0
)
