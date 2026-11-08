package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiLanguage
import com.example.data.model.AudioGenerationOptions
import com.example.data.model.AvailableVoiceStyles
import com.example.data.model.CreationEntity
import com.example.data.model.CreationType
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.KhaledPrimaryGradient
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.UUID

@Composable
fun AudioStudioScreen(
    isPlaying: Boolean,
    amplitudes: List<Float>,
    onSpeak: (String, String, Float, Float) -> Unit,
    onStop: () -> Unit,
    onSaveAudioCreation: (CreationEntity) -> Unit,
    initialText: String = "",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var speechText by remember { mutableStateOf(initialText) }
    var selectedLanguage by remember { mutableStateOf(AiLanguage.ENGLISH) }
    var selectedVoiceStyle by remember { mutableStateOf("Studio Narrator") }
    var speed by remember { mutableFloatStateOf(1.0f) }
    var pitch by remember { mutableFloatStateOf(1.0f) }

    LaunchedEffect(initialText) {
        if (initialText.isNotBlank()) speechText = initialText
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CyanGlow.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFF0891B2), Color(0xFF06B6D4)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "KHALED AI Audio Studio",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Professional speech synthesis across English, Arabic, Darija, and French",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Live Audio Waveform
        item {
            Column {
                Text(
                    text = "Live Frequency Spectrum",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                AudioWaveformVisualizer(
                    amplitudes = amplitudes,
                    isPlaying = isPlaying
                )
            }
        }

        // Script input text field
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Script / Speech Text",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "${speechText.length} characters",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = speechText,
                    onValueChange = { speechText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Type or paste narration script in English, Arabic, Darija, or French...",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanGlow,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard
                    ),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 4,
                    maxLines = 8
                )
            }
        }

        // Language selection
        item {
            Column {
                Text(
                    text = "Spoken Language",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(AiLanguage.values()) { lang ->
                        val isSelected = selectedLanguage == lang
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) CyanGlow.copy(alpha = 0.2f) else SurfaceCard)
                                .border(1.dp, if (isSelected) CyanGlow else SurfaceCardBorder, RoundedCornerShape(12.dp))
                                .clickable { selectedLanguage = lang }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = lang.flag, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = lang.displayName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Voice Style selection
        item {
            Column {
                Text(
                    text = "Voice Style Profile",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(AvailableVoiceStyles) { style ->
                        val isSelected = selectedVoiceStyle == style
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PurpleNeon.copy(alpha = 0.25f) else SurfaceCard)
                                .border(1.dp, if (isSelected) PurpleNeon else SurfaceCardBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedVoiceStyle = style
                                    // Adjust pitch & speed according to style preset
                                    when (style) {
                                        "Energetic Host" -> { speed = 1.15f; pitch = 1.1f }
                                        "Calm Meditation" -> { speed = 0.85f; pitch = 0.9f }
                                        "Tech Presenter" -> { speed = 1.05f; pitch = 1.0f }
                                        "Storyteller" -> { speed = 0.95f; pitch = 0.95f }
                                        else -> { speed = 1.0f; pitch = 1.0f }
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = style,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) Color.White else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }
        }

        // Pitch & Speed Sliders
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Speed
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Speaking Speed",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                        Text(
                            text = "${String.format("%.2f", speed)}x",
                            style = MaterialTheme.typography.labelSmall.copy(color = CyanGlow, fontWeight = FontWeight.Bold)
                        )
                    }
                    Slider(
                        value = speed,
                        onValueChange = { speed = it },
                        valueRange = 0.5f..2.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanGlow,
                            activeTrackColor = CyanGlow,
                            inactiveTrackColor = Color(0xFF1E293B)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Pitch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Voice Pitch",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                        Text(
                            text = "${String.format("%.2f", pitch)}x",
                            style = MaterialTheme.typography.labelSmall.copy(color = PurpleNeon, fontWeight = FontWeight.Bold)
                        )
                    }
                    Slider(
                        value = pitch,
                        onValueChange = { pitch = it },
                        valueRange = 0.5f..1.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = PurpleNeon,
                            activeTrackColor = PurpleNeon,
                            inactiveTrackColor = Color(0xFF1E293B)
                        )
                    )
                }
            }
        }

        // Action Buttons: Play/Stop & Save
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GradientButton(
                    text = if (isPlaying) "Stop Speech" else "Synthesize & Speak",
                    icon = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    gradient = if (isPlaying) Brush.horizontalGradient(listOf(Color(0xFFEF4444), Color(0xFFDC2626))) else Brush.horizontalGradient(listOf(Color(0xFF0891B2), Color(0xFF06B6D4))),
                    onClick = {
                        if (isPlaying) {
                            onStop()
                        } else {
                            if (speechText.isNotBlank()) {
                                onSpeak(speechText, selectedLanguage.code, pitch, speed)
                            } else {
                                Toast.makeText(context, "Please enter script text first", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = speechText.isNotBlank() || isPlaying
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Save to My Creations
            Button(
                onClick = {
                    if (speechText.isNotBlank()) {
                        val creation = CreationEntity(
                            id = UUID.randomUUID().toString(),
                            title = speechText.take(30).replace("\n", " "),
                            type = CreationType.AUDIO,
                            prompt = speechText,
                            mediaUriOrData = "synthesized_speech",
                            voice = "$selectedVoiceStyle (${selectedLanguage.displayName})",
                            style = "${String.format("%.1f", speed)}x speed"
                        )
                        onSaveAudioCreation(creation)
                        Toast.makeText(context, "Saved audio script to My Creations!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = speechText.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Audio to My Creations")
            }
        }
    }
}
