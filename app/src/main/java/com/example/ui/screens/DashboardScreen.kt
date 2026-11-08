package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Send
import com.example.ui.components.getNavIcon
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CreationEntity
import com.example.data.model.CreationType
import com.example.data.model.NavDestination
import com.example.ui.components.GlassCard
import com.example.ui.components.GlowingGradientCard
import com.example.ui.theme.BlueElectric
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.KhaledAccentGradient
import com.example.ui.theme.KhaledCardGradient
import com.example.ui.theme.KhaledPrimaryGradient
import com.example.ui.theme.PinkNeon
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
    creationsCount: Int,
    chatCount: Int,
    recentCreations: List<CreationEntity>,
    onNavigate: (NavDestination) -> Unit,
    onLaunchWithPrompt: (NavDestination, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var quickPrompt by remember { mutableStateOf("") }

    // Pulsing aura animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // Hero Card
        item {
            GlowingGradientCard(
                gradient = KhaledPrimaryGradient,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(CyanGlow)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "MULTIMODAL INTELLIGENCE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CyanGlow,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Welcome to KHALED AI",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Interact with next-gen conversational AI, generate photorealistic images, direct cinematic videos, and synthesize voiceovers.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    lineHeight = 18.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Glowing Orb Asset
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(KhaledPrimaryGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceCard),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = PurpleNeon,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Live Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatChip(
                    title = "Conversations",
                    value = "$chatCount",
                    color = BlueElectric,
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    title = "Creations",
                    value = "$creationsCount",
                    color = PurpleNeon,
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    title = "AI Engines",
                    value = "4 Active",
                    color = CyanGlow,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Central Quick Multimodal Prompt Box
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = Color(0xFF28354D)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Quick Creation Console",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = quickPrompt,
                        onValueChange = { quickPrompt = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Describe what you want to create or ask KHALED AI...",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurpleNeon,
                            unfocusedBorderColor = SurfaceCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = Color(0xFF0A0E1A),
                            unfocusedContainerColor = Color(0xFF0A0E1A)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Route prompt to:",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickRouteButton(
                            text = "Chat",
                            icon = Icons.Default.ChatBubble,
                            color = BlueElectric,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onLaunchWithPrompt(NavDestination.CHAT, quickPrompt)
                            }
                        )
                        QuickRouteButton(
                            text = "Image",
                            icon = Icons.Default.Image,
                            color = PurpleNeon,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onLaunchWithPrompt(NavDestination.IMAGE, quickPrompt)
                            }
                        )
                        QuickRouteButton(
                            text = "Video",
                            icon = Icons.Default.Movie,
                            color = PinkNeon,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onLaunchWithPrompt(NavDestination.VIDEO, quickPrompt)
                            }
                        )
                        QuickRouteButton(
                            text = "Audio",
                            icon = Icons.Default.Mic,
                            color = CyanGlow,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onLaunchWithPrompt(NavDestination.AUDIO, quickPrompt)
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // AI Modes Cards Section
        item {
            Text(
                text = "Multimodal AI Studios",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ModeFeatureCard(
                    title = "AI Chatbot & Knowledge",
                    description = "Multilingual support in Arabic, Moroccan Darija, French, and English with code highlighting.",
                    icon = Icons.Default.ChatBubble,
                    gradient = Brush.horizontalGradient(listOf(Color(0xFF2563EB), Color(0xFF3B82F6))),
                    tag = "Gemini 2.5",
                    onClick = { onNavigate(NavDestination.CHAT) }
                )

                ModeFeatureCard(
                    title = "Text-to-Image Studio",
                    description = "Photorealistic, anime, 3D, and Moroccan heritage visual generation with aspect ratios.",
                    icon = Icons.Default.Image,
                    gradient = Brush.horizontalGradient(listOf(Color(0xFF7C3AED), Color(0xFFA855F7))),
                    tag = "Imagen 3",
                    onClick = { onNavigate(NavDestination.IMAGE) }
                )

                ModeFeatureCard(
                    title = "Text-to-Video Director",
                    description = "Direct cinematic videos with camera movements (Orbit, Drone, Dolly) and style controls.",
                    icon = Icons.Default.Movie,
                    gradient = Brush.horizontalGradient(listOf(Color(0xFFDB2777), Color(0xFFF43F5E))),
                    tag = "Google Veo",
                    onClick = { onNavigate(NavDestination.VIDEO) }
                )

                ModeFeatureCard(
                    title = "Text-to-Audio Studio",
                    description = "Synthesize realistic multi-voice speech with live animated waveform and MP3/WAV export.",
                    icon = Icons.Default.Mic,
                    gradient = Brush.horizontalGradient(listOf(Color(0xFF0891B2), Color(0xFF06B6D4))),
                    tag = "Cloud TTS",
                    onClick = { onNavigate(NavDestination.AUDIO) }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Quick Inspiration Prompts
        item {
            Text(
                text = "Inspiration & Shortcuts",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            val inspirationPrompts = listOf(
                "🇲🇦 عاود ليا قصة ممتعة بالدارجة المغربية على مدينة فاس القديمة" to NavDestination.CHAT,
                "Cyberpunk neon alley in rain, volumetric fog, octane render 8k" to NavDestination.IMAGE,
                "Cinematic drone flyover over golden sand dunes at sunset" to NavDestination.VIDEO,
                "Welcome to KHALED AI, your next-generation creative companion." to NavDestination.AUDIO,
                "Explain how transformers work in AI with clear Kotlin examples" to NavDestination.CHAT
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(inspirationPrompts) { (prompt, dest) ->
                    GlassCard(
                        modifier = Modifier.width(260.dp),
                        onClick = { onLaunchWithPrompt(dest, prompt) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = getNavIcon(dest),
                                    contentDescription = null,
                                    tint = PurpleNeon,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = dest.title,
                                    style = MaterialTheme.typography.labelSmall.copy(color = CyanGlow)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = prompt,
                                maxLines = 2,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary
                                )
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Recent Creations Carousel
        if (recentCreations.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Creations",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "View All (${recentCreations.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PurpleNeon,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable { onNavigate(NavDestination.CREATIONS) }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(recentCreations.take(6)) { creation ->
                        GlassCard(
                            modifier = Modifier.width(180.dp),
                            onClick = { onNavigate(NavDestination.CREATIONS) }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF070B14)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (creation.type) {
                                            CreationType.IMAGE -> Icons.Default.Image
                                            CreationType.VIDEO -> Icons.Default.Movie
                                            CreationType.AUDIO -> Icons.Default.Mic
                                            CreationType.CHAT -> Icons.Default.ChatBubble
                                        },
                                        contentDescription = null,
                                        tint = PurpleNeon,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = creation.title,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = creation.type.name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatChip(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        borderColor = color.copy(alpha = 0.3f),
        backgroundColor = SurfaceCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
private fun QuickRouteButton(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun ModeFeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    gradient: Brush,
    tag: String,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(gradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyanGlow,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
