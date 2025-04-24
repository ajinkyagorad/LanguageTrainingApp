package com.example.languagetrainingapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LanguageGameScreen(
    modifier: Modifier = Modifier,
    viewModel: LanguageGameViewModel = viewModel(),
    onSettingsClick: () -> Unit,
    onQuestionTypeSelected: (QuestionType) -> Unit,
    onAnswerSelected: (String) -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF89CFF0).copy(alpha = 0.7f), // Light blue
            Color(0xFF98FB98).copy(alpha = 0.7f)  // Light green
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            // Score and Level
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Score: ${gameState.score}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "Level ${gameState.level}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Question type selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuestionType.values().forEach { type ->
                    Button(
                        onClick = { onQuestionTypeSelected(type) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (gameState.questionType == type)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(type.name)
                    }
                }
            }

            // Game content
            Box(modifier = Modifier.weight(1f)) {
                // Question and answers
                if (gameState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Question
                        gameState.error?.let { error ->
                            Text(
                                text = error,
                                color = Color.Red,
                                modifier = Modifier.padding(16.dp)
                            )
                        } ?: run {
                            if (gameState.currentQuestion.isNotEmpty()) {
                                Text(
                                    text = gameState.currentQuestion,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(16.dp)
                                )

                                // Answer options
                                gameState.options.forEach { option ->
                                    val isSelected = option == gameState.selectedAnswer
                                    val isCorrect = option == gameState.correctAnswer
                                    val backgroundColor = when {
                                        !gameState.showFeedback -> Color.White.copy(alpha = 0.2f)
                                        isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.3f) // Green
                                        isSelected -> Color(0xFFE57373).copy(alpha = 0.3f) // Red
                                        else -> Color.White.copy(alpha = 0.2f)
                                    }

                                    Button(
                                        onClick = { onAnswerSelected(option) },
                                        enabled = !gameState.showFeedback,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = backgroundColor
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .padding(vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = option,
                                            color = Color.White,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Chat window
            AnimatedVisibility(
                visible = gameState.showChat,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp),
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        items(gameState.chatMessages) { message ->
                            Text(
                                text = message,
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }
            
            // Bottom bar with settings and chat toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp), // Increased padding
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
                
                IconButton(
                    onClick = { viewModel.toggleChat() }
                ) {
                    Icon(
                        imageVector = if (gameState.showChat) Icons.Filled.Info else Icons.Outlined.Info,
                        contentDescription = if (gameState.showChat) "Hide Chat" else "Show Chat",
                        tint = Color.White
                    )
                }
            }
        }
            }
        }

