package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// KHALED AI Signature Palette - Premium Futuristic Obsidian & Deep Navy
val BackgroundDark = Color(0xFF070B14)
val SurfaceDark = Color(0xFF0F172A)
val SurfaceCard = Color(0xFF131D33)
val SurfaceCardBorder = Color(0xFF1E293B)
val SurfaceCardGlow = Color(0x338B5CF6)

// Neon & Electric Brand Accents
val PurpleNeon = Color(0xFFA855F7)
val PurpleDeep = Color(0xFF7E22CE)
val BlueElectric = Color(0xFF3B82F6)
val CyanGlow = Color(0xFF06B6D4)
val PinkNeon = Color(0xFFEC4899)
val EmeraldNeon = Color(0xFF10B981)
val AmberNeon = Color(0xFFF59E0B)

// Text & Content Shades
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

// Signature Gradients
val KhaledPrimaryGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF8B5CF6), Color(0xFF3B82F6), Color(0xFF06B6D4))
)

val KhaledAccentGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFEC4899), Color(0xFF8B5CF6))
)

val KhaledCardGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF162038), Color(0xFF0F172A))
)

val KhaledOrbGradient = Brush.radialGradient(
    colors = listOf(Color(0x998B5CF6), Color(0x443B82F6), Color(0x00070B14))
)
