package com.example.core.auth

import android.content.Context
import androidx.credentials.CredentialManager
import com.example.ui.AppNavScreen
import com.example.ui.DashboardTab
import com.example.ui.auth.AuthManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class RouteGuardResult(
    val allowed: Boolean,
    val resolvedPath: String,
    val targetScreen: AppNavScreen,
    val targetTab: DashboardTab?,
    val reason: String
)

/**
 * Android counterpart to `core/auth/auth.service.ts` and `core/auth/auth.guard.ts`.
 * Manages Firebase Authentication state (exclusively via Google Sign-In + CredentialManager)
 * and evaluates `canActivate: [authGuard]` for the routes defined in `app.routes.ts`
 * (`login`, `dashboard`, `profile`, `finance/goals`, `'' -> 'dashboard'`, `'**' -> 'dashboard'`).
 */
object FirebaseAuthRouteGuardService {

    val currentUser: FirebaseUser?
        get() = try {
            Firebase.auth.currentUser
        } catch (_: Exception) {
            null
        }

    val isAuthenticated: Boolean
        get() = currentUser != null

    fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val auth = try {
            Firebase.auth
        } catch (_: Exception) {
            null
        }
        if (auth == null) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    fun signInWithGoogle(
        context: Context,
        credentialManager: CredentialManager,
        scope: CoroutineScope,
        onSuccess: (FirebaseUser?) -> Unit,
        onError: (String) -> Unit
    ) {
        AuthManager.onGoogleSignInClicked(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = { onSuccess(currentUser) },
            onAuthError = onError,
            scope = scope
        )
    }

    fun signOut(
        credentialManager: CredentialManager,
        scope: CoroutineScope,
        onComplete: () -> Unit
    ) {
        AuthManager.signOut(
            credentialManager = credentialManager,
            scope = scope,
            onSignOutComplete = onComplete
        )
    }

    /**
     * Mirrors `AuthService.logout()` in `core/auth/auth.service.ts`, signing out of
     * Firebase Auth and triggering redirect to `/login`.
     */
    fun logout(onRedirectToLogin: () -> Unit = {}) {
        try {
            Firebase.auth.signOut()
        } catch (_: Exception) {
        }
        onRedirectToLogin()
    }

    /**
     * Evaluates `canActivate: [authGuard]` for any requested path in `app.routes.ts`.
     */
    fun evaluateAuthGuard(
        rawPath: String,
        requireFirebaseSession: Boolean = true
    ): RouteGuardResult {
        val cleanPath = rawPath.trim().trimStart('/').substringBefore('?').lowercase()
        if (cleanPath == "login") {
            return RouteGuardResult(
                allowed = true,
                resolvedPath = "login",
                targetScreen = AppNavScreen.LOGIN,
                targetTab = null,
                reason = "Public route 'login' (LoginPageComponent)"
            )
        }

        val signedIn = isAuthenticated
        if (requireFirebaseSession && !signedIn) {
            return RouteGuardResult(
                allowed = false,
                resolvedPath = "login",
                targetScreen = AppNavScreen.LOGIN,
                targetTab = null,
                reason = "authGuard redirected unauthenticated request '$cleanPath' -> '/login'"
            )
        }

        return when (cleanPath) {
            "", "dashboard" -> RouteGuardResult(
                allowed = true,
                resolvedPath = "dashboard",
                targetScreen = AppNavScreen.MAIN_DASHBOARD,
                targetTab = DashboardTab.HUB_3D,
                reason = "authGuard passed -> DashboardPageComponent ('dashboard')"
            )
            "profile" -> RouteGuardResult(
                allowed = true,
                resolvedPath = "profile",
                targetScreen = AppNavScreen.MAIN_DASHBOARD,
                targetTab = DashboardTab.THEME_PROFILE,
                reason = "authGuard passed -> ProfilePageComponent ('profile')"
            )
            "finance/goals", "goals" -> RouteGuardResult(
                allowed = true,
                resolvedPath = "finance/goals",
                targetScreen = AppNavScreen.MAIN_DASHBOARD,
                targetTab = DashboardTab.FINANCE_GOALS,
                reason = "authGuard passed -> GoalsPageComponent ('finance/goals')"
            )
            else -> RouteGuardResult(
                allowed = true,
                resolvedPath = "dashboard",
                targetScreen = AppNavScreen.MAIN_DASHBOARD,
                targetTab = DashboardTab.HUB_3D,
                reason = "Wildcard '**' redirected '$cleanPath' -> 'dashboard'"
            )
        }
    }
}
