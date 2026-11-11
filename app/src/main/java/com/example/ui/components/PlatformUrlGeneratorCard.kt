package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

const val OFFICIAL_PLATFORM_CLOUD_URL = "StagesOfLife"

/**
 * Platform URL display removed per user request ("Get rid of http:// and http:// platform of the platform
 * i dont want it on the platform at all").
 */
@Composable
fun PlatformUrlGeneratorCard(
    userDisplayName: String = "",
    currentVisualStyleId: String = "",
    currentThemeHex: String = "",
    onPublishUrlToCommunityWall: (title: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    // Intentionally empty — all http:// / https:// / https:\ platform URLs removed from the UI.
}
