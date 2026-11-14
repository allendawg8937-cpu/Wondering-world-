package com.example.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.model.CommunityConnectionCard
import com.example.data.model.UserProfile
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RosePulse
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.parseThemeHex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.hypot

data class CityCoordinates(
    val name: String,
    val lat: Double,
    val lon: Double,
    val defaultTempF: Double,
    val defaultCondition: String
)

val SUPPORTED_CITIES = listOf(
    CityCoordinates("Austin, TX", 30.2672, -97.7431, 76.0, "Sunny & Clear"),
    CityCoordinates("Spokane, WA", 47.6588, -117.4260, 62.0, "Crisp & Clear"),
    CityCoordinates("New York, NY", 40.7128, -74.0060, 64.0, "Partly Cloudy"),
    CityCoordinates("Los Angeles, CA", 34.0522, -118.2437, 74.0, "Golden Sunshine"),
    CityCoordinates("Chicago, IL", 41.8781, -87.6298, 59.0, "Crisp & Breezy"),
    CityCoordinates("Seattle, WA", 47.6062, -122.3321, 57.0, "Light Coastal Mist"),
    CityCoordinates("Denver, CO", 39.7392, -104.9903, 66.0, "Mountain Clear")
)

fun hasLocationPermissionGranted(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val coarse = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    return fine || coarse
}

data class ResolvedDeviceLocation(
    val displayName: String,
    val latitude: Double,
    val longitude: Double
)

@SuppressLint("MissingPermission")
@Suppress("DEPRECATION")
suspend fun resolveDeviceGpsLocation(context: Context, fallbackLocationName: String): ResolvedDeviceLocation =
    withContext(Dispatchers.IO) {
        val defaultCity = SUPPORTED_CITIES.firstOrNull {
            fallbackLocationName.contains(it.name.substringBefore(","), ignoreCase = true)
        } ?: SUPPORTED_CITIES.first()

        if (!hasLocationPermissionGranted(context)) {
            return@withContext ResolvedDeviceLocation(
                displayName = defaultCity.name,
                latitude = defaultCity.lat,
                longitude = defaultCity.lon
            )
        }

        try {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val providers = listOf(
                LocationManager.GPS_PROVIDER,
                LocationManager.NETWORK_PROVIDER,
                LocationManager.PASSIVE_PROVIDER
            )
            var bestLocation: Location? = null
            if (locationManager != null) {
                for (provider in providers) {
                    try {
                        if (locationManager.isProviderEnabled(provider)) {
                            val loc = locationManager.getLastKnownLocation(provider)
                            if (loc != null) {
                                if (bestLocation == null || loc.accuracy < bestLocation.accuracy) {
                                    bestLocation = loc
                                }
                            }
                        }
                    } catch (_: Exception) {
                    }
                }
            }

            if (bestLocation != null) {
                val lat = bestLocation.latitude
                val lon = bestLocation.longitude
                var resolvedCityName: String? = null
                try {
                    val geocoder = Geocoder(context, Locale.US)
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val addr = addresses[0]
                        val locality = addr.locality ?: addr.subAdminArea
                        val admin = addr.adminArea
                        if (!locality.isNullOrBlank() && !admin.isNullOrBlank()) {
                            resolvedCityName = "$locality, $admin"
                        } else if (!locality.isNullOrBlank()) {
                            resolvedCityName = locality
                        }
                    }
                } catch (_: Exception) {
                }

                if (resolvedCityName.isNullOrBlank()) {
                    val nearest = SUPPORTED_CITIES.minByOrNull {
                        hypot(it.lat - lat, it.lon - lon)
                    }
                    resolvedCityName = nearest?.name ?: String.format(Locale.US, "GPS %.2f, %.2f", lat, lon)
                }

                return@withContext ResolvedDeviceLocation(
                    displayName = resolvedCityName ?: defaultCity.name,
                    latitude = lat,
                    longitude = lon
                )
            }
        } catch (_: Exception) {
        }

        ResolvedDeviceLocation(
            displayName = defaultCity.name,
            latitude = defaultCity.lat,
            longitude = defaultCity.lon
        )
    }

/**
 * Global Time, Date, Weather & Location Permission Bar rendered on EVERY page of the platform
 * (Login, Sign Up, Dashboard, Profile, Messages, Video, News, Resources, AI Tools, Studio, Wallet,
 * Lounge, Code of Conduct, and Maintenance & Settings).
 */
@Composable
fun EveryPageTimeDateWeatherLocationBar(
    locationName: String,
    onLocationUpdated: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isLocationGranted by remember {
        mutableStateOf(hasLocationPermissionGranted(context))
    }
    var statusToast by remember { mutableStateOf<String?>(null) }

    var currentTimeText by remember {
        mutableStateOf(SimpleDateFormat("hh:mm:ss a", Locale.US).format(Date()))
    }
    var currentDateText by remember {
        mutableStateOf(SimpleDateFormat("EEE, MMM d, yyyy", Locale.US).format(Date()))
    }

    LaunchedEffect(Unit) {
        val timeFmt = SimpleDateFormat("hh:mm:ss a", Locale.US)
        val dateFmt = SimpleDateFormat("EEE, MMM d, yyyy", Locale.US)
        while (true) {
            val now = Date()
            currentTimeText = timeFmt.format(now)
            currentDateText = dateFmt.format(now)
            isLocationGranted = hasLocationPermissionGranted(context)
            delay(1000L)
        }
    }

    val matchedCity = remember(locationName) {
        SUPPORTED_CITIES.firstOrNull {
            locationName.contains(it.name.substringBefore(","), ignoreCase = true)
        } ?: SUPPORTED_CITIES.first()
    }

    var activeLat by remember(matchedCity) { mutableDoubleStateOf(matchedCity.lat) }
    var activeLon by remember(matchedCity) { mutableDoubleStateOf(matchedCity.lon) }
    var tempFahrenheit by remember(matchedCity) { mutableDoubleStateOf(matchedCity.defaultTempF) }
    var weatherCondition by remember(matchedCity) { mutableStateOf(matchedCity.defaultCondition) }
    var useCelsius by remember { mutableStateOf(false) }
    var weatherRefreshTick by remember { mutableIntStateOf(0) }

    fun syncGpsAndWeather() {
        scope.launch {
            val resolved = resolveDeviceGpsLocation(context, locationName)
            activeLat = resolved.latitude
            activeLon = resolved.longitude
            if (resolved.displayName.isNotBlank()) {
                onLocationUpdated(resolved.displayName)
            }
            weatherRefreshTick += 1
            statusToast = "GPS Active: ${resolved.displayName}"
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        isLocationGranted = granted
        if (granted) {
            syncGpsAndWeather()
        } else {
            statusToast = "Location permission denied — tap Settings to allow"
        }
    }

    LaunchedEffect(activeLat, activeLon, weatherRefreshTick) {
        withContext(Dispatchers.IO) {
            try {
                val urlStr =
                    "https://api.open-meteo.com/v1/forecast?latitude=$activeLat&longitude=$activeLon&current_weather=true&temperature_unit=fahrenheit&windspeed_unit=mph"
                val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 3500
                    readTimeout = 3500
                }
                if (conn.responseCode == 200) {
                    val body = conn.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(body)
                    val current = json.optJSONObject("current_weather")
                    if (current != null) {
                        val temp = current.optDouble("temperature", matchedCity.defaultTempF)
                        val code = current.optInt("weathercode", 0)
                        tempFahrenheit = temp
                        weatherCondition = when (code) {
                            0 -> "Clear Sky"
                            1, 2, 3 -> "Partly Cloudy"
                            45, 48 -> "Misty Fog"
                            51, 53, 55, 61, 63, 65 -> "Rain"
                            71, 73, 75 -> "Snow"
                            95, 96, 99 -> "Storm"
                            else -> "Sunny"
                        }
                    }
                }
                conn.disconnect()
            } catch (_: Exception) {
            }
        }
    }

    val displayedTemp = if (useCelsius) {
        String.format(Locale.US, "%.0f°C", (tempFahrenheit - 32.0) * 5.0 / 9.0)
    } else {
        String.format(Locale.US, "%.0f°F", tempFahrenheit)
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.horizontalGradient(listOf(HoloCyan, SolarAmber, CyberEmerald))
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("every_page_time_date_weather_location_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Live Time Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "Live Time",
                    tint = HoloCyan,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = currentTimeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Text(text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant)

            // 2. Live Date Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Live Date",
                    tint = NeonPink,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = currentDateText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant)

            // 3. Live Weather Pill (Tap to toggle °F/°C)
            Surface(
                color = SolarAmber.copy(alpha = 0.18f),
                border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .clickable { useCelsius = !useCelsius }
                    .testTag("every_page_weather_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (weatherCondition.contains("Cloud", ignoreCase = true)) {
                            Icons.Default.Cloud
                        } else {
                            Icons.Default.WbSunny
                        },
                        contentDescription = "Weather",
                        tint = SolarAmber,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "$displayedTemp $weatherCondition (${locationName.ifBlank { "Austin, TX" }})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // 4. Location Permission & GPS Sync Pill ("Allow Location" or "GPS Active")
            Surface(
                color = if (isLocationGranted) {
                    CyberEmerald.copy(alpha = 0.22f)
                } else {
                    HoloCyan.copy(alpha = 0.22f)
                },
                border = BorderStroke(
                    width = 1.2.dp,
                    color = if (isLocationGranted) CyberEmerald else HoloCyan
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .clickable {
                        if (hasLocationPermissionGranted(context)) {
                            syncGpsAndWeather()
                        } else {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    }
                    .testTag("every_page_allow_location_permission_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isLocationGranted) Icons.Default.GpsFixed else Icons.Default.MyLocation,
                        contentDescription = "Allow Location Permission",
                        tint = if (isLocationGranted) CyberEmerald else HoloCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (isLocationGranted) {
                            "📍 Location Allowed (Sync GPS)"
                        } else {
                            "📍 Allow Location Permission"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isLocationGranted) CyberEmerald else HoloCyan,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            if (!statusToast.isNullOrBlank()) {
                Text(
                    text = statusToast ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberEmerald,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Interactive Location Permission & GPS Control Card for the Maintenance & Settings Page
 * and Resource / Dashboard screens.
 */
@Composable
fun LocationPermissionControlCard(
    currentLocationName: String,
    onLocationUpdated: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isGranted by remember { mutableStateOf(hasLocationPermissionGranted(context)) }
    var gpsStatusMessage by remember {
        mutableStateOf(
            if (isGranted) {
                "Location permission is ALLOWED for Stages Of Life ($currentLocationName)"
            } else {
                "Tap 'Allow Platform Location Permission' below to grant GPS access for live local weather & resources."
            }
        )
    }

    fun refreshGpsLocation() {
        scope.launch {
            val resolved = resolveDeviceGpsLocation(context, currentLocationName)
            onLocationUpdated(resolved.displayName)
            gpsStatusMessage =
                "GPS Synced: ${resolved.displayName} (Lat ${String.format(Locale.US, "%.4f", resolved.latitude)}, Lon ${String.format(Locale.US, "%.4f", resolved.longitude)})"
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        isGranted = granted
        if (granted) {
            refreshGpsLocation()
        } else {
            gpsStatusMessage =
                "Location permission was not granted. You can allow it anytime here or in Phone Location Settings."
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("location_permission_control_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (isGranted) CyberEmerald else HoloCyan
        )
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
                        imageVector = if (isGranted) Icons.Default.GpsFixed else Icons.Default.LocationOn,
                        contentDescription = "Platform Location Permission",
                        tint = if (isGranted) CyberEmerald else HoloCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Platform Location Permission & Live GPS Weather",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isGranted) {
                                "Status: ALLOWED • Active City: $currentLocationName"
                            } else {
                                "Status: NOT YET ALLOWED • Tap switch or button to allow location"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isGranted) CyberEmerald else SolarAmber,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Switch(
                    checked = isGranted,
                    onCheckedChange = { wantEnable ->
                        if (wantEnable) {
                            if (hasLocationPermissionGranted(context)) {
                                isGranted = true
                                refreshGpsLocation()
                            } else {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        } else {
                            launchPhoneSystemSetting(context, Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        }
                    },
                    modifier = Modifier.testTag("location_permission_switch")
                )
            }

            Text(
                text = gpsStatusMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (hasLocationPermissionGranted(context)) {
                            isGranted = true
                            refreshGpsLocation()
                        } else {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isGranted) CyberEmerald else HoloCyan,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("allow_platform_location_permission_button")
                ) {
                    Icon(
                        imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.MyLocation,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isGranted) "Sync Live GPS Location" else "Allow Location Permission",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                OutlinedButton(
                    onClick = {
                        launchPhoneSystemSetting(context, Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.2.dp, SolarAmber),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = SolarAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Phone GPS Settings",
                        style = MaterialTheme.typography.labelMedium,
                        color = SolarAmber,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardTopWeatherSearchHeader(
    locationName: String,
    universalSearchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedSearchEngine: String,
    onSelectSearchEngine: (String) -> Unit,
    onExecuteSearch: (String?) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onOpenPhoneSystemSettings: () -> Unit,
    onSignOut: () -> Unit,
    onNavigateToFriendsDiscovery: () -> Unit = {},
    onOpenInstallPublishHub: () -> Unit = {},
    onLocationUpdated: (String) -> Unit = {},
    discoveredUsers: List<UserProfile> = emptyList(),
    communityConnections: List<CommunityConnectionCard> = emptyList(),
    onSearchCommunityMembers: (String) -> Unit = {},
    onSendFriendRequest: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showSearchBar by rememberSaveable { mutableStateOf(false) }
    var showSearchFilters by remember { mutableStateOf(false) }
    var searchMode by remember { mutableStateOf("COMMUNITY_MEMBERS") }
    var friendRequestSentTo by remember { mutableStateOf<String?>(null) }
    var isLocationGranted by remember { mutableStateOf(hasLocationPermissionGranted(context)) }

    var currentTimeText by remember {
        mutableStateOf(SimpleDateFormat("hh:mm a", Locale.US).format(Date()))
    }
    var currentDateText by remember {
        mutableStateOf(SimpleDateFormat("EEE, MMM d", Locale.US).format(Date()))
    }

    LaunchedEffect(Unit) {
        val timeFmt = SimpleDateFormat("hh:mm a", Locale.US)
        val dateFmt = SimpleDateFormat("EEE, MMM d", Locale.US)
        while (true) {
            val now = Date()
            currentTimeText = timeFmt.format(now)
            currentDateText = dateFmt.format(now)
            isLocationGranted = hasLocationPermissionGranted(context)
            delay(1000L)
        }
    }

    val matchedCity = remember(locationName) {
        SUPPORTED_CITIES.firstOrNull {
            locationName.contains(it.name.substringBefore(","), ignoreCase = true)
        } ?: SUPPORTED_CITIES.first()
    }

    var activeLat by remember(matchedCity) { mutableDoubleStateOf(matchedCity.lat) }
    var activeLon by remember(matchedCity) { mutableDoubleStateOf(matchedCity.lon) }
    var tempFahrenheit by remember(matchedCity) { mutableDoubleStateOf(matchedCity.defaultTempF) }
    var windMph by remember { mutableDoubleStateOf(6.8) }
    var weatherCondition by remember(matchedCity) { mutableStateOf(matchedCity.defaultCondition) }
    var useCelsius by remember { mutableStateOf(false) }
    var weatherRefreshTick by remember { mutableIntStateOf(0) }

    val headerLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        isLocationGranted = granted
        if (granted) {
            scope.launch {
                val resolved = resolveDeviceGpsLocation(context, locationName)
                activeLat = resolved.latitude
                activeLon = resolved.longitude
                onLocationUpdated(resolved.displayName)
                weatherRefreshTick += 1
            }
        }
    }

    LaunchedEffect(activeLat, activeLon, weatherRefreshTick) {
        withContext(Dispatchers.IO) {
            try {
                val urlStr =
                    "https://api.open-meteo.com/v1/forecast?latitude=$activeLat&longitude=$activeLon&current_weather=true&temperature_unit=fahrenheit&windspeed_unit=mph"
                val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 3500
                    readTimeout = 3500
                }
                if (conn.responseCode == 200) {
                    val body = conn.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(body)
                    val current = json.optJSONObject("current_weather")
                    if (current != null) {
                        val temp = current.optDouble("temperature", matchedCity.defaultTempF)
                        val wind = current.optDouble("windspeed", 6.8)
                        val code = current.optInt("weathercode", 0)
                        tempFahrenheit = temp
                        windMph = wind
                        weatherCondition = when (code) {
                            0 -> "Clear Sky"
                            1, 2, 3 -> "Partly Cloudy"
                            45, 48 -> "Misty Fog"
                            51, 53, 55, 61, 63, 65 -> "Rain"
                            71, 73, 75 -> "Snow"
                            95, 96, 99 -> "Storm"
                            else -> "Breezy"
                        }
                    }
                }
                conn.disconnect()
            } catch (_: Exception) {
            }
        }
    }

    val displayedTemp = if (useCelsius) {
        String.format(Locale.US, "%.0f°C", (tempFahrenheit - 32.0) * 5.0 / 9.0)
    } else {
        String.format(Locale.US, "%.0f°F", tempFahrenheit)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .sixDimensionalCardEffect(tiltFactor = 1.0f)
            .testTag("dashboard_top_header_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.2.dp,
            brush = Brush.horizontalGradient(
                listOf(HoloCyan.copy(alpha = 0.7f), MaterialTheme.colorScheme.primary, NeonPink.copy(alpha = 0.65f))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Compact Single-Line 6D Telemetry Header: Identity + Time/Weather Pill + Location Permission + Action Icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_time_date_weather_bar"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 3D / Multi-Dimensional WEREALLEQUALS Icon + Brand + Live Time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dashboard_time_date_display")
                ) {
                    WereAllEquals3DMultiDimensionalIcon(
                        iconSize = 44.dp,
                        showRibbonText = true,
                        onClick = onNavigateToEditProfile
                    )
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "STAGES OF LIFE",
                                style = MaterialTheme.typography.labelLarge,
                                color = HoloCyan,
                                fontWeight = FontWeight.Black,
                                maxLines = 1
                            )
                            Text(
                                text = "• CREATE YOUR LIFE",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = HoloCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "$currentTimeText • $currentDateText",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Live Weather + GPS Permission Pill
                Surface(
                    color = SolarAmber.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.55f)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .clickable { useCelsius = !useCelsius }
                        .testTag("dashboard_weather_widget")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (weatherCondition.contains("Cloud", ignoreCase = true)) {
                                Icons.Default.Cloud
                            } else {
                                Icons.Default.WbSunny
                            },
                            contentDescription = "Weather",
                            tint = SolarAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "$displayedTemp ${locationName.substringBefore(",").ifBlank { "Austin" }}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (isLocationGranted) Icons.Default.GpsFixed else Icons.Default.MyLocation,
                            contentDescription = "Allow Location Permission",
                            tint = if (isLocationGranted) CyberEmerald else HoloCyan,
                            modifier = Modifier
                                .size(14.dp)
                                .clickable {
                                    if (hasLocationPermissionGranted(context)) {
                                        scope.launch {
                                            val resolved = resolveDeviceGpsLocation(context, locationName)
                                            activeLat = resolved.latitude
                                            activeLon = resolved.longitude
                                            onLocationUpdated(resolved.displayName)
                                            weatherRefreshTick += 1
                                        }
                                    } else {
                                        headerLocationPermissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    }
                                }
                        )
                    }
                }

                // Compact Action Icons (Search toggle, Install/Publish, Settings, Edit, Sign Out)
                Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                    IconButton(
                        onClick = { showSearchBar = !showSearchBar },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("dashboard_toggle_search_button")
                    ) {
                        Icon(
                            imageVector = if (showSearchBar || universalSearchQuery.isNotBlank()) {
                                Icons.Default.Close
                            } else {
                                Icons.Default.Search
                            },
                            contentDescription = "Toggle Search",
                            tint = if (showSearchBar) NeonPink else HoloCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onOpenInstallPublishHub,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("dashboard_install_publish_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Install, Download & Publish",
                            tint = CyberEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onOpenPhoneSystemSettings,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("dashboard_phone_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Phone System Settings",
                            tint = SolarAmber,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    IconButton(
                        onClick = onNavigateToEditProfile,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("dashboard_edit_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = HoloCyan,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    IconButton(
                        onClick = onSignOut,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("dashboard_sign_out_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = stringResource(R.string.sign_out),
                            tint = RosePulse,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            // Collapsible Search Bar (hidden by default so page content appears higher up)
            AnimatedVisibility(visible = showSearchBar || universalSearchQuery.isNotBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            val matchedLocalMembers = remember(universalSearchQuery, communityConnections) {
                val q = universalSearchQuery.trim()
                if (q.isEmpty()) {
                    emptyList()
                } else {
                    communityConnections.filter {
                        it.name.contains(q, ignoreCase = true) ||
                            it.role.contains(q, ignoreCase = true) ||
                            it.location.contains(q, ignoreCase = true)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedTextField(
                    value = universalSearchQuery,
                    onValueChange = { newText ->
                        onSearchQueryChange(newText)
                        if (newText.trim().length >= 2) {
                            onSearchCommunityMembers(newText.trim())
                        }
                    },
                    placeholder = {
                        Text(
                            text = if (searchMode == "COMMUNITY_MEMBERS") {
                                "Find community members by name or email (Firestore indexed)…"
                            } else {
                                "Search $selectedSearchEngine, resources, sports or AI…"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (searchMode == "COMMUNITY_MEMBERS") Icons.Default.GroupAdd else Icons.Default.Search,
                            contentDescription = null,
                            tint = if (searchMode == "COMMUNITY_MEMBERS") CyberEmerald else HoloCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            IconButton(
                                onClick = { showSearchFilters = !showSearchFilters },
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("top_search_mode_options_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Search Mode & Engine Options",
                                    tint = if (showSearchFilters) NeonPink else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Button(
                                onClick = {
                                    if (searchMode == "COMMUNITY_MEMBERS") {
                                        onSearchCommunityMembers(universalSearchQuery.trim())
                                    } else {
                                        onExecuteSearch(null)
                                    }
                                },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (searchMode == "COMMUNITY_MEMBERS") CyberEmerald else MaterialTheme.colorScheme.primary,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("universal_search_go_button")
                            ) {
                                Text(
                                    text = if (searchMode == "COMMUNITY_MEMBERS") "Members" else selectedSearchEngine,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (searchMode == "COMMUNITY_MEMBERS") {
                                onSearchCommunityMembers(universalSearchQuery.trim())
                            } else {
                                onExecuteSearch(null)
                            }
                        }
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("universal_search_input")
                )
            }

            // Quick Mode Toggle Row: Community Members (Firestore Indexed) vs Web Search
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = searchMode == "COMMUNITY_MEMBERS",
                    onClick = { searchMode = "COMMUNITY_MEMBERS" },
                    label = {
                        Text(
                            text = "👥 Find Members (Firestore Index)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (searchMode == "COMMUNITY_MEMBERS") FontWeight.ExtraBold else FontWeight.Medium
                        )
                    },
                    modifier = Modifier.testTag("top_search_mode_members_chip")
                )
                FilterChip(
                    selected = searchMode == "WEB_SEARCH",
                    onClick = { searchMode = "WEB_SEARCH" },
                    label = {
                        Text(
                            text = "🌐 Web ($selectedSearchEngine)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (searchMode == "WEB_SEARCH") FontWeight.ExtraBold else FontWeight.Medium
                        )
                    },
                    modifier = Modifier.testTag("top_search_mode_web_chip")
                )
                Surface(
                    color = HoloCyan.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .clickable { onNavigateToFriendsDiscovery() }
                        .testTag("top_search_open_friends_directory_chip")
                ) {
                    Text(
                        text = "Directory",
                        style = MaterialTheme.typography.labelSmall,
                        color = HoloCyan,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // Live Firestore-Indexed Community Member Lookup Results Panel
            AnimatedVisibility(
                visible = searchMode == "COMMUNITY_MEMBERS" &&
                    (universalSearchQuery.isNotBlank() || discoveredUsers.isNotEmpty())
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("top_search_member_results_panel")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚡ Firestore Indexed Member Lookup (${discoveredUsers.size + matchedLocalMembers.size} matches)",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberEmerald,
                                fontWeight = FontWeight.ExtraBold
                            )
                            if (friendRequestSentTo != null) {
                                Text(
                                    text = "✓ Request sent to $friendRequestSentTo",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HoloCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Firestore-indexed UserProfile results
                        discoveredUsers.take(4).forEach { member ->
                            val accent = parseThemeHex(member.themeColorHex)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                                    .testTag("top_search_firestore_member_${member.userId}"),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(accent.copy(alpha = 0.25f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = member.displayName.take(2).uppercase(),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = accent,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = member.displayName.ifBlank { "Community Member" },
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${member.role.uppercase()} • ${member.location.ifBlank { member.email.ifBlank { "Stages Of Life Hub" } }}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                Button(
                                    onClick = {
                                        onSendFriendRequest(member.userId, member.displayName)
                                        friendRequestSentTo = member.displayName
                                    },
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CyberEmerald,
                                        contentColor = Color.Black
                                    ),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(
                                        text = "Add Friend",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }

                        // Featured Community Connections matches
                        matchedLocalMembers.take(3).forEach { conn ->
                            val accent = parseThemeHex(conn.themeHex)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                                    .testTag("top_search_community_member_${conn.id}"),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(accent.copy(alpha = 0.25f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = conn.name.take(2).uppercase(),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = accent,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = conn.name,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${conn.role} • ${conn.location}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                Button(
                                    onClick = {
                                        onSendFriendRequest(conn.id, conn.name)
                                        friendRequestSentTo = conn.name
                                    },
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = HoloCyan,
                                        contentColor = Color.Black
                                    ),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(
                                        text = "Add Friend",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }

                        if (discoveredUsers.isEmpty() && matchedLocalMembers.isEmpty()) {
                            Text(
                                text = "No exact prefix match for '$universalSearchQuery' yet — tap 'Members' to query Firestore index or open Directory.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Optional Expandable Search Engine & Topic Shortcuts (Hidden by default to prevent clutter)
            AnimatedVisibility(visible = showSearchFilters) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Google", "DuckDuckGo", "Bing").forEach { engine ->
                            FilterChip(
                                selected = selectedSearchEngine == engine,
                                onClick = {
                                    searchMode = "WEB_SEARCH"
                                    onSelectSearchEngine(engine)
                                },
                                label = { Text(engine, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.testTag("search_engine_chip_${engine.lowercase(Locale.ROOT)}")
                            )
                        }
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Maya Lin",
                            "Marcus Sterling",
                            "Elena Rostova",
                            "FAFSA Grants",
                            "VA Clinics Near Me"
                        ).forEach { topic ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, HoloCyan.copy(alpha = 0.4f)),
                                modifier = Modifier.clickable {
                                    onSearchQueryChange(topic)
                                    if (searchMode == "COMMUNITY_MEMBERS") {
                                        onSearchCommunityMembers(topic)
                                    } else {
                                        onExecuteSearch(topic)
                                    }
                                }
                            ) {
                                Text(
                                    text = "🔍 $topic",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HoloCyan,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
            }
            }
        }
    }
}
