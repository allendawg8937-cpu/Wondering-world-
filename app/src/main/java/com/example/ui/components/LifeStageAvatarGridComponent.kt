package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Displays the six generated 3D-style character avatars (Kid, Teen, Student, Adult, Business, Veteran)
 * in a responsive selection grid with active highlighting and optional persona stage synchronization.
 */
@Composable
fun LifeStageAvatarSelectionGridCard(
    currentPersonaRole: String,
    selected3DAvatarResId: Int?,
    onSelect3DAvatarPreset: (ThreeDAvatarPreset) -> Unit,
    onSyncLifeStagePersona: ((stageId: String, defaultAge: Long, accentHex: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val activeRoleAvatar = remember(currentPersonaRole) {
        findLifeStageAvatarForRole(currentPersonaRole)
    }

    val currentlyEquippedStageAvatar = remember(selected3DAvatarResId, activeRoleAvatar) {
        LIFE_STAGE_3D_AVATARS.firstOrNull { it.drawableResId == selected3DAvatarResId }
            ?: activeRoleAvatar
    }

    var syncPersonaOnSelect by rememberSaveable { mutableStateOf(onSyncLifeStagePersona != null) }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = 1.5.dp,
            color = currentlyEquippedStageAvatar.accentColor.copy(alpha = 0.55f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("life_stage_avatar_selection_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Section Title & Spotlight Active Selection
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
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        currentlyEquippedStageAvatar.accentColor,
                                        MaterialTheme.colorScheme.primary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "3D Life Stage Avatars",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Stages Of Life • 3D Character Avatars",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Select one of the 6 bespoke 3D portraits to represent your journey",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Active Spotlight Banner + Sync Persona Toggle
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = currentlyEquippedStageAvatar.accentColor.copy(alpha = 0.12f),
                border = BorderStroke(
                    width = 1.dp,
                    color = currentlyEquippedStageAvatar.accentColor.copy(alpha = 0.45f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("life_stage_avatar_selected_banner")
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = currentlyEquippedStageAvatar.drawableResId),
                            contentDescription = "${currentlyEquippedStageAvatar.stageLabel} 3D Avatar Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "${currentlyEquippedStageAvatar.badgeEmoji} ${currentlyEquippedStageAvatar.stageLabel} • ${currentlyEquippedStageAvatar.characterTitle}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = currentlyEquippedStageAvatar.accentColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = currentlyEquippedStageAvatar.tagline,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (onSyncLifeStagePersona != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (syncPersonaOnSelect) {
                                    "Tapping an avatar also switches your active Life Stage"
                                } else {
                                    "Tapping an avatar updates your 3D portrait only"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            )
                            FilterChip(
                                selected = syncPersonaOnSelect,
                                onClick = { syncPersonaOnSelect = !syncPersonaOnSelect },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = "Toggle life stage persona sync",
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = if (syncPersonaOnSelect) "Sync Stage" else "Avatar Only",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = currentlyEquippedStageAvatar.accentColor.copy(alpha = 0.24f),
                                    selectedLabelColor = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.testTag("life_stage_avatar_sync_toggle")
                            )
                        }
                    }
                }
            }

            // Responsive 2-column (compact) / 3-column (medium/expanded) 6-Avatar Grid
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("life_stage_avatar_selection_grid")
            ) {
                val columns = if (maxWidth >= 560.dp) 3 else 2
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LIFE_STAGE_3D_AVATARS.chunked(columns).forEach { rowAvatars ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Max),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowAvatars.forEach { avatar ->
                                val isSelected = selected3DAvatarResId == avatar.drawableResId ||
                                    (selected3DAvatarResId == null && currentlyEquippedStageAvatar.stageId == avatar.stageId)

                                LifeStageAvatarGridTile(
                                    avatar = avatar,
                                    isSelected = isSelected,
                                    onClick = {
                                        onSelect3DAvatarPreset(avatar.toThreeDAvatarPreset())
                                        if (syncPersonaOnSelect) {
                                            onSyncLifeStagePersona?.invoke(
                                                avatar.stageId,
                                                avatar.defaultAge,
                                                avatar.accentHex
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                            }
                            repeat(columns - rowAvatars.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LifeStageAvatarGridTile(
    avatar: LifeStageAvatarPreset,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) {
            avatar.accentColor.copy(alpha = 0.16f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        },
        border = BorderStroke(
            width = if (isSelected) 2.5.dp else 1.dp,
            color = if (isSelected) avatar.accentColor else avatar.accentColor.copy(alpha = 0.35f)
        ),
        shadowElevation = if (isSelected) 6.dp else 1.dp,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .semantics { selected = isSelected }
            .testTag("life_stage_avatar_card_${avatar.stageId}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                Image(
                    painter = painterResource(id = avatar.drawableResId),
                    contentDescription = "${avatar.stageLabel} 3D character avatar: ${avatar.characterTitle}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Subtle bottom scrim for depth
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.72f)
                                )
                            )
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = avatar.characterTitle,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Life stage pill in top-left
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.74f),
                    border = BorderStroke(1.dp, avatar.accentColor.copy(alpha = 0.8f)),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = "${avatar.badgeEmoji} ${avatar.stageLabel.uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = avatar.accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Selection checkmark in top-right
                if (isSelected) {
                    Surface(
                        shape = CircleShape,
                        color = avatar.accentColor,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(24.dp)
                            .testTag("life_stage_avatar_selected_check_${avatar.stageId}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected ${avatar.stageLabel} 3D Avatar",
                                tint = Color.Black,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = if (isSelected) "Equipped • ${avatar.stageLabel}" else "Tap to Equip",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) avatar.accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = avatar.tagline,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
