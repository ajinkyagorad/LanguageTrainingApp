# Language Training App

An Android application that helps users learn languages through interactive exercises powered by Google's Gemini AI.

## Features

- Interactive language learning exercises
- AI-powered question generation and feedback
- Multiple question types (vocabulary, grammar, translation)
- Chat interface for natural language interaction
- Customizable AI model selection
- Secure API key management

## Technical Stack

- **Language**: Kotlin
- **Framework**: Android Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **AI Integration**: Google's Gemini AI API
- **Data Storage**: DataStore for preferences
- **UI**: Material Design 3

## Setup

1. Clone the repository
2. Open in Android Studio
3. Get a Gemini API key from [Google AI Studio](https://makersuite.google.com/app/apikey)
4. Build and run the app
5. Enter your API key in the Settings screen

## Project Structure

- `LanguageGameScreen.kt`: Main game interface
- `LanguageGameViewModel.kt`: Game logic and state management
- `Settings.kt`: Settings and API key management
- `SettingsViewModel.kt`: Settings state management
- `LanguageGameState.kt`: Game state data classes

## AI Models

Currently supported Gemini models:
- models/gemini-pro
- models/gemini-1.5-pro

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
