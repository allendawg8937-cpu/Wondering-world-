package com.example.network

import android.net.Uri
import com.example.ui.DashboardTab

/**
 * Canonical HTTPS URL & Deep-Link utility for Wondering World / Stages Of Life.
 * Enforces strict TLS (`https://`) for all outbound web URLs, sanitizes search/resource
 * links, and parses verified platform HTTPS & custom-scheme deep links.
 */
object SecureUrlHelper {

    const val CANONICAL_HTTPS_ORIGIN = "https://wereallequals.world"
    const val CLOUD_PREVIEW_HTTPS_ORIGIN = "https://ais-pre-q6he5yolfycvsoiroobcch-165234349617.us-east1.run.app"
    const val GITHUB_RELEASES_HTTPS_URL = "https://github.com/allendawg8937-cpu/Wondering-world-/releases/tag/latest-apk"
    const val CUSTOM_SCHEME = "wereallequals"
    const val CUSTOM_HOST = "platform"

    /**
     * Upgrades accidental `http://` links to `https://` and blocks unsafe non-HTTPS schemes
     * such as `javascript:`, `file:`, or `data:`. Returns [CANONICAL_HTTPS_ORIGIN] on invalid input.
     */
    fun normalizeHttpsUrl(rawUrl: String): String {
        val trimmed = rawUrl.trim()
        if (trimmed.isEmpty()) return CANONICAL_HTTPS_ORIGIN
        val lower = trimmed.lowercase()
        if (lower.startsWith("javascript:") || lower.startsWith("file:") || lower.startsWith("data:") || lower.startsWith("content:")) {
            return CANONICAL_HTTPS_ORIGIN
        }
        val upgraded = when {
            lower.startsWith("https://") -> trimmed
            lower.startsWith("http://") -> "https://" + trimmed.substring(7)
            trimmed.contains("://") -> return CANONICAL_HTTPS_ORIGIN
            else -> "https://$trimmed"
        }
        val parsed = runCatching { Uri.parse(upgraded) }.getOrNull()
        val host = parsed?.host.orEmpty()
        return if (parsed != null && parsed.scheme.equals("https", ignoreCase = true) && host.isNotBlank()) {
            upgraded
        } else {
            CANONICAL_HTTPS_ORIGIN
        }
    }

    fun isStrictHttpsUrl(url: String): Boolean {
        val trimmed = url.trim()
        if (!trimmed.startsWith("https://", ignoreCase = true)) return false
        val uri = runCatching { Uri.parse(trimmed) }.getOrNull() ?: return false
        return !uri.host.isNullOrBlank()
    }

    fun buildPlatformModuleUrl(
        pathSegment: String,
        stageId: String? = null
    ): String {
        val cleanPath = pathSegment.trim().trimStart('/')
        val base = if (cleanPath.isEmpty()) CANONICAL_HTTPS_ORIGIN else "$CANONICAL_HTTPS_ORIGIN/$cleanPath"
        return if (!stageId.isNullOrBlank()) {
            "$base?stage=${Uri.encode(stageId.trim().lowercase())}"
        } else {
            base
        }
    }

    fun resolveDashboardTabFromUri(uri: Uri?): DashboardTab? {
        if (uri == null) return null
        val scheme = uri.scheme?.lowercase().orEmpty()
        val host = uri.host?.lowercase().orEmpty()
        val isTrustedHost = (scheme == "https" && (
            host == "wereallequals.world" ||
                host.endsWith(".wereallequals.world") ||
                host == "ais-pre-q6he5yolfycvsoiroobcch-165234349617.us-east1.run.app"
            )) || (scheme == CUSTOM_SCHEME && host == CUSTOM_HOST)

        if (!isTrustedHost) return null

        val path = uri.path?.lowercase().orEmpty().trim('/')
        return when {
            path.startsWith("finance") || path.startsWith("wallet") -> DashboardTab.FINANCE_GOALS
            path.startsWith("resources") || path.startsWith("lifeline") -> DashboardTab.RESOURCES
            path.startsWith("tools") || path.startsWith("ai") -> DashboardTab.AI_TOOLS
            path.startsWith("studio") || path.startsWith("arcade") -> DashboardTab.STUDIO_ARCADE
            path.startsWith("profile") || path.startsWith("saved") -> DashboardTab.THEME_PROFILE
            path.startsWith("settings") || path.startsWith("maintenance") -> DashboardTab.MAINTENANCE_SETTINGS
            path.startsWith("dating") || path.startsWith("conduct") || path.startsWith("community") -> DashboardTab.CODE_OF_CONDUCT
            path.startsWith("friends") -> DashboardTab.FRIENDS
            path.startsWith("messaging") -> DashboardTab.MESSAGING
            path.startsWith("3d") || path.startsWith("hub") -> DashboardTab.HUB_3D
            else -> DashboardTab.HUB_3D
        }
    }
}
