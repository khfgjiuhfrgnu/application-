package com.example.data.model

data class ImageGenerationOptions(
    val prompt: String,
    val negativePrompt: String = "",
    val aspectRatio: String = "1:1",
    val style: String = "Photorealistic",
    val resolution: String = "1024x1024",
    val enhancePrompt: Boolean = true
)

val AvailableImageStyles = listOf(
    "Photorealistic",
    "Cinematic 8K",
    "Anime / Manga",
    "3D Pixar / Unreal",
    "Cyberpunk Neon",
    "Moroccan Heritage",
    "Digital Fantasy",
    "Watercolor Painting",
    "Retro Synthwave"
)

val AvailableAspectRatios = listOf(
    "1:1" to "Square (1:1)",
    "16:9" to "Landscape (16:9)",
    "9:16" to "Portrait (9:16)",
    "4:3" to "Standard (4:3)"
)

val AvailableResolutions = listOf(
    "1024x1024",
    "1920x1080",
    "768x1344"
)

data class VideoGenerationOptions(
    val prompt: String,
    val durationSeconds: Int = 5,
    val aspectRatio: String = "16:9",
    val cinematicStyle: String = "Ultra-realistic 4K",
    val cameraMovement: String = "Orbit 360°"
)

val AvailableVideoStyles = listOf(
    "Ultra-realistic 4K",
    "Sci-Fi Cinematic",
    "Hyper-lapse City",
    "Noir Dramatic",
    "Studio Anime Film",
    "Nature Documentary"
)

val AvailableCameraMovements = listOf(
    "Orbit 360°",
    "Drone Flyover",
    "Slow Push In",
    "Pan Right to Left",
    "Dolly Forward",
    "Static Tripod"
)

val AvailableVideoDurations = listOf(5, 10, 15)

data class AudioGenerationOptions(
    val text: String,
    val languageCode: String = "en",
    val voiceStyle: String = "Studio Narrator",
    val speed: Float = 1.0f,
    val pitch: Float = 1.0f
)

val AvailableVoiceStyles = listOf(
    "Studio Narrator",
    "Energetic Host",
    "Calm Meditation",
    "Tech Presenter",
    "Storyteller"
)

sealed interface GenerationState<out T> {
    data object Idle : GenerationState<Nothing>
    data class Generating(val progress: Float, val stageText: String) : GenerationState<Nothing>
    data class Success<T>(val data: T) : GenerationState<T>
    data class Error(val message: String) : GenerationState<Nothing>
}
