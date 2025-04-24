package com.example.languagetrainingapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    var apiKey by remember { mutableStateOf("") }
    val savedApiKey by viewModel.apiKey.collectAsState(initial = null)
    val availableModels by viewModel.availableModels.collectAsState(initial = emptyList())
    val selectedModel by viewModel.selectedModel.collectAsState(initial = null)

    LaunchedEffect(savedApiKey) {
        savedApiKey?.let { apiKey = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("Gemini API Key") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Button(
                onClick = { viewModel.saveApiKey(apiKey) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save")
            }

            if (savedApiKey != null) {
                Text(
                    "API Key is set",
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Available Models",
                    style = MaterialTheme.typography.titleMedium
                )

                if (availableModels.isNotEmpty()) {
                    Column {
                        availableModels.forEach { model ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = model == selectedModel,
                                    onClick = { viewModel.selectModel(model) }
                                )
                                Text(
                                    text = model,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        "No models available. Please check your API key.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
