package com.example.dating

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import java.util.Locale

/**
 * Standalone, data-isolated ViewModel for the 18+ Sanctuary.
 * Never reads or writes to main platform UserProfile, WallPost, Budget, or Friend stores.
 */
class IsolatedDatingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(IsolatedDatingUiState())
    val uiState: StateFlow<IsolatedDatingUiState> = _uiState.asStateFlow()

    fun updateBirthYear(year: String) {
        val digits = year.filter { it.isDigit() }.take(4)
        _uiState.update { it.copy(birthYearInput = digits, statusNotice = null) }
    }

    fun updateBirthMonth(month: String) {
        val digits = month.filter { it.isDigit() }.take(2)
        _uiState.update { it.copy(birthMonthInput = digits, statusNotice = null) }
    }

    fun updateBirthDay(day: String) {
        val digits = day.filter { it.isDigit() }.take(2)
        _uiState.update { it.copy(birthDayInput = digits, statusNotice = null) }
    }

    fun updateIsolatedAlias(alias: String) {
        _uiState.update { it.copy(isolatedAliasInput = alias.take(32), statusNotice = null) }
    }

    fun selectVerificationProvider(provider: AgeVerificationProvider) {
        _uiState.update {
            it.copy(
                selectedProvider = provider,
                statusNotice = "Selected ${provider.displayName} for isolated 18+ age assurance."
            )
        }
    }

    fun updateProviderReferenceCode(code: String) {
        _uiState.update { it.copy(providerReferenceCode = code.take(48), statusNotice = null) }
    }

    fun toggleConductAndPrivacyPledge(accepted: Boolean) {
        _uiState.update { it.copy(hasAcceptedConductAndPrivacyPledge = accepted, statusNotice = null) }
    }

    /**
     * Initiates a third-party provider assurance session request after validating DOB is 18+.
     */
    fun launchProviderHandshake(referenceCalendar: Calendar = Calendar.getInstance()): Boolean {
        val state = _uiState.value
        val year = state.birthYearInput.toIntOrNull()
        val month = state.birthMonthInput.toIntOrNull()
        val day = state.birthDayInput.toIntOrNull()
        if (year == null || month == null || day == null) {
            _uiState.update {
                it.copy(statusNotice = "Enter a valid MM / DD / YYYY birth date before verification.")
            }
            return false
        }
        val computedAge = AgeGateCalculator.calculateAgeYears(year, month, day, referenceCalendar)
        if (computedAge == null) {
            _uiState.update {
                it.copy(statusNotice = "Invalid calendar birth date. Check month and day values.")
            }
            return false
        }
        if (computedAge < 18) {
            _uiState.update {
                it.copy(
                    verificationStatus = DatingVerificationStatus.DeniedUnderage(
                        computedAgeYears = computedAge,
                        reason = "Access strictly blocked: computed age ($computedAge) is under 18."
                    ),
                    statusNotice = "18+ Gate Enforced: Access denied for users under 18."
                )
            }
            return false
        }

        val generatedSessionRef = "${state.selectedProvider.tokenPrefix}${year}VERIFIED"
        _uiState.update {
            it.copy(
                verificationStatus = DatingVerificationStatus.PendingProviderHandshake(
                    provider = state.selectedProvider,
                    computedAgeYears = computedAge
                ),
                providerReferenceCode = it.providerReferenceCode.ifBlank { generatedSessionRef },
                statusNotice = "Provider handshake active via ${state.selectedProvider.displayName}. Confirm token & pledge to enter."
            )
        }
        return true
    }

    fun completeVerificationAndEnter(referenceCalendar: Calendar = Calendar.getInstance()): Boolean {
        val state = _uiState.value
        val year = state.birthYearInput.toIntOrNull()
        val month = state.birthMonthInput.toIntOrNull()
        val day = state.birthDayInput.toIntOrNull()
        val computedAge = if (year != null && month != null && day != null) {
            AgeGateCalculator.calculateAgeYears(year, month, day, referenceCalendar)
        } else {
            null
        }
        if (computedAge == null) {
            _uiState.update {
                it.copy(statusNotice = "Enter a complete, valid birth date (MM / DD / YYYY).")
            }
            return false
        }
        if (computedAge < 18) {
            _uiState.update {
                it.copy(
                    verificationStatus = DatingVerificationStatus.DeniedUnderage(
                        computedAgeYears = computedAge,
                        reason = "Strictly 18+ only: calculated age ($computedAge) cannot unlock this module."
                    ),
                    statusNotice = "Blocked: Must be at least 18 years old."
                )
            }
            return false
        }
        if (!AgeGateCalculator.isValidAssuranceToken(state.providerReferenceCode, state.selectedProvider)) {
            _uiState.update {
                it.copy(
                    verificationStatus = DatingVerificationStatus.PendingProviderHandshake(
                        provider = state.selectedProvider,
                        computedAgeYears = computedAge
                    ),
                    statusNotice = "Run Third-Party Provider Check or enter a valid 10+ char assurance token."
                )
            }
            return false
        }
        if (!state.hasAcceptedConductAndPrivacyPledge) {
            _uiState.update {
                it.copy(statusNotice = "Accept the 18+ PG-13 Respectful Conduct & Data Isolation pledge to continue.")
            }
            return false
        }

        val cleanAlias = state.isolatedAliasInput.trim().ifEmpty { "Sanctuary Member" }.take(32)
        _uiState.update {
            it.copy(
                isolatedAliasInput = cleanAlias,
                verificationStatus = DatingVerificationStatus.Verified18Plus(
                    verifiedAgeYears = computedAge,
                    provider = state.selectedProvider,
                    assuranceToken = state.providerReferenceCode.trim().uppercase(Locale.US),
                    isolatedAlias = cleanAlias
                ),
                activeSection = "DISCOVER",
                statusNotice = "Verified 18+ via ${state.selectedProvider.displayName}. Isolated session active."
            )
        }
        return true
    }

    fun selectSection(section: String) {
        _uiState.update { it.copy(activeSection = section) }
    }

    fun likeCandidate(candidate: IsolatedDatingCandidate) {
        _uiState.update { state ->
            state.copy(
                likedCandidateIds = state.likedCandidateIds + candidate.id,
                candidateIndex = state.candidateIndex + 1,
                statusNotice = "Sent an isolated wave to ${candidate.alias}."
            )
        }
    }

    fun passCandidate(candidate: IsolatedDatingCandidate) {
        _uiState.update { state ->
            state.copy(
                passedCandidateIds = state.passedCandidateIds + candidate.id,
                candidateIndex = state.candidateIndex + 1,
                statusNotice = null
            )
        }
    }

    fun blockAndHideCandidate(candidate: IsolatedDatingCandidate) {
        _uiState.update { state ->
            state.copy(
                blockedCandidateIds = state.blockedCandidateIds + candidate.id,
                likedCandidateIds = state.likedCandidateIds - candidate.id,
                candidateIndex = state.candidateIndex + 1,
                statusNotice = "${candidate.alias} hidden & blocked inside isolated session."
            )
        }
    }

    fun sendIsolatedIcebreaker(recipientAlias: String, message: String) {
        val clean = message.trim().take(240)
        if (clean.isEmpty()) return
        _uiState.update { state ->
            val msg = IsolatedDatingMessage(
                id = "iso_msg_${System.currentTimeMillis()}_${state.isolatedChats.size}",
                recipientAlias = recipientAlias,
                text = clean,
                isMine = true
            )
            state.copy(
                isolatedChats = state.isolatedChats + msg,
                statusNotice = "Sent private message to $recipientAlias (stored only in isolated memory)."
            )
        }
    }

    /**
     * Immediately locks the 18+ gate and purges all isolated alias, match, and message state.
     */
    fun revokeVerificationAndPurgeSession() {
        _uiState.value = IsolatedDatingUiState(
            statusNotice = "18+ Sanctuary session wiped and re-locked."
        )
    }
}
