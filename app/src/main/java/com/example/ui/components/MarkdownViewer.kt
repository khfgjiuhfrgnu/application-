package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun MarkdownViewer(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = TextPrimary
) {
    val context = LocalContext.current
    val sections = parseMarkdownIntoSections(text)

    Column(modifier = modifier) {
        sections.forEach { section ->
            when (section) {
                is MarkdownSection.CodeBlock -> {
                    CodeBlockView(
                        code = section.code,
                        language = section.language,
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Code", section.code))
                            Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                is MarkdownSection.Header -> {
                    Text(
                        text = section.text,
                        style = when (section.level) {
                            1 -> MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = PurpleNeon)
                            2 -> MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CyanGlow)
                            else -> MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                is MarkdownSection.BulletItem -> {
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text(
                            text = "•",
                            color = CyanGlow,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        FormattedInlineText(text = section.text, textColor = textColor)
                    }
                }
                is MarkdownSection.Paragraph -> {
                    FormattedInlineText(text = section.text, textColor = textColor)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun FormattedInlineText(
    text: String,
    textColor: Color
) {
    val annotatedString = buildAnnotatedString {
        var cursor = 0
        val regex = Regex("""(\*\*.*?\*\*|`.*?`)""")
        val matches = regex.findAll(text)

        matches.forEach { match ->
            if (match.range.first > cursor) {
                withStyle(SpanStyle(color = textColor, fontSize = 14.sp)) {
                    append(text.substring(cursor, match.range.first))
                }
            }

            val token = match.value
            when {
                token.startsWith("**") && token.endsWith("**") -> {
                    withStyle(SpanStyle(color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)) {
                        append(token.removeSurrounding("**"))
                    }
                }
                token.startsWith("`") && token.endsWith("`") -> {
                    withStyle(
                        SpanStyle(
                            color = CyanGlow,
                            background = Color(0x3306B6D4),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        )
                    ) {
                        append(" " + token.removeSurrounding("`") + " ")
                    }
                }
            }
            cursor = match.range.last + 1
        }

        if (cursor < text.length) {
            withStyle(SpanStyle(color = textColor, fontSize = 14.sp)) {
                append(text.substring(cursor))
            }
        }
    }

    Text(text = annotatedString, lineHeight = 20.sp)
}

@Composable
private fun CodeBlockView(
    code: String,
    language: String,
    onCopy: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF070B14))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
    ) {
        Column {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = language.ifBlank { "code" },
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace
                    ),
                    modifier = Modifier.weight(1f)
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(onClick = onCopy)
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = CyanGlow,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Copy",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyanGlow,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Code content
            Text(
                text = code,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFE2E8F0),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                ),
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

private sealed interface MarkdownSection {
    data class Header(val level: Int, val text: String) : MarkdownSection
    data class CodeBlock(val code: String, val language: String) : MarkdownSection
    data class BulletItem(val text: String) : MarkdownSection
    data class Paragraph(val text: String) : MarkdownSection
}

private fun parseMarkdownIntoSections(raw: String): List<MarkdownSection> {
    val result = mutableListOf<MarkdownSection>()
    val lines = raw.lines()
    var inCodeBlock = false
    var codeLang = ""
    val codeBuilder = StringBuilder()

    for (line in lines) {
        val trimmed = line.trim()
        if (trimmed.startsWith("```")) {
            if (inCodeBlock) {
                result.add(MarkdownSection.CodeBlock(codeBuilder.toString().trimEnd(), codeLang))
                codeBuilder.clear()
                inCodeBlock = false
                codeLang = ""
            } else {
                inCodeBlock = true
                codeLang = trimmed.removePrefix("```").trim()
            }
        } else if (inCodeBlock) {
            codeBuilder.append(line).append("\n")
        } else if (trimmed.startsWith("### ")) {
            result.add(MarkdownSection.Header(3, trimmed.removePrefix("### ")))
        } else if (trimmed.startsWith("## ")) {
            result.add(MarkdownSection.Header(2, trimmed.removePrefix("## ")))
        } else if (trimmed.startsWith("# ")) {
            result.add(MarkdownSection.Header(1, trimmed.removePrefix("# ")))
        } else if (trimmed.startsWith("* ") || trimmed.startsWith("- ")) {
            result.add(MarkdownSection.BulletItem(trimmed.substring(2)))
        } else if (trimmed.isNotBlank()) {
            result.add(MarkdownSection.Paragraph(line))
        }
    }

    if (inCodeBlock && codeBuilder.isNotEmpty()) {
        result.add(MarkdownSection.CodeBlock(codeBuilder.toString().trimEnd(), codeLang))
    }

    return result
}
