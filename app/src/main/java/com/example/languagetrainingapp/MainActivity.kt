package com.example.languagetrainingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.languagetrainingapp.ui.theme.LanguageTrainingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LanguageTrainingAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    var showSettings by remember { mutableStateOf(false) }
                    
                    if (showSettings) {
                        SettingsScreen(onBackClick = { showSettings = false })
                    } else {
                        val viewModel: LanguageGameViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                        LanguageGameScreen(
                            onSettingsClick = { showSettings = true },
                            onQuestionTypeSelected = { type -> viewModel.setQuestionType(type) },
                            onAnswerSelected = { answer -> viewModel.checkAnswer(answer) }
                        )
                    }
                }
            }
        }
    }
}