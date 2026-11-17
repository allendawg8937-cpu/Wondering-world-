package com.example.ui.navigation

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.model.BudgetEntry
import com.example.data.model.UserProfile
import com.example.dating.IsolatedAgeGatedDatingScreen
import com.example.ui.UiState
import com.example.ui.ai.AiBuiltInToolScreen
import com.example.ui.components.CreativePhotoStudioCard
import com.example.ui.components.FinancialLiteracyTrackerCard
import com.example.ui.components.FinancialTipOfTheDayCard
import com.example.ui.components.PhotoStudioComponent
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink

/**
 * Sealed class hierarchy defining all top-level module routes managed by PlatformModuleNavHost.
 */
sealed class PlatformModuleDestination(
    val route: String,
    val title: String,
    val badge: String
) {
    data object MainDashboard : PlatformModuleDestination(
        route = "module_main_dashboard",
        title = "Main Dashboard",
        badge = "Core Hub"
    )

    data object PhotoStudio : PlatformModuleDestination(
        route = "module_photo_studio",
        title = "Photo Studio",
        badge = "Camera & Edit"
    )

    data object FinancialLiteracy : PlatformModuleDestination(
        route = "module_financial_literacy",
        title = "Financial Literacy",
        badge = "Budgets & Goals"
    )

    data object AiBuiltInTool : PlatformModuleDestination(
        route = "module_ai_built_in_tool",
        title = "Built-In AI Tool",
        badge = "Gemini 3.8 Flash"
    )

    data object AgeGatedDatingSanctuary : PlatformModuleDestination(
        route = "module_age_gated_dating_18plus",
        title = "18+ Sanctuary",
        badge = "Isolated 18+ Gate"
    )

    companion object {
        val coreModules: List<PlatformModuleDestination>
            get() = listOf(
                MainDashboard,
                PhotoStudio,
                FinancialLiteracy,
                AiBuiltInTool
            )
    }
}

@Composable
fun PlatformModuleNavHost(
    userProfile: UserProfile,
    customProfileBitmap: Bitmap?,
    selected3DAvatarResId: Int?,
    budgetState: UiState<List<BudgetEntry>>,
    virtualCurrencyBalance: Long,
    onApplyPhotoBitmap: (Bitmap) -> Unit,
    onAddBudgetItem: (String, String, Double) -> Unit,
    onDeleteBudgetItem: (String) -> Unit,
    onShareToCommunityWall: (category: String, title: String, content: String) -> Unit,
    onLogModuleInteraction: (actionType: String, detail: String, category: String) -> Unit,
    navController: NavHostController = rememberNavController(),
    startDestination: PlatformModuleDestination = PlatformModuleDestination.MainDashboard,
    mainDashboardContent: @Composable (onNavigateToModule: (PlatformModuleDestination) -> Unit) -> Unit
) {
    val navigateToModule: (PlatformModuleDestination) -> Unit = { target ->
        navController.navigate(target.route) {
            launchSingleTop = true
        }
    }

    val returnToDashboard: () -> Unit = {
        val popped = navController.popBackStack(
            route = PlatformModuleDestination.MainDashboard.route,
            inclusive = false
        )
        if (!popped) {
            navController.navigate(PlatformModuleDestination.MainDashboard.route) {
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        enterTransition = {
            fadeIn(animationSpec = tween(260)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(260)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(220)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(220)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(260)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(260)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(220)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(220)
            )
        }
    ) {
        composable(PlatformModuleDestination.MainDashboard.route) {
            mainDashboardContent(navigateToModule)
        }

        composable(PlatformModuleDestination.PhotoStudio.route) {
            BackHandler { returnToDashboard() }
            DedicatedModuleScaffold(
                currentDestination = PlatformModuleDestination.PhotoStudio,
                accentColor = NeonPink,
                onBackToDashboard = returnToDashboard,
                onSwitchModule = navigateToModule
            ) {
                PhotoStudioComponent(
                    displayName = userProfile.displayName.ifBlank { "Jordan Vance" },
                    currentProfileBitmap = customProfileBitmap,
                    selected3DAvatarResId = selected3DAvatarResId,
                    userPersona = userProfile.role,
                    onApplyToProfile = onApplyPhotoBitmap,
                    onShareCreationToFeed = { category, title, content ->
                        onShareToCommunityWall(category, title, content)
                        returnToDashboard()
                    }
                )
                CreativePhotoStudioCard(
                    displayName = userProfile.displayName.ifBlank { "Jordan Vance" },
                    currentProfileBitmap = customProfileBitmap,
                    selected3DAvatarResId = selected3DAvatarResId,
                    userPersona = userProfile.role,
                    onApplyFilteredBitmapToProfile = onApplyPhotoBitmap,
                    onLogStudioInteraction = { detail ->
                        onLogModuleInteraction("PhotoStudio", detail, "Studio")
                    },
                    onPublishPhotoToSocialPost = { title, caption, category ->
                        onShareToCommunityWall(category, title, caption)
                    }
                )
            }
        }

        composable(PlatformModuleDestination.FinancialLiteracy.route) {
            BackHandler { returnToDashboard() }
            DedicatedModuleScaffold(
                currentDestination = PlatformModuleDestination.FinancialLiteracy,
                accentColor = CyberEmerald,
                onBackToDashboard = returnToDashboard,
                onSwitchModule = navigateToModule
            ) {
                FinancialLiteracyTrackerCard(
                    budgetState = budgetState,
                    onAddBudgetItem = onAddBudgetItem,
                    onDeleteBudgetItem = onDeleteBudgetItem,
                    onShareSummaryToWall = { headline, body ->
                        onShareToCommunityWall("Milestone", headline, body)
                        returnToDashboard()
                    },
                    userRole = userProfile.role,
                    userAge = userProfile.age,
                    equalCoinsBalance = virtualCurrencyBalance
                )
                FinancialTipOfTheDayCard(
                    userRole = userProfile.role,
                    userAge = userProfile.age,
                    onApplyTipSavingsAction = { title, amountDollars ->
                        onAddBudgetItem(title, "Save", amountDollars)
                    },
                    onShareTipToWall = { headline, body ->
                        onShareToCommunityWall("Milestone", headline, body)
                    }
                )
            }
        }

        composable(PlatformModuleDestination.AiBuiltInTool.route) {
            BackHandler { returnToDashboard() }
            DedicatedModuleScaffold(
                currentDestination = PlatformModuleDestination.AiBuiltInTool,
                accentColor = HoloCyan,
                onBackToDashboard = returnToDashboard,
                onSwitchModule = navigateToModule
            ) {
                AiBuiltInToolScreen(
                    userDisplayName = userProfile.displayName.ifBlank { "Jordan Vance" },
                    userRole = userProfile.role,
                    onShareResultToWall = { title, body ->
                        onShareToCommunityWall("AI Insights", title, body)
                        onLogModuleInteraction("AiBuiltInTool", title, "AI")
                        returnToDashboard()
                    }
                )
            }
        }

        composable(PlatformModuleDestination.AgeGatedDatingSanctuary.route) {
            // Completely separate UI and data-isolated 18+ module
            IsolatedAgeGatedDatingScreen(
                onExitToMainPlatform = returnToDashboard
            )
        }
    }
}

@Composable
private fun DedicatedModuleScaffold(
    currentDestination: PlatformModuleDestination,
    accentColor: Color,
    onBackToDashboard: () -> Unit,
    onSwitchModule: (PlatformModuleDestination) -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier
            .fillMaxSize()
            .testTag("dedicated_module_screen_${currentDestination.route}")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.2.dp, accentColor.copy(alpha = 0.55f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = onBackToDashboard,
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("module_nav_back_dashboard")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Main Dashboard",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Main Dashboard", fontWeight = FontWeight.Bold)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = currentDestination.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = accentColor
                            )
                            Text(
                                text = currentDestination.badge,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PlatformModuleDestination.coreModules.forEach { dest ->
                            val icon = when (dest) {
                                PlatformModuleDestination.MainDashboard -> Icons.Default.Dashboard
                                PlatformModuleDestination.PhotoStudio -> Icons.Default.CameraAlt
                                PlatformModuleDestination.FinancialLiteracy -> Icons.Default.AccountBalanceWallet
                                PlatformModuleDestination.AiBuiltInTool -> Icons.Default.AutoAwesome
                                PlatformModuleDestination.AgeGatedDatingSanctuary -> Icons.Default.Favorite
                            }
                            FilterChip(
                                selected = dest == currentDestination,
                                onClick = {
                                    if (dest == PlatformModuleDestination.MainDashboard) {
                                        onBackToDashboard()
                                    } else {
                                        onSwitchModule(dest)
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                label = { Text(dest.title) },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("module_nav_chip_${dest.route}")
                            )
                        }
                        FilterChip(
                            selected = false,
                            onClick = { onSwitchModule(PlatformModuleDestination.AgeGatedDatingSanctuary) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Open 18+ Sanctuary",
                                    tint = HoloCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text("18+ Sanctuary") },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("module_nav_chip_${PlatformModuleDestination.AgeGatedDatingSanctuary.route}")
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                content()
            }
        }
    }
}
