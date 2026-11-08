package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NavDestination
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.KhaledPrimaryGradient
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

fun getNavIcon(destination: NavDestination): ImageVector {
    return when (destination) {
        NavDestination.DASHBOARD -> Icons.Default.SpaceDashboard
        NavDestination.CHAT -> Icons.Default.ChatBubble
        NavDestination.IMAGE -> Icons.Default.Image
        NavDestination.VIDEO -> Icons.Default.Movie
        NavDestination.AUDIO -> Icons.Default.Mic
        NavDestination.CREATIONS -> Icons.Default.FolderSpecial
        NavDestination.HISTORY -> Icons.Default.History
        NavDestination.SETTINGS -> Icons.Default.Settings
    }
}

@Composable
fun KhaledBottomNav(
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryTabs = listOf(
        NavDestination.DASHBOARD,
        NavDestination.CHAT,
        NavDestination.IMAGE,
        NavDestination.VIDEO,
        NavDestination.AUDIO
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark.copy(alpha = 0.95f))
            .border(width = 1.dp, color = Color(0xFF1E293B))
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            primaryTabs.forEach { destination ->
                val isSelected = currentDestination == destination

                Column(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigate(destination) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val iconBgModifier = if (isSelected) {
                        Modifier.background(KhaledPrimaryGradient)
                    } else Modifier

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .then(iconBgModifier),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getNavIcon(destination),
                            contentDescription = destination.title,
                            tint = if (isSelected) Color.White else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = when (destination) {
                            NavDestination.DASHBOARD -> "Home"
                            NavDestination.CHAT -> "Chat"
                            NavDestination.IMAGE -> "Images"
                            NavDestination.VIDEO -> "Videos"
                            NavDestination.AUDIO -> "Audio"
                            else -> destination.title
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) PurpleNeon else TextMuted
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun KhaledDrawerContent(
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier
            .width(300.dp)
            .fillMaxHeight(),
        drawerContainerColor = SurfaceDark,
        drawerContentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(KhaledPrimaryGradient)
                        .padding(1.5.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(SurfaceCard),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = PurpleNeon,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row {
                        Text(
                            text = "KHALED ",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "AI",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = CyanGlow
                            )
                        )
                    }
                    Text(
                        text = "Multimodal Creative Studio",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            HorizontalDivider(color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 16.dp))

            // Navigation Items
            NavDestination.values().forEach { destination ->
                val isSelected = currentDestination == destination

                NavigationDrawerItem(
                    label = {
                        Text(
                            text = destination.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = getNavIcon(destination),
                            contentDescription = null,
                            tint = if (isSelected) Color.White else PurpleNeon
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        onNavigate(destination)
                        onCloseDrawer()
                    },
                    modifier = Modifier.padding(vertical = 3.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = PurpleNeon.copy(alpha = 0.2f),
                        unselectedContainerColor = Color.Transparent,
                        selectedTextColor = Color.White,
                        unselectedTextColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Brand Footnote
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCard)
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(CyanGlow)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Powered by Google Gemini 2.5",
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
