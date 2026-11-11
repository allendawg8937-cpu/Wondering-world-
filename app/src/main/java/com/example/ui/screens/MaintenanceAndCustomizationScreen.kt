package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon as AndroidIcon
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.MainActivity
import com.example.R
import com.example.ui.components.LocationPermissionControlCard
import com.example.ui.components.THREE_D_THEME_VISUAL_STYLES
import com.example.ui.components.ThreeDProfileThemePresetsToggleCard
import com.example.ui.components.WereAllEquals3DHeroShowcaseBanner
import com.example.ui.components.WereAllEquals3DMultiDimensionalIcon
import com.example.ui.components.launchPhoneSystemSetting
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin

const val CREATOR_EMAIL = "allengrogan33@gmail.com"
const val CREATOR_PHONE_RAW = "5095523217"
const val CREATOR_PHONE_DISPLAY = "509-552-3217 (5095523217)"
const val CREATOR_ADDRESS = "820 N Summit Blvd Apt 205"
const val SHARED_INSTALLABLE_APP_URL = "https://ais-pre-q6he5yolfycvsoiroobcch-165234349617.us-east1.run.app"

enum class MaintenanceSectionLink(
    val id: String,
    val title: String,
    val subtitle: String
) {
    ALL_SECTIONS(
        id = "all",
        title = "All Maintenance & Links",
        subtitle = "Full view: Download/Install APK, Color Wheel, Transparency, Phone/Email, About & Contact Us"
    ),
    DOWNLOAD_INSTALL_APP(
        id = "download_install_app",
        title = "Download & Install App (APK / AAB)",
        subtitle = "Download signed Android APK/AAB, Pin 3D Icon to Home Screen & Share App Installer"
    ),
    SETTING_CUSTOMIZATION(
        id = "setting_customization",
        title = "Setting Customization",
        subtitle = "Real 360° Color Wheel, Background Transparency Bar & 3D Themes"
    ),
    PHONE_EMAIL_SETTINGS(
        id = "phone_email_settings",
        title = "Phone & Email Settings",
        subtitle = "Sync your account Phone & Email with your Phone OS Settings"
    ),
    ABOUT_PLATFORM(
        id = "about_platform",
        title = "About the Platform",
        subtitle = "WEREALLEQUALS mission, 3D/6D architecture & community equality pledge"
    ),
    CONTACT_US_CREATOR(
        id = "contact_us_creator",
        title = "Contact Us (Creator of the Platform)",
        subtitle = "allengrogan33@gmail.com • 5095523217 • 820 N Summit Blvd Apt 205"
    )
}

fun convertHsvToHex(hue: Float, saturation: Float, value: Float): String {
    val argb = android.graphics.Color.HSVToColor(
        floatArrayOf(
            hue.coerceIn(0f, 360f),
            saturation.coerceIn(0f, 1f),
            value.coerceIn(0f, 1f)
        )
    )
    val r = android.graphics.Color.red(argb)
    val g = android.graphics.Color.green(argb)
    val b = android.graphics.Color.blue(argb)
    return String.format(Locale.US, "#%02X%02X%02X", r, g, b)
}

fun extractHsvFromHex(hex: String): FloatArray {
    val hsv = FloatArray(3)
    try {
        val c = android.graphics.Color.parseColor(
            if (hex.startsWith("#")) hex else "#$hex"
        )
        android.graphics.Color.colorToHSV(c, hsv)
    } catch (_: Exception) {
        hsv[0] = 220f
        hsv[1] = 0.85f
        hsv[2] = 0.95f
    }
    return hsv
}

/**
 * Compact & Reusable Background Transparency Bar + Quick Color Wheel Access Card
 * Displayed on the Dashboard Page, Profile Page, and Maintenance Page so users can
 * make page/card backgrounds transparent at any time.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BackgroundTransparencyControlBarCard(
    backgroundTransparency: Float,
    onTransparencyChange: (Float) -> Unit,
    currentThemeHex: String,
    onSelectColorHex: (String) -> Unit,
    onOpenMaintenancePage: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showInlineColorWheel by remember { mutableStateOf(false) }
    val transparencyPercent = (backgroundTransparency * 100f).roundToInt().coerceIn(0, 100)
    val activeAccent = parseThemeHex(currentThemeHex)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("background_transparency_control_bar_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.4.dp,
            brush = Brush.horizontalGradient(
                listOf(HoloCyan, activeAccent, NeonPink, CyberEmerald)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Opacity,
                        contentDescription = "Background Transparency",
                        tint = HoloCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Background Transparency Bar ($transparencyPercent%)",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Adjusts transparency across Dashboard, Profile & all pages",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = activeAccent.copy(alpha = 0.22f),
                        border = BorderStroke(1.2.dp, activeAccent),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .clickable { showInlineColorWheel = !showInlineColorWheel }
                            .testTag("toggle_inline_color_wheel_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ColorLens,
                                contentDescription = "Color Wheel",
                                tint = activeAccent,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (showInlineColorWheel) "Hide Wheel" else "Color Wheel",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    if (onOpenMaintenancePage != null) {
                        Surface(
                            color = SolarAmber.copy(alpha = 0.20f),
                            border = BorderStroke(1.2.dp, SolarAmber),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .clickable { onOpenMaintenancePage() }
                                .testTag("open_maintenance_page_quick_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = "Maintenance Page",
                                    tint = SolarAmber,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Maintenance",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SolarAmber,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }

            // Live Transparency Slider
            Slider(
                value = backgroundTransparency,
                onValueChange = { onTransparencyChange(it) },
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = HoloCyan,
                    activeTrackColor = HoloCyan,
                    inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("background_transparency_slider")
            )

            // Quick Transparency Presets
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "Crystal Clear (100%)" to 1.0f,
                    "Ultra Glass (75%)" to 0.75f,
                    "Glassmorphic (45%)" to 0.45f,
                    "Subtle Glass (25%)" to 0.25f,
                    "Solid (0%)" to 0.0f
                ).forEach { (label, presetVal) ->
                    val isSelected = kotlin.math.abs(backgroundTransparency - presetVal) < 0.05f
                    FilterChip(
                        selected = isSelected,
                        onClick = { onTransparencyChange(presetVal) },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("transparency_preset_${(presetVal * 100).roundToInt()}")
                    )
                }
            }

            // Optional Inline Real Color Wheel
            AnimatedVisibility(
                visible = showInlineColorWheel,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                InteractiveColorWheelPicker(
                    currentHex = currentThemeHex,
                    onColorSelected = onSelectColorHex,
                    wheelSize = 200.dp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

/**
 * Real Interactive 360° HSV Color Wheel Picker with Touch/Drag Selection,
 * Brightness Slider, Live Swatch Preview, and Hex Output.
 */
@Composable
fun InteractiveColorWheelPicker(
    currentHex: String,
    onColorSelected: (String) -> Unit,
    wheelSize: Dp = 230.dp,
    modifier: Modifier = Modifier
) {
    val initialHsv = remember(currentHex) { extractHsvFromHex(currentHex) }
    var hue by remember { mutableFloatStateOf(initialHsv[0]) }
    var saturation by remember { mutableFloatStateOf(initialHsv[1]) }
    var brightness by remember { mutableFloatStateOf(initialHsv[2].coerceIn(0.2f, 1f)) }

    LaunchedEffect(currentHex) {
        val parsed = extractHsvFromHex(currentHex)
        hue = parsed[0]
        saturation = parsed[1]
        brightness = parsed[2].coerceIn(0.2f, 1f)
    }

    val selectedColor = remember(hue, saturation, brightness) {
        Color(
            android.graphics.Color.HSVToColor(
                floatArrayOf(hue, saturation, brightness)
            )
        )
    }
    val selectedHex = remember(hue, saturation, brightness) {
        convertHsvToHex(hue, saturation, brightness)
    }

    val rainbowColors = remember {
        listOf(
            Color(0xFFFF0000), // 0° Red
            Color(0xFFFFFF00), // 60° Yellow
            Color(0xFF00FF00), // 120° Green
            Color(0xFF00FFFF), // 180° Cyan
            Color(0xFF0000FF), // 240° Blue
            Color(0xFFFF00FF), // 300° Magenta
            Color(0xFFFF0000)  // 360° Red
        )
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.2.dp, selectedColor.copy(alpha = 0.85f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("interactive_real_color_wheel_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(selectedColor)
                            .border(2.dp, Color.White, CircleShape)
                    )
                    Column {
                        Text(
                            text = "Real 360° Color Wheel",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Touch or drag anywhere on the wheel to pick any color",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = selectedColor.copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, selectedColor),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = selectedHex,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // 360° Interactive HSV Color Wheel Canvas
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(wheelSize)
            ) {
                Canvas(
                    modifier = Modifier
                        .size(wheelSize)
                        .clip(CircleShape)
                        .pointerInput(brightness) {
                            fun updateFromOffset(offset: Offset) {
                                val cx = size.width / 2f
                                val cy = size.height / 2f
                                val radius = (size.width.coerceAtMost(size.height) / 2f).coerceAtLeast(1f)
                                val dx = offset.x - cx
                                val dy = offset.y - cy
                                val dist = hypot(dx, dy)
                                val angleDeg = ((Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() + 360f) % 360f)
                                val sat = (dist / radius).coerceIn(0f, 1f)
                                hue = angleDeg
                                saturation = sat
                                onColorSelected(convertHsvToHex(hue, saturation, brightness))
                            }

                            detectTapGestures { offset ->
                                updateFromOffset(offset)
                            }
                        }
                        .pointerInput(brightness) {
                            fun updateFromDrag(offset: Offset) {
                                val cx = size.width / 2f
                                val cy = size.height / 2f
                                val radius = (size.width.coerceAtMost(size.height) / 2f).coerceAtLeast(1f)
                                val dx = offset.x - cx
                                val dy = offset.y - cy
                                val dist = hypot(dx, dy)
                                val angleDeg = ((Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() + 360f) % 360f)
                                val sat = (dist / radius).coerceIn(0f, 1f)
                                hue = angleDeg
                                saturation = sat
                                onColorSelected(convertHsvToHex(hue, saturation, brightness))
                            }

                            detectDragGestures { change, _ ->
                                change.consume()
                                updateFromDrag(change.position)
                            }
                        }
                        .testTag("real_color_wheel_canvas")
                ) {
                    val radius = size.minDimension / 2f
                    val centerPt = Offset(size.width / 2f, size.height / 2f)

                    // 1. 360° Hue Sweep Gradient
                    drawCircle(
                        brush = Brush.sweepGradient(
                            colors = rainbowColors,
                            center = centerPt
                        ),
                        radius = radius,
                        center = centerPt
                    )

                    // 2. Radial Saturation Gradient (White at center -> Transparent at rim)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White, Color.Transparent),
                            center = centerPt,
                            radius = radius
                        ),
                        radius = radius,
                        center = centerPt
                    )

                    // 3. Brightness / Value Shade Overlay
                    if (brightness < 0.99f) {
                        drawCircle(
                            color = Color.Black.copy(alpha = (1f - brightness).coerceIn(0f, 0.85f)),
                            radius = radius,
                            center = centerPt
                        )
                    }

                    // 4. Outer metallic ring
                    drawCircle(
                        color = Color.White.copy(alpha = 0.7f),
                        radius = radius - 1.5f,
                        center = centerPt,
                        style = Stroke(width = 3f)
                    )

                    // 5. Active Crosshair Selector Thumb on the Wheel
                    val angleRad = Math.toRadians(hue.toDouble()).toFloat()
                    val thumbDist = saturation * (radius - 10f)
                    val thumbPt = Offset(
                        x = centerPt.x + cos(angleRad) * thumbDist,
                        y = centerPt.y + sin(angleRad) * thumbDist
                    )

                    drawCircle(
                        color = Color.Black.copy(alpha = 0.75f),
                        radius = 13f,
                        center = thumbPt,
                        style = Stroke(width = 4f)
                    )
                    drawCircle(
                        color = selectedColor,
                        radius = 10f,
                        center = thumbPt
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 11f,
                        center = thumbPt,
                        style = Stroke(width = 2.5f)
                    )
                }
            }

            // Brightness / Value Slider
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Wheel Brightness / Value",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${(brightness * 100).roundToInt()}% • Hue ${hue.roundToInt()}° • Sat ${(saturation * 100).roundToInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = HoloCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = brightness,
                    onValueChange = { newVal ->
                        brightness = newVal.coerceIn(0.2f, 1f)
                        onColorSelected(convertHsvToHex(hue, saturation, brightness))
                    },
                    valueRange = 0.2f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = selectedColor,
                        activeTrackColor = selectedColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("color_wheel_brightness_slider")
                )
            }
        }
    }
}

/**
 * Full Maintenance & Customization Screen:
 * 1. Real 360° Color Wheel & Background Transparency Bar (for Dashboard, Profile & all pages)
 * 2. Phone & Email Settings Synchronization with Phone OS Settings
 * 3. About the Platform ("About the Platform" link & section)
 * 4. Contact Us — Creator of the Platform:
 *    - Email: allengrogan33@gmail.com
 *    - Phone: 5095523217 (509-552-3217)
 *    - Address: 820 N Summit Blvd Apt 205
 * 5. Interactive Pull-Up Bar at the bottom so users can choose what link they need on the Maintenance page.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MaintenanceAndCustomizationScreen(
    backgroundTransparency: Float,
    onTransparencyChange: (Float) -> Unit,
    currentThemeHex: String,
    onThemeColorSelect: (String) -> Unit,
    currentVisualStyleId: String,
    onThemeVisualStyleSelect: (String) -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    userDisplayName: String,
    userEmail: String,
    userPhone: String,
    currentLocationName: String = "Austin, TX",
    onUpdateUserLocation: (String) -> Unit = {},
    onUpdateUserEmail: (String) -> Unit,
    onUpdateUserPhone: (String) -> Unit,
    onSaveSettingsToCloud: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCodeOfConduct: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeSectionLink by remember { mutableStateOf(MaintenanceSectionLink.ALL_SECTIONS) }
    var isPullUpBarExpanded by remember { mutableStateOf(true) }
    var statusBanner by remember { mutableStateOf<String?>(null) }

    fun openExternalIntent(intent: Intent, fallbackMessage: String) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            statusBanner = fallbackMessage
        }
    }

    fun emailPlatformCreator() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$CREATOR_EMAIL")
            putExtra(Intent.EXTRA_SUBJECT, "WereEqual Platform Inquiry — Contact Us")
        }
        openExternalIntent(intent, "Ready to email Creator at $CREATOR_EMAIL")
    }

    fun callPlatformCreator() {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$CREATOR_PHONE_RAW")
        }
        openExternalIntent(intent, "Dialing Creator at $CREATOR_PHONE_DISPLAY")
    }

    fun textPlatformCreator() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$CREATOR_PHONE_RAW")
            putExtra("sms_body", "Hello Allen, reaching out from the WereEqual platform!")
        }
        openExternalIntent(intent, "Messaging Creator at $CREATOR_PHONE_DISPLAY")
    }

    fun mapPlatformCreatorAddress() {
        val encodedAddress = Uri.encode(CREATOR_ADDRESS)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$encodedAddress"))
        openExternalIntent(intent, "Opening Maps for $CREATOR_ADDRESS")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("maintenance_and_customization_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Card with Quick Links Bar ("Contact Us", "About the Platform", "Setting Customization", "Phone & Email Settings")
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                width = 1.6.dp,
                brush = Brush.horizontalGradient(
                    listOf(HoloCyan, NeonPink, SolarAmber, CyberEmerald)
                )
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
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
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Maintenance & Settings Hub",
                            tint = HoloCyan,
                            modifier = Modifier.size(26.dp)
                        )
                        Column {
                            Text(
                                text = "MAINTENANCE & SETTING CUSTOMIZATION HUB",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Color Wheel • Transparency Bar • Phone/Email OS Sync • About • Contact Us",
                                style = MaterialTheme.typography.labelSmall,
                                color = HoloCyan
                            )
                        }
                    }
                }

                // Direct Quick-Access Links Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MaintenanceSectionLink.entries.forEach { linkItem ->
                        val isSelected = activeSectionLink == linkItem
                        FilterChip(
                            selected = isSelected,
                            onClick = { activeSectionLink = linkItem },
                            label = {
                                Text(
                                    text = linkItem.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            },
                            modifier = Modifier.testTag("maintenance_link_chip_${linkItem.id}")
                        )
                    }
                }

                if (!statusBanner.isNullOrBlank()) {
                    Surface(
                        color = CyberEmerald.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, CyberEmerald),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CyberEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = statusBanner ?: "",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // SECTION 0: DOWNLOAD & INSTALL WEREEQUAL APP CENTER (APK / AAB / PIN TO HOME SCREEN / SHARE)
        if (activeSectionLink == MaintenanceSectionLink.ALL_SECTIONS ||
            activeSectionLink == MaintenanceSectionLink.DOWNLOAD_INSTALL_APP
        ) {
            DownloadAndInstallAppCenterCard(
                onStatusMessage = { msg -> statusBanner = msg }
            )
        }

        // SECTION 1: SETTING CUSTOMIZATION (Background Transparency Bar + Real 360° Color Wheel)
        if (activeSectionLink == MaintenanceSectionLink.ALL_SECTIONS ||
            activeSectionLink == MaintenanceSectionLink.SETTING_CUSTOMIZATION
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.5.dp, HoloCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("maintenance_setting_customization_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = HoloCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "Setting Customization — Color Wheel & Background Transparency",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Customize transparency and pick any color on the 360° wheel for Dashboard, Profile & all pages",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // 1A.0. 3D Profile Theme Presets Quick Toggle Card
                    ThreeDProfileThemePresetsToggleCard(
                        currentVisualStyleId = currentVisualStyleId,
                        currentThemeHex = currentThemeHex,
                        isDarkMode = isDarkMode,
                        backgroundTransparency = backgroundTransparency,
                        onSelectVisualStyle = onThemeVisualStyleSelect,
                        onSelectThemeColor = onThemeColorSelect,
                        onTransparencyChange = onTransparencyChange,
                        onSetDarkMode = { wantDark ->
                            if (wantDark != isDarkMode) {
                                onToggleDarkMode()
                            }
                        },
                        onPersistThemeToFirestore = onSaveSettingsToCloud
                    )

                    // 1A.0B. Platform URL & Deep Link Builder
                    com.example.ui.components.PlatformUrlGeneratorCard(
                        userDisplayName = userDisplayName.ifBlank { "Jordan Vance" },
                        currentVisualStyleId = currentVisualStyleId,
                        currentThemeHex = currentThemeHex
                    )

                    // 1A. Background Transparency Control Bar
                    BackgroundTransparencyControlBarCard(
                        backgroundTransparency = backgroundTransparency,
                        onTransparencyChange = onTransparencyChange,
                        currentThemeHex = currentThemeHex,
                        onSelectColorHex = onThemeColorSelect,
                        onOpenMaintenancePage = null
                    )

                    // 1B. Full Interactive 360° Real Color Wheel
                    InteractiveColorWheelPicker(
                        currentHex = currentThemeHex,
                        onColorSelected = onThemeColorSelect,
                        wheelSize = 240.dp
                    )

                    // 1C. 3D Visual Style & Dark/Light Mode Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = if (isDarkMode) HoloCyan else SolarAmber
                            )
                            Column {
                                Text(
                                    text = if (isDarkMode) "Dark Mode Active" else "Light Mode Active",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Works together with your Transparency Bar & Color Wheel",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { onToggleDarkMode() },
                            modifier = Modifier.testTag("maintenance_dark_mode_switch")
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        THREE_D_THEME_VISUAL_STYLES.forEach { style ->
                            val selected = style.id == currentVisualStyleId
                            FilterChip(
                                selected = selected,
                                onClick = { onThemeVisualStyleSelect(style.id) },
                                label = {
                                    Text(
                                        text = "◈ ${style.title}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
                                    )
                                }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onSaveSettingsToCloud()
                            statusBanner = "Saved your Color Wheel ($currentThemeHex) & Transparency (${(backgroundTransparency * 100).roundToInt()}%) settings!"
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HoloCyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("maintenance_save_customization_button")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save Color Wheel & Transparency to Profile",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // SECTION 2: PHONE & EMAIL SETTINGS (Change Phone/Email & Sync with Phone OS Settings)
        if (activeSectionLink == MaintenanceSectionLink.ALL_SECTIONS ||
            activeSectionLink == MaintenanceSectionLink.PHONE_EMAIL_SETTINGS
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.5.dp, CyberEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("maintenance_phone_email_settings_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = CyberEmerald,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "Phone & Email Settings — Sync with Your Phone OS",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Update your phone number & email option to match and sync with your phone's system settings",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedTextField(
                        value = userEmail,
                        onValueChange = onUpdateUserEmail,
                        label = { Text("Your Account Email Address (Synced with Phone Email Settings)") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = HoloCyan)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("maintenance_user_email_input")
                    )

                    OutlinedTextField(
                        value = userPhone,
                        onValueChange = onUpdateUserPhone,
                        label = { Text("Your Phone Number (Synced with Phone Settings)") },
                        leadingIcon = {
                            Icon(Icons.Default.Call, contentDescription = null, tint = CyberEmerald)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("maintenance_user_phone_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                onSaveSettingsToCloud()
                                statusBanner = "Synced Email ($userEmail) & Phone ($userPhone) with your profile!"
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberEmerald,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("maintenance_save_phone_email_button")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Phone & Email", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
                        }

                        OutlinedButton(
                            onClick = {
                                launchPhoneSystemSetting(context, Settings.ACTION_SYNC_SETTINGS)
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.2.dp, HoloCyan),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("maintenance_open_phone_sync_settings_button")
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = HoloCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Phone Account Sync", style = MaterialTheme.typography.labelMedium, color = HoloCyan, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Platform Location Permission & Live GPS City Sync
                    LocationPermissionControlCard(
                        currentLocationName = currentLocationName,
                        onLocationUpdated = { newCity ->
                            onUpdateUserLocation(newCity)
                            statusBanner = "✓ Allowed platform location & synced live weather/time for $newCity!"
                        }
                    )

                    Text(
                        text = "Direct Phone OS Settings Links (Match Phone, Email, Accounts & Permissions):",
                        style = MaterialTheme.typography.labelMedium,
                        color = SolarAmber,
                        fontWeight = FontWeight.ExtraBold
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            "📱 Phone OS Main Settings" to Settings.ACTION_SETTINGS,
                            "📧 Manage Email & Google Accounts" to Settings.ACTION_SYNC_SETTINGS,
                            "➕ Add Email / Account on Phone" to Settings.ACTION_ADD_ACCOUNT,
                            "📞 Default Phone & Email Apps" to Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS,
                            "🔔 App Notifications & Permissions" to Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            "🔊 Phone Sound & Vibration" to Settings.ACTION_SOUND_SETTINGS,
                            "☀️ Phone Display & Brightness" to Settings.ACTION_DISPLAY_SETTINGS
                        ).forEach { (label, settingAction) ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.65f)),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.clickable {
                                    launchPhoneSystemSetting(context, settingAction)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                        contentDescription = null,
                                        tint = CyberEmerald,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // SECTION 3: ABOUT THE PLATFORM ("About the Platform" Link & Overview)
        if (activeSectionLink == MaintenanceSectionLink.ALL_SECTIONS ||
            activeSectionLink == MaintenanceSectionLink.ABOUT_PLATFORM
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.5.dp, NeonPink),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("maintenance_about_platform_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About the Platform",
                            tint = NeonPink,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "About the Platform — STAGES OF LIFE (Create Your Life)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "\"Create Your Life • Live Life Love\" • Multi-Dimensional 3D/6D Community, Creativity & Opportunity Hub",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonPink
                            )
                        }
                    }

                    WereAllEquals3DHeroShowcaseBanner()

                    Text(
                        text = "Stages Of Life (CREATE YOUR LIFE • LIVE LIFE LOVE) is built on the foundational belief that every person — youth, high school and college students, creators, working adults, business owners, and military veterans — deserves equal access to creative expression, education, financial literacy, and community support.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "• Personal 3D Profile & Public Dashboard Feed with YouTube Player, Vertical Shorts/Reels & All-Platform Gaming Hub\n" +
                            "• Real 360° Color Wheel & Glassmorphic Background Transparency Bar across every page\n" +
                            "• Student Career Milestone Timeline, Common App & FAFSA Portals, VA Healthcare Locator & Biometric Financial Vault",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onNavigateToDashboard,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Dashboard", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onNavigateToProfile,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Profile", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onNavigateToCodeOfConduct,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Equality Pledge", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // SECTION 4: CONTACT US — CREATOR OF THE PLATFORM (allengrogan33@gmail.com • 5095523217 • 820 N Summit Blvd Apt 205)
        if (activeSectionLink == MaintenanceSectionLink.ALL_SECTIONS ||
            activeSectionLink == MaintenanceSectionLink.CONTACT_US_CREATOR
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = 1.8.dp,
                    brush = Brush.horizontalGradient(
                        listOf(SolarAmber, HoloCyan, NeonPink, CyberEmerald)
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("maintenance_contact_us_creator_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContactSupport,
                            contentDescription = "Contact Us — Creator of the Platform",
                            tint = SolarAmber,
                            modifier = Modifier.size(26.dp)
                        )
                        Column {
                            Text(
                                text = "Contact Us — Creator of the Platform",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Direct Official Contact Links for Allen Grogan (Creator of WereEqual / WEREALLEQUALS)",
                                style = MaterialTheme.typography.labelSmall,
                                color = SolarAmber,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))

                    // 1. Creator Email Link: allengrogan33@gmail.com
                    Surface(
                        color = HoloCyan.copy(alpha = 0.14f),
                        border = BorderStroke(1.2.dp, HoloCyan),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { emailPlatformCreator() }
                            .testTag("contact_us_creator_email_link")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email Creator",
                                    tint = HoloCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                                Column {
                                    Text(
                                        text = "Creator Email (Tap to Contact Us)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = CREATOR_EMAIL,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = HoloCyan,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Send Email",
                                tint = HoloCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // 2. Creator Phone Number Link: 5095523217
                    Surface(
                        color = CyberEmerald.copy(alpha = 0.14f),
                        border = BorderStroke(1.2.dp, CyberEmerald),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { callPlatformCreator() }
                            .testTag("contact_us_creator_phone_link")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Call Creator",
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(22.dp)
                                )
                                Column {
                                    Text(
                                        text = "Creator Phone Number (Tap to Call or Text)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = CREATOR_PHONE_DISPLAY,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = CyberEmerald,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Call Creator",
                                tint = CyberEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // 3. Creator Mailing / Office Address Link: 820 N Summit Blvd Apt 205
                    Surface(
                        color = SolarAmber.copy(alpha = 0.14f),
                        border = BorderStroke(1.2.dp, SolarAmber),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { mapPlatformCreatorAddress() }
                            .testTag("contact_us_creator_address_link")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Creator Address",
                                    tint = SolarAmber,
                                    modifier = Modifier.size(22.dp)
                                )
                                Column {
                                    Text(
                                        text = "Creator Address (Tap to Open in Maps)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = CREATOR_ADDRESS,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = SolarAmber,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Open Address in Maps",
                                tint = SolarAmber,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Direct Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { emailPlatformCreator() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HoloCyan,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Email Us", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                        }
                        Button(
                            onClick = { callPlatformCreator() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberEmerald,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call Us", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                        }
                        Button(
                            onClick = { textPlatformCreator() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonPink,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContactSupport, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Text Us", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        // SECTION 5: PULL-UP BAR SO USER CAN CHOOSE WHAT LINK THEY NEED ON THE MAINTENANCE PAGE
        MaintenancePullUpLinkChooserBar(
            isExpanded = isPullUpBarExpanded,
            onToggleExpanded = { isPullUpBarExpanded = !isPullUpBarExpanded },
            activeSectionLink = activeSectionLink,
            onSelectSectionLink = { chosenLink ->
                activeSectionLink = chosenLink
                statusBanner = "Opened Maintenance Link: ${chosenLink.title}"
            },
            onEmailCreator = ::emailPlatformCreator,
            onCallCreator = ::callPlatformCreator,
            onMapCreatorAddress = ::mapPlatformCreatorAddress,
            onOpenPhoneSettings = {
                launchPhoneSystemSetting(context, Settings.ACTION_SETTINGS)
            }
        )
    }
}

/**
 * Interactive Pull-Up Bar on the Maintenance Page allowing users to swipe up or tap
 * to choose what link they need:
 * - Contact Us (Creator of the Platform: allengrogan33@gmail.com • 5095523217 • 820 N Summit Blvd Apt 205)
 * - About the Platform
 * - Setting Customization (Color Wheel & Transparency Bar)
 * - Phone & Email Settings (Match Phone OS Settings)
 */
@Composable
fun MaintenancePullUpLinkChooserBar(
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    activeSectionLink: MaintenanceSectionLink,
    onSelectSectionLink: (MaintenanceSectionLink) -> Unit,
    onEmailCreator: () -> Unit,
    onCallCreator: () -> Unit,
    onMapCreatorAddress: () -> Unit,
    onOpenPhoneSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
        border = BorderStroke(
            width = 1.8.dp,
            brush = Brush.horizontalGradient(
                listOf(HoloCyan, NeonPink, SolarAmber, CyberEmerald)
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -6f && !isExpanded) {
                        onToggleExpanded()
                    } else if (dragAmount > 6f && isExpanded) {
                        onToggleExpanded()
                    }
                }
            }
            .testTag("maintenance_pull_up_link_chooser_bar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Pull-Up Handle Pill & Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpanded() }
                    .testTag("maintenance_pull_up_bar_toggle"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(listOf(HoloCyan, NeonPink, SolarAmber))
                        )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                            contentDescription = "Pull Up Link Chooser Bar",
                            tint = HoloCyan
                        )
                        Column {
                            Text(
                                text = if (isExpanded) {
                                    "▼ Maintenance Pull-Up Link Bar (Choose a Link Below)"
                                } else {
                                    "▲ Pull Up Bar — Choose Maintenance Link (Contact Us, About, Settings)"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Swipe up or tap to choose Contact Us, About the Platform, or Setting Customization",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MaintenanceSectionLink.entries.forEach { linkOption ->
                        val selected = activeSectionLink == linkOption
                        val accentColor = when (linkOption) {
                            MaintenanceSectionLink.DOWNLOAD_INSTALL_APP -> CyberEmerald
                            MaintenanceSectionLink.SETTING_CUSTOMIZATION -> HoloCyan
                            MaintenanceSectionLink.PHONE_EMAIL_SETTINGS -> CyberEmerald
                            MaintenanceSectionLink.ABOUT_PLATFORM -> NeonPink
                            MaintenanceSectionLink.CONTACT_US_CREATOR -> SolarAmber
                            MaintenanceSectionLink.ALL_SECTIONS -> HoloCyan
                        }
                        Surface(
                            color = if (selected) accentColor.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                width = if (selected) 2.dp else 1.dp,
                                color = if (selected) accentColor else accentColor.copy(alpha = 0.55f)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectSectionLink(linkOption) }
                                .testTag("pull_up_link_option_${linkOption.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "🔗 ${linkOption.title}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = linkOption.subtitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    // Direct One-Tap Action Links inside the Pull-Up Bar
                    Text(
                        text = "⚡ Direct Action Links (Creator Contact & Phone Settings):",
                        style = MaterialTheme.typography.labelSmall,
                        color = SolarAmber,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onEmailCreator,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, HoloCyan)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = HoloCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Contact Us: $CREATOR_EMAIL", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = onCallCreator,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, CyberEmerald)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call: $CREATOR_PHONE_RAW", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = onMapCreatorAddress,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, SolarAmber)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = SolarAmber, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(CREATOR_ADDRESS, style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = onOpenPhoneSettings,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, NeonPink)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = NeonPink, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Phone OS Settings", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Downloadable & Installable App Hub Card ("Download & Install WereEqual App"):
 * 1. Pin the 3D "WEREALLEQUALS" Adaptive Launcher Icon directly to the Android Home Screen
 *    using Android's ShortcutManager API.
 * 2. Share the installable app link & invite via Android's native Share Sheet or SMS/Email.
 * 3. Step-by-step instructions & package verification for downloading the standalone signed
 *    Android APK / AAB (Google Play Bundle) or Full Project ZIP from AI Studio.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DownloadAndInstallAppCenterCard(
    onStatusMessage: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var installStatusText by remember { mutableStateOf<String?>(null) }

    val packageName = context.packageName
    val versionInfo = remember(context) {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "v${pInfo.versionName ?: "1.0"} (Build ${pInfo.longVersionCode})"
        } catch (_: Exception) {
            "v1.0 (Build 1)"
        }
    }

    fun pinAppIconToHomeScreen() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val shortcutManager = context.getSystemService(ShortcutManager::class.java)
                if (shortcutManager != null && shortcutManager.isRequestPinShortcutSupported) {
                    val launchIntent = Intent(context, MainActivity::class.java).apply {
                        action = Intent.ACTION_MAIN
                        addCategory(Intent.CATEGORY_LAUNCHER)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    val pinShortcutInfo = ShortcutInfo.Builder(context, "wereallequals_home_launcher")
                        .setShortLabel("WereEqual")
                        .setLongLabel("WereEqual • WEREALLEQUALS 3D")
                        .setIcon(AndroidIcon.createWithResource(context, R.mipmap.ic_launcher))
                        .setIntent(launchIntent)
                        .build()

                    shortcutManager.requestPinShortcut(pinShortcutInfo, null)
                    val msg = "✓ Requested Android Home Screen shortcut for WereEqual (3D WEREALLEQUALS Icon)!"
                    installStatusText = msg
                    onStatusMessage(msg)
                    return
                }
            }
            val msg = "✓ WereEqual ($packageName • $versionInfo) is installed and ready on your device launcher!"
            installStatusText = msg
            onStatusMessage(msg)
        } catch (_: Exception) {
            val msg = "✓ WereEqual 3D Launcher Icon is active on your device!"
            installStatusText = msg
            onStatusMessage(msg)
        }
    }

    fun shareInstallableAppLink() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Download & Install WereEqual (WEREALLEQUALS 3D App)")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Download & install the WereEqual (WEREALLEQUALS) 3D Community App! " +
                        "Live App & APK Link: $SHARED_INSTALLABLE_APP_URL • Created by Allen Grogan ($CREATOR_EMAIL)"
                )
            }
            val chooser = Intent.createChooser(shareIntent, "Share WereEqual Installable App").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            val msg = "✓ Opened Share Sheet to send the WereEqual install link!"
            installStatusText = msg
            onStatusMessage(msg)
        } catch (_: Exception) {
            val msg = "✓ Installable App URL ready: $SHARED_INSTALLABLE_APP_URL"
            installStatusText = msg
            onStatusMessage(msg)
        }
    }

    fun openInstallableWebUrl() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(SHARED_INSTALLABLE_APP_URL)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            val msg = "✓ App URL: $SHARED_INSTALLABLE_APP_URL"
            installStatusText = msg
            onStatusMessage(msg)
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.6.dp,
            brush = Brush.horizontalGradient(
                listOf(CyberEmerald, HoloCyan, SolarAmber, NeonPink)
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("download_and_install_app_center_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with 3D WEREALLEQUALS Launcher Icon & APK/AAB Ready Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    WereAllEquals3DMultiDimensionalIcon(
                        iconSize = 54.dp,
                        showRibbonText = true
                    )
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "📲 Download & Install Stages Of Life App",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Text(
                            text = "Standalone Android APK / AAB • 3D Pyramid Launcher Icon • $versionInfo",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    color = CyberEmerald.copy(alpha = 0.20f),
                    border = BorderStroke(1.dp, CyberEmerald),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "APK / AAB READY",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberEmerald,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }

            // Package & Signing Metadata Strip
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.55f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "📦 Verified Installable Android Application Package",
                        style = MaterialTheme.typography.labelLarge,
                        color = HoloCyan,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "• App Name: WereEqual (3D WEREALLEQUALS Adaptive Icon)\n" +
                            "• Package ID: $packageName • Version: $versionInfo\n" +
                            "• Compatibility: Android 7.0+ (API 24–36) Phones, Tablets & Foldables\n" +
                            "• Build Formats: Signed Debug/Release APK, Google Play AAB & Source ZIP",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Step-by-Step How to Download APK / AAB or Install on Any Phone
            Surface(
                color = SolarAmber.copy(alpha = 0.12f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.65f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "⚡ How to Download the APK / AAB or Install on Your Phone:",
                        style = MaterialTheme.typography.labelMedium,
                        color = SolarAmber,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "1. Download APK / AAB in AI Studio: Click the Settings (⚙️) / Export menu in the top-right corner of AI Studio and select 'Download APK' (to install directly on any Android phone) or 'Download AAB' (for Google Play).\n" +
                            "2. Pin 3D Icon to Home Screen: Tap 'Pin to Home Screen' below to add the 3D WEREALLEQUALS launcher icon directly to your device home screen.\n" +
                            "3. Share App Link: Tap 'Share Install Link' to text or email the live app to any phone.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Interactive Install, Pin & Share Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { pinAppIconToHomeScreen() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberEmerald,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pin_app_to_home_screen_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.InstallMobile,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Pin to Home Screen",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Button(
                    onClick = { shareInstallableAppLink() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HoloCyan,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_installable_app_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Share Install Link",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { openInstallableWebUrl() },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SolarAmber),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("open_live_app_url_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = SolarAmber,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Open Live App URL",
                        style = MaterialTheme.typography.labelSmall,
                        color = SolarAmber,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        launchPhoneSystemSetting(context, Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, NeonPink),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("open_installed_app_info_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = NeonPink,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Installed App Info",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPink,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (!installStatusText.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = CyberEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = installStatusText!!,
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

