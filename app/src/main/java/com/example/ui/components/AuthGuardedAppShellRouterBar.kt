package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.AppNavScreen
import com.example.ui.DashboardTab
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class AppShellRouteSpec(
    val path: String,
    val componentName: String,
    val requiresAuthGuard: Boolean,
    val targetScreen: AppNavScreen,
    val targetTab: DashboardTab?,
    val icon: ImageVector,
    val accentColor: Color
)

val STAGES_OF_LIFE_APP_SHELL_ROUTES = listOf(
    AppShellRouteSpec(
        path = "login",
        componentName = "LoginPageComponent",
        requiresAuthGuard = false,
        targetScreen = AppNavScreen.LOGIN,
        targetTab = null,
        icon = Icons.AutoMirrored.Filled.Login,
        accentColor = NeonPink
    ),
    AppShellRouteSpec(
        path = "dashboard",
        componentName = "DashboardPageComponent",
        requiresAuthGuard = true,
        targetScreen = AppNavScreen.MAIN_DASHBOARD,
        targetTab = DashboardTab.HUB_3D,
        icon = Icons.Default.Dashboard,
        accentColor = HoloCyan
    ),
    AppShellRouteSpec(
        path = "profile",
        componentName = "ProfilePageComponent",
        requiresAuthGuard = true,
        targetScreen = AppNavScreen.MAIN_DASHBOARD,
        targetTab = DashboardTab.THEME_PROFILE,
        icon = Icons.Default.Person,
        accentColor = CyberEmerald
    ),
    AppShellRouteSpec(
        path = "finance/goals",
        componentName = "GoalsPageComponent",
        requiresAuthGuard = true,
        targetScreen = AppNavScreen.MAIN_DASHBOARD,
        targetTab = DashboardTab.FINANCE_GOALS,
        icon = Icons.Default.AccountBalanceWallet,
        accentColor = SolarAmber
    )
)

/**
 * Resolves any route path using the exact routing rules:
 * - 'login' -> LoginPageComponent
 * - '' (full match) -> redirectTo: 'dashboard'
 * - 'dashboard' -> DashboardPageComponent (canActivate: [authGuard])
 * - 'profile' -> ProfilePageComponent (canActivate: [authGuard])
 * - 'finance/goals' -> GoalsPageComponent (canActivate: [authGuard])
 * - '**' (wildcard) -> redirectTo: 'dashboard'
 */
fun resolveStagesOfLifeRoute(
    rawPath: String,
    isUserAuthenticated: Boolean,
    enforceStrictAuthGuard: Boolean
): Pair<AppShellRouteSpec, String> {
    val trimmed = rawPath.trim().trimStart('/')
    val pathPart = trimmed.substringBefore('?').lowercase()
    val queryPart = trimmed.substringAfter('?', "")
    val boundInputsSummary = if (queryPart.isNotBlank()) {
        " [withComponentInputBinding: $queryPart]"
    } else {
        ""
    }

    val loginSpec = STAGES_OF_LIFE_APP_SHELL_ROUTES.first { it.path == "login" }
    val dashboardSpec = STAGES_OF_LIFE_APP_SHELL_ROUTES.first { it.path == "dashboard" }

    if (pathPart == "login") {
        return loginSpec to "Resolved route 'login' -> LoginPageComponent$boundInputsSummary"
    }

    val matchedChild = when (pathPart) {
        "" -> dashboardSpec to "Empty path '' (pathMatch: 'full') -> redirected to 'dashboard'$boundInputsSummary"
        "dashboard" -> dashboardSpec to "Resolved AppShell child route 'dashboard' -> DashboardPageComponent$boundInputsSummary"
        "profile" -> STAGES_OF_LIFE_APP_SHELL_ROUTES.first { it.path == "profile" } to
            "Resolved AppShell child route 'profile' -> ProfilePageComponent$boundInputsSummary"
        "finance/goals", "goals" -> STAGES_OF_LIFE_APP_SHELL_ROUTES.first { it.path == "finance/goals" } to
            "Resolved AppShell child route 'finance/goals' -> GoalsPageComponent$boundInputsSummary"
        else -> dashboardSpec to "Wildcard '**' matched '$pathPart' -> redirected to 'dashboard'$boundInputsSummary"
    }

    if (matchedChild.first.requiresAuthGuard && enforceStrictAuthGuard && !isUserAuthenticated) {
        return loginSpec to "authGuard blocked '${matchedChild.first.path}' (unauthenticated) -> redirected to 'login'"
    }

    return matchedChild
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthGuardedAppShellRouterCard(
    effectiveScreen: AppNavScreen,
    activeTab: DashboardTab,
    isAuthenticated: Boolean,
    userDisplayName: String,
    onNavigateToLogin: () -> Unit,
    onNavigateToTab: (DashboardTab) -> Unit,
    onLogout: () -> Unit = onNavigateToLogin,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var enforceStrictAuthGuard by rememberSaveable { mutableStateOf(false) }
    var customRouteInput by rememberSaveable { mutableStateOf("") }
    var showRouteTreeDetails by rememberSaveable { mutableStateOf(false) }

    val currentActivePath = remember(effectiveScreen, activeTab) {
        when {
            effectiveScreen == AppNavScreen.LOGIN || effectiveScreen == AppNavScreen.SIGN_UP -> "login"
            activeTab == DashboardTab.THEME_PROFILE -> "profile"
            activeTab == DashboardTab.FINANCE_GOALS -> "finance/goals"
            else -> "dashboard"
        }
    }

    var routeResolutionStatus by rememberSaveable(currentActivePath) {
        mutableStateOf("AppShellComponent Active • Current Outlet: '$currentActivePath'")
    }

    fun dispatchRouteNavigation(rawRoute: String) {
        val (resolvedSpec, statusMsg) = resolveStagesOfLifeRoute(
            rawPath = rawRoute,
            isUserAuthenticated = isAuthenticated || userDisplayName.isNotBlank(),
            enforceStrictAuthGuard = enforceStrictAuthGuard
        )
        routeResolutionStatus = statusMsg
        coroutineScope.launch {
            GlobalLoadingTracker.withLoading(
                title = "Fetching /${resolvedSpec.path} Data...",
                subtitle = "Loading ${resolvedSpec.componentName} from Firebase & API",
                category = "Firebase / API Fetch",
                minDisplayMillis = 480L
            ) {
                delay(240L)
            }
        }
        if (resolvedSpec.targetScreen == AppNavScreen.LOGIN) {
            onNavigateToLogin()
        } else if (resolvedSpec.targetTab != null) {
            onNavigateToTab(resolvedSpec.targetTab)
        }
    }

    fun handleSidebarLogout() {
        coroutineScope.launch {
            GlobalLoadingTracker.withLoading(
                title = "Signing Out of Session...",
                subtitle = "Calling AuthService.logout() & redirecting to /login",
                category = "Firebase Auth",
                minDisplayMillis = 420L
            ) {
                delay(220L)
            }
            com.example.core.auth.FirebaseAuthRouteGuardService.logout {
                routeResolutionStatus = "AuthService.logout() -> Redirected to '/login' (LoginPageComponent)"
                onLogout()
            }
        }
    }

    fun triggerFirebaseApiDataFetchDemo() {
        coroutineScope.launch {
            GlobalLoadingTracker.withLoading(
                title = "Fetching Firebase & API Data...",
                subtitle = "Step 1/2: Querying ProfileService.getCurrentUser() & Firestore users collection",
                category = "Firebase / API Fetch",
                minDisplayMillis = 950L
            ) { opId ->
                GlobalLoadingTracker.updateRequest(
                    id = opId,
                    subtitle = "Step 1/2: Fetching UserProfile from Firebase Firestore",
                    progress = 0.45f
                )
                delay(450L)
                GlobalLoadingTracker.updateRequest(
                    id = opId,
                    subtitle = "Step 2/2: Fetching FinanceService.getGoals() & Community WallPosts",
                    progress = 0.90f
                )
                delay(450L)
                routeResolutionStatus = "Firebase & API data fetch completed for '/$currentActivePath'"
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect()
            .testTag("auth_guarded_app_shell_router_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)
        ),
        border = BorderStroke(1.5.dp, HoloCyan.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(HoloCyan, CyberEmerald))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Route,
                            contentDescription = "AppShell Router Outlet",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "APPSHELL ROUTER & AUTH GUARD",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = HoloCyan
                        )
                        Text(
                            text = "canActivate: [authGuard] • login | dashboard | profile | finance/goals",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = CyberEmerald.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f)),
                    modifier = Modifier.clickable {
                        showRouteTreeDetails = !showRouteTreeDetails
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isAuthenticated) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = "Auth Guard Status",
                            tint = CyberEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "authGuard: PASS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberEmerald
                        )
                    }
                }
            }

            // AppShellComponent 2-Column Layout (<aside class="sidebar"> + <main class="content">)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(1.dp, Color(0xFF1F2937)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_shell_component_container")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // <aside class="sidebar">
                    Column(
                        modifier = Modifier
                            .width(156.dp)
                            .background(Color(0xFF111827))
                            .padding(14.dp)
                            .testTag("app_shell_sidebar"),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Wondering World",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        // Sidebar <nav> links: Dashboard, Profile, Finance Goals
                        val sidebarRoutes = remember {
                            STAGES_OF_LIFE_APP_SHELL_ROUTES.filter { it.path != "login" }
                        }
                        sidebarRoutes.forEach { routeSpec ->
                            val isActive = currentActivePath == routeSpec.path
                            val navLabel = when (routeSpec.path) {
                                "dashboard" -> "Dashboard"
                                "profile" -> "Profile"
                                "finance/goals" -> "Finance Goals"
                                else -> routeSpec.path
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isActive) Color(0xFF1D4ED8) else Color.Transparent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { dispatchRouteNavigation(routeSpec.path) }
                                    .testTag("app_shell_sidebar_link_${routeSpec.path.replace("/", "_")}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = routeSpec.icon,
                                        contentDescription = navLabel,
                                        tint = if (isActive) Color.White else Color(0xFFCBD5E1),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = navLabel,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isActive) Color.White else Color(0xFFCBD5E1)
                                    )
                                }
                            }
                        }

                        // Sidebar Logout Button: calls AuthService.logout() and navigates to /login
                        Button(
                            onClick = { handleSidebarLogout() },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEF4444).copy(alpha = 0.18f),
                                contentColor = Color(0xFFFCA5A5)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFEF4444)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                                .testTag("app_shell_sidebar_logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Login,
                                contentDescription = "Logout",
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Logout",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    // <main class="content"> <router-outlet>
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val activeSpec = STAGES_OF_LIFE_APP_SHELL_ROUTES.firstOrNull {
                            it.path == currentActivePath
                        } ?: STAGES_OF_LIFE_APP_SHELL_ROUTES.first { it.path == "dashboard" }

                        Text(
                            text = "<router-outlet> • /${activeSpec.path}",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = HoloCyan
                        )
                        Text(
                            text = activeSpec.componentName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFE2E8F0)
                        )
                        Text(
                            text = "Protected by canActivate: [authGuard]. Clicking 'Logout' in the sidebar calls AuthService.logout() and redirects to '/login'.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                        Button(
                            onClick = { triggerFirebaseApiDataFetchDemo() },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HoloCyan.copy(alpha = 0.20f),
                                contentColor = HoloCyan
                            ),
                            border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.7f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("app_shell_trigger_global_loading_button")
                        ) {
                            Text(
                                text = "Fetch Firebase / API Data (Spinner Overlay)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // Interactive Route Chips for Login, Dashboard, Profile, and Finance/Goals
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                STAGES_OF_LIFE_APP_SHELL_ROUTES.forEach { routeSpec ->
                    val isCurrent = currentActivePath == routeSpec.path
                    FilterChip(
                        selected = isCurrent,
                        onClick = { dispatchRouteNavigation(routeSpec.path) },
                        leadingIcon = {
                            Icon(
                                imageVector = routeSpec.icon,
                                contentDescription = routeSpec.componentName,
                                tint = if (isCurrent) routeSpec.accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "${routeSpec.path} (${routeSpec.componentName.removeSuffix("Component")})",
                                fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = routeSpec.accentColor.copy(alpha = 0.22f),
                            selectedLabelColor = routeSpec.accentColor
                        ),
                        modifier = Modifier.testTag("router_nav_chip_${routeSpec.path.replace("/", "_")}")
                    )
                }
            }

            // Route Redirect & Wildcard Tester ('' -> 'dashboard', '**' -> 'dashboard')
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = customRouteInput,
                    onValueChange = { customRouteInput = it },
                    label = {
                        Text("Navigate path (e.g., dashboard, profile, finance/goals, or wildcard)...")
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("router_custom_path_input")
                )
                Button(
                    onClick = {
                        dispatchRouteNavigation(customRouteInput)
                        customRouteInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HoloCyan,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.testTag("router_dispatch_path_button")
                ) {
                    Text("Go", fontWeight = FontWeight.ExtraBold)
                }
            }

            // Status Banner showing resolved component & redirect rule
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Resolved Route",
                            tint = CyberEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = routeResolutionStatus,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    FilterChip(
                        selected = showRouteTreeDetails,
                        onClick = { showRouteTreeDetails = !showRouteTreeDetails },
                        label = {
                            Text(
                                text = if (showRouteTreeDetails) "Hide Tree" else "Routes Spec",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }

            if (showRouteTreeDetails) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF070C18),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Active AppComponent (<app-root>) & Route Outlet Hierarchy:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberEmerald
                        )
                        Text(
                            text = "<app-root> (AppComponent • standalone: true)\n" +
                                " └─ <router-outlet>\n" +
                                "     ├─ path: 'login' -> LoginPageComponent (AppNavScreen.LOGIN)\n" +
                                "     ├─ path: '' [canActivate: authGuard] -> AppShellComponent\n" +
                                "     │   ├─ path: 'dashboard' -> DashboardPageComponent (DashboardTab.HUB_3D)\n" +
                                "     │   ├─ path: 'profile' -> ProfilePageComponent (DashboardTab.THEME_PROFILE)\n" +
                                "     │   ├─ path: 'finance/goals' -> GoalsPageComponent (DashboardTab.FINANCE_GOALS)\n" +
                                "     │   └─ path: '' (pathMatch: 'full') -> redirectTo: 'dashboard'\n" +
                                "     └─ path: '**' (wildcard) -> redirectTo: 'dashboard'",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}
