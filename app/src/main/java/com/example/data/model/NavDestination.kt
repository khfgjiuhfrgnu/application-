package com.example.data.model

enum class NavDestination(val title: String, val badge: String = "") {
    DASHBOARD("Dashboard"),
    CHAT("AI Chat"),
    IMAGE("Image Generator"),
    VIDEO("Video Generator"),
    AUDIO("Audio Studio"),
    CREATIONS("My Creations"),
    HISTORY("History"),
    SETTINGS("Settings")
}

enum class AiLanguage(val displayName: String, val code: String, val flag: String, val greeting: String) {
    ENGLISH("English", "en", "🇺🇸", "How can I assist your imagination today?"),
    DARIJA("الدارجة المغربية", "ary", "🇲🇦", "أهلاً بيك! آش نقدر نعاونك تصنع اليوم؟"),
    ARABIC("العربية الفصحى", "ar", "🇸🇦", "مرحباً بك في KHALED AI! كيف يمكنني مساعدتك اليوم؟"),
    FRENCH("Français", "fr", "🇫🇷", "Bonjour ! Que souhaitez-vous créer aujourd'hui ?")
}
