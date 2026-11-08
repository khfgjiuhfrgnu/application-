package com.example.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class GeminiRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val api: GeminiApiService = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(GeminiApiService::class.java)

    private fun buildSystemInstruction(languageCode: String): String {
        val langSpecific = when (languageCode) {
            "ary" -> "You are conversing primarily in Moroccan Darija (الدارجة المغربية). Use authentic, expressive, and natural Moroccan vocabulary, idioms, and friendly tone. You can seamlessly switch between Arabic letters or Arabizi when appropriate."
            "ar" -> "You are conversing primarily in Modern Standard Arabic (العربية الفصحى). Speak with eloquent, grammatically sound, and engaging Arabic phrasing."
            "fr" -> "Vous communiquez principalement en Français. Adoptez un ton professionnel, élégant, créatif et rigoureux."
            else -> "You are conversing in English with an intelligent, clear, articulate, and motivating tone."
        }

        return """
            You are KHALED AI, an elite multimodal artificial intelligence assistant and creative studio engine.
            You excel in conversation, creative ideation, coding, multilingual translation (especially English, Arabic, Moroccan Darija, and French), and multimedia generation planning.
            $langSpecific
            
            Guidelines:
            - Provide clear, direct, and structured answers using clean Markdown.
            - When providing code, use triple backtick markdown code blocks with the language tag (e.g. ```kotlin ... ```).
            - Be polite, inspiring, and technically astute.
            - If asked who you are, proudly introduce yourself as KHALED AI.
        """.trimIndent()
    }

    suspend fun sendChatMessage(
        history: List<Pair<Boolean, String>>, // isUser to text
        userMessage: String,
        languageCode: String,
        model: String,
        apiKey: String
    ): Result<String> {
        if (apiKey.isBlank()) {
            return Result.failure(
                IllegalStateException("Gemini API key is not configured. Please add your key in Settings or AI Studio Secrets.")
            )
        }

        return try {
            val contents = mutableListOf<GeminiContent>()

            // Add previous conversational context (last 10 turns)
            val recentHistory = history.takeLast(10)
            for ((isUser, text) in recentHistory) {
                contents.add(
                    GeminiContent(
                        role = if (isUser) "user" else "model",
                        parts = listOf(GeminiPart(text = text))
                    )
                )
            }

            // Append current prompt
            contents.add(
                GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart(text = userMessage))
                )
            )

            val request = GeminiGenerateRequest(
                contents = contents,
                systemInstruction = GeminiSystemInstruction(
                    parts = listOf(GeminiPart(text = buildSystemInstruction(languageCode)))
                ),
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.7,
                    maxOutputTokens = 4096
                )
            )

            val response = api.generateContent(
                model = model.ifBlank { "gemini-2.5-flash" },
                apiKey = apiKey,
                request = request
            )

            if (response.isSuccessful) {
                val candidateText = response.body()
                    ?.candidates
                    ?.firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstOrNull()
                    ?.text

                if (!candidateText.isNullOrBlank()) {
                    Result.success(candidateText)
                } else {
                    Result.failure(Exception("AI returned an empty response. Please try again."))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                val friendlyMessage = parseGeminiError(response.code(), errorBody)
                Result.failure(Exception(friendlyMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun enhanceImagePrompt(
        prompt: String,
        style: String,
        aspectRatio: String,
        apiKey: String
    ): Result<String> {
        if (apiKey.isBlank()) return Result.success("$prompt, $style, ultra detailed 8k photography, volumetric lighting, photorealistic octane render")

        return try {
            val systemPrompt = "You are an expert prompt engineer for Imagen 3 and Midjourney. Take the user's raw idea and expand it into a rich, vivid, descriptive visual prompt. Focus on lighting, textures, camera angle, and artistic atmosphere. Output ONLY the refined prompt text without preamble."
            val userPrompt = "Enhance this image prompt for style '$style' and aspect ratio '$aspectRatio': $prompt"

            val request = GeminiGenerateRequest(
                contents = listOf(
                    GeminiContent(role = "user", parts = listOf(GeminiPart(text = userPrompt)))
                ),
                systemInstruction = GeminiSystemInstruction(parts = listOf(GeminiPart(text = systemPrompt))),
                generationConfig = GeminiGenerationConfig(temperature = 0.8, maxOutputTokens = 300)
            )

            val response = api.generateContent(model = "gemini-2.5-flash", apiKey = apiKey, request = request)
            if (response.isSuccessful) {
                val text = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) Result.success(text.trim()) else Result.success(prompt)
            } else {
                Result.success("$prompt, highly detailed $style, cinematic lighting, 8k resolution, award winning composition")
            }
        } catch (_: Exception) {
            Result.success("$prompt, highly detailed $style, cinematic lighting, 8k resolution, award winning composition")
        }
    }

    suspend fun enhanceVideoPrompt(
        prompt: String,
        cinematicStyle: String,
        cameraMovement: String,
        duration: Int,
        apiKey: String
    ): Result<String> {
        if (apiKey.isBlank()) return Result.success("$prompt | Cinematic style: $cinematicStyle | Camera: $cameraMovement | $duration sec sequence")

        return try {
            val systemPrompt = "You are a director and prompt architect for Google Veo video generation. Given the user's concept, output a cinematic camera script describing the motion, lighting, subject action, and atmosphere. Keep it concise (2-3 sentences), highly visual, and suitable for generative video models."
            val userPrompt = "Direct a $duration-second video for: $prompt. Style: $cinematicStyle. Camera: $cameraMovement."

            val request = GeminiGenerateRequest(
                contents = listOf(
                    GeminiContent(role = "user", parts = listOf(GeminiPart(text = userPrompt)))
                ),
                systemInstruction = GeminiSystemInstruction(parts = listOf(GeminiPart(text = systemPrompt))),
                generationConfig = GeminiGenerationConfig(temperature = 0.7, maxOutputTokens = 350)
            )

            val response = api.generateContent(model = "gemini-2.5-flash", apiKey = apiKey, request = request)
            if (response.isSuccessful) {
                val text = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) Result.success(text.trim()) else Result.success(prompt)
            } else {
                Result.success("$prompt with $cameraMovement in stunning $cinematicStyle.")
            }
        } catch (_: Exception) {
            Result.success("$prompt with $cameraMovement in stunning $cinematicStyle.")
        }
    }

    suspend fun testApiKey(apiKey: String): Result<Boolean> {
        return try {
            val request = GeminiGenerateRequest(
                contents = listOf(
                    GeminiContent(role = "user", parts = listOf(GeminiPart(text = "Hello!")))
                ),
                generationConfig = GeminiGenerationConfig(maxOutputTokens = 10)
            )
            val response = api.generateContent(
                model = "gemini-2.5-flash",
                apiKey = apiKey,
                request = request
            )
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                val error = parseGeminiError(response.code(), response.errorBody()?.string() ?: "")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseGeminiError(statusCode: Int, errorBody: String): String {
        return when (statusCode) {
            400 -> "Invalid request or API key formatting. Please check your key in Settings."
            403 -> "API key does not have permission or has expired. Please verify in Google AI Studio."
            404 -> "Requested Gemini model not found. Try switching to gemini-2.5-flash in Settings."
            429 -> "Rate limit reached. Please wait a moment before trying again."
            500, 503 -> "Gemini service temporarily unavailable. Please retry in a few seconds."
            else -> if (errorBody.contains("API_KEY_INVALID")) {
                "Invalid API key. Please check your credentials in Settings."
            } else {
                "Error ($statusCode): ${errorBody.take(150)}"
            }
        }
    }
}
