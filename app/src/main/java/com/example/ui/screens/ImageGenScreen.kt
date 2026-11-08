package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import coil.compose.AsyncImage
import com.example.data.model.AvailableAspectRatios
import com.example.data.model.AvailableImageStyles
import com.example.data.model.AvailableResolutions
import com.example.data.model.CreationEntity
import com.example.data.model.CreationType
import com.example.data.model.GenerationState
import com.example.data.model.ImageGenerationOptions
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.KhaledAccentGradient
import com.example.ui.theme.KhaledPrimaryGradient
import com.example.ui.theme.PinkNeon
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ImageGenScreen(
    generationState: GenerationState<CreationEntity>,
    onGenerateImage: (ImageGenerationOptions) -> Unit,
    onEnhancePrompt: (String, String, String) -> Unit,
    enhancedPrompt: String?,
    onSaveToCreations: (CreationEntity) -> Unit,
    initialPrompt: String = "",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var prompt by remember { mutableStateOf(initialPrompt) }
    var negativePrompt by remember { mutableStateOf("") }
    var showNegativePrompt by remember { mutableStateOf(false) }
    var selectedStyle by remember { mutableStateOf("Photorealistic") }
    var selectedAspectRatio by remember { mutableStateOf("1:1") }
    var selectedResolution by remember { mutableStateOf("1024x1024") }
    var isEnhancing by remember { mutableStateOf(false) }

    LaunchedEffect(initialPrompt) {
        if (initialPrompt.isNotBlank()) prompt = initialPrompt
    }

    LaunchedEffect(enhancedPrompt) {
        if (!enhancedPrompt.isNullOrBlank()) {
            prompt = enhancedPrompt
            isEnhancing = false
            Toast.makeText(context, "Prompt enhanced by KHALED AI!", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Studio Banner
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = PurpleNeon.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(KhaledPrimaryGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Google Imagen 3 Studio",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Synthesize hyper-detailed visuals with custom aspects & styles",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Prompt Input Field
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Image Prompt",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    // Enhance Prompt with AI button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceCard)
                            .border(1.dp, PurpleNeon.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .clickable(enabled = prompt.isNotBlank() && !isEnhancing) {
                                isEnhancing = true
                                onEnhancePrompt(prompt, selectedStyle, selectedAspectRatio)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isEnhancing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = PurpleNeon,
                                strokeWidth = 1.5.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PurpleNeon,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isEnhancing) "Enhancing..." else "AI Enhance",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyanGlow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "e.g. Majestic Moroccan palace interior illuminated by glowing bioluminescent lanterns, octane render 8k...",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleNeon,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard
                    ),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle Negative Prompt
                Row(
                    modifier = Modifier
                        .clickable { showNegativePrompt = !showNegativePrompt }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (showNegativePrompt) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showNegativePrompt) "Hide Negative Prompt" else "+ Add Negative Prompt",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                }

                AnimatedVisibility(visible = showNegativePrompt) {
                    Column(modifier = Modifier.padding(top = 4.dp)) {
                        OutlinedTextField(
                            value = negativePrompt,
                            onValueChange = { negativePrompt = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("Elements to avoid: blurry, distorted, extra limbs, low resolution...", color = TextMuted, fontSize = 12.sp)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PinkNeon,
                                unfocusedBorderColor = SurfaceCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = SurfaceCard,
                                unfocusedContainerColor = SurfaceCard
                            ),
                            shape = RoundedCornerShape(10.dp),
                            maxLines = 2
                        )
                    }
                }
            }
        }

        // Style Selector
        item {
            Column {
                Text(
                    text = "Visual Style",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(AvailableImageStyles) { style ->
                        val isSelected = selectedStyle == style
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) PurpleNeon.copy(alpha = 0.25f) else SurfaceCard
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) PurpleNeon else SurfaceCardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedStyle = style }
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

        // Aspect Ratio Selector
        item {
            Column {
                Text(
                    text = "Aspect Ratio",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AvailableAspectRatios.forEach { (ratio, label) ->
                        val isSelected = selectedAspectRatio == ratio
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PurpleNeon.copy(alpha = 0.2f) else SurfaceCard)
                                .border(
                                    1.dp,
                                    if (isSelected) PurpleNeon else SurfaceCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedAspectRatio = ratio }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = ratio,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) PurpleNeon else TextPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Generate Action Button
        item {
            val isGenerating = generationState is GenerationState.Generating
            GradientButton(
                text = if (isGenerating) "Synthesizing Image..." else "Generate with Imagen 3",
                icon = Icons.Default.AutoAwesome,
                onClick = {
                    onGenerateImage(
                        ImageGenerationOptions(
                            prompt = prompt,
                            negativePrompt = negativePrompt,
                            aspectRatio = selectedAspectRatio,
                            style = selectedStyle,
                            resolution = selectedResolution
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = prompt.isNotBlank() && !isGenerating,
                loading = isGenerating
            )
        }

        // Generation Progress or Results
        item {
            when (generationState) {
                is GenerationState.Generating -> {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = PurpleNeon)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = generationState.stageText,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { generationState.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = CyanGlow,
                                trackColor = Color(0xFF1E293B)
                            )
                        }
                    }
                }

                is GenerationState.Success -> {
                    val creation = generationState.data
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = PurpleNeon.copy(alpha = 0.4f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = creation.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(PurpleNeon.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = creation.style,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = PurpleNeon,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Image display
                            val ratioFloat = when (creation.aspectRatio) {
                                "16:9" -> 16f / 9f
                                "9:16" -> 9f / 16f
                                "4:3" -> 4f / 3f
                                else -> 1f
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(ratioFloat)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF070B14)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = creation.mediaUriOrData,
                                    contentDescription = creation.prompt,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action buttons (Save, Share)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        onSaveToCreations(creation)
                                        Toast.makeText(context, "Saved to My Creations!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Save Creation", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_SUBJECT, creation.title)
                                            putExtra(Intent.EXTRA_TEXT, "Generated with KHALED AI: ${creation.prompt}\n${creation.mediaUriOrData}")
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Share", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                is GenerationState.Error -> {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = PinkNeon
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Generation Error",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = PinkNeon,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = generationState.message,
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }
                }

                GenerationState.Idle -> {
                    // Placeholder card showing tips
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = CyanGlow,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Tip: Tap 'AI Enhance' to have KHALED AI expand lighting, camera angle, and artistic texture automatically.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
