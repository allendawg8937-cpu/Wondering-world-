package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CarRental
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.components.SixDimensionalHdBadge
import com.example.ui.components.sixDimensionalCardEffect
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Locale

enum class PlatformTypeCategory {
    ALL,
    RIDE_DRIVING,
    FOOD_DELIVERY,
    GROCERY_ESSENTIALS,
    EARN_AS_DRIVER
}

data class TransitPlatformConfig(
    val id: String,
    val name: String,
    val brandBadge: String,
    val category: PlatformTypeCategory,
    val tagline: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val icon: ImageVector,
    val mainActionLabel: String,
    val mainUrl: String,
    val driverEarnLabel: String,
    val driverEarnUrl: String,
    val searchUrlTemplate: String,
    val specialPromoBadge: String,
    val specialPromoUrl: String,
    val popularTags: List<String>
)

data class RecentTransitTrip(
    val id: String,
    val serviceName: String,
    val tripDescription: String,
    val costDollars: Double,
    val category: String,
    val timestampText: String,
    val badgeColor: Color
)

val TRANSIT_AND_DELIVERY_PLATFORMS = listOf(
    // 1. Uber (Rides & Food Delivery & Driver)
    TransitPlatformConfig(
        id = "uber",
        name = "Uber (Rides & Eats)",
        brandBadge = "🚗 RIDESHARE & MEALS",
        category = PlatformTypeCategory.RIDE_DRIVING,
        tagline = "Request Rides, Airport Trips & Restaurant Meals",
        description = "On-demand personal rides, UberX, Uber XL, Comfort, Green EV, and late-night restaurant meal delivery in 10,000+ cities worldwide.",
        primaryColor = Color(0xFF000000),
        secondaryColor = Color(0xFF06C167),
        icon = Icons.Default.DirectionsCar,
        mainActionLabel = "Book an Uber Ride",
        mainUrl = "https://m.uber.com/",
        driverEarnLabel = "Drive with Uber (Earn $)",
        driverEarnUrl = "https://www.uber.com/drive/",
        searchUrlTemplate = "https://www.ubereats.com/search?q=%s",
        specialPromoBadge = "🎓 Uber One Student ($4.99/mo) & Ride Discounts",
        specialPromoUrl = "https://www.ubereats.com/uber-one",
        popularTags = listOf("UberX Ride", "Reserve Ride", "Airport Trip", "Uber Eats Food", "Comfort")
    ),

    // 2. Lyft (Rideshare & Driver)
    TransitPlatformConfig(
        id = "lyft",
        name = "Lyft Rideshare",
        brandBadge = "🚗 RIDESHARE & TRANSIT",
        category = PlatformTypeCategory.RIDE_DRIVING,
        tagline = "Reliable On-Demand Rides & Driver Flexibility",
        description = "Get reliable rides in minutes with upfront pricing, scheduled pickups, Lyft Pink perks, and flexible driving opportunities nationwide.",
        primaryColor = Color(0xFFFF00BF),
        secondaryColor = Color(0xFF333333),
        icon = Icons.Default.CarRental,
        mainActionLabel = "Request a Lyft Ride",
        mainUrl = "https://ride.lyft.com/",
        driverEarnLabel = "Drive with Lyft (Keep 100% Tips)",
        driverEarnUrl = "https://www.lyft.com/driver",
        searchUrlTemplate = "https://ride.lyft.com/",
        specialPromoBadge = "🌟 Lyft Pink Perks & Student Ride Credits",
        specialPromoUrl = "https://www.lyft.com/pink",
        popularTags = listOf("Standard Ride", "Lyft XL", "Wait & Save", "Schedule Ride", "Bikes & Scooters")
    ),

    // 3. Waymo One (Autonomous Driverless Driving)
    TransitPlatformConfig(
        id = "waymo",
        name = "Waymo One (Autonomous Driving)",
        brandBadge = "⚡ 100% AUTONOMOUS EV",
        category = PlatformTypeCategory.RIDE_DRIVING,
        tagline = "World's First Fully Autonomous Rideshare Service",
        description = "Experience quiet, safe, all-electric autonomous rides with no human driver required in Austin, SF, Phoenix, and expanding cities.",
        primaryColor = Color(0xFF0052CC),
        secondaryColor = Color(0xFF00D2B4),
        icon = Icons.Default.ElectricCar,
        mainActionLabel = "Ride Autonomous Waymo",
        mainUrl = "https://waymo.com/waymo-one/",
        driverEarnLabel = "Waymo Safety & Tech Info",
        driverEarnUrl = "https://waymo.com/safety/",
        searchUrlTemplate = "https://waymo.com/waymo-one/",
        specialPromoBadge = "🤖 Zero Human Driver • Pure Clean EV Mobility",
        specialPromoUrl = "https://waymo.com/waymo-one/",
        popularTags = listOf("Fully Autonomous", "All-Electric EV", "Quiet Cabin", "Austin / Phoenix / SF", "24/7 Service")
    ),

    // 4. DoorDash (Food, Convenience & Dasher Driver)
    TransitPlatformConfig(
        id = "doordash",
        name = "DoorDash",
        brandBadge = "🍔 FOOD, GROCERIES & DASHING",
        category = PlatformTypeCategory.FOOD_DELIVERY,
        tagline = "Food Delivery, Groceries & Dasher Earnings",
        description = "Order from top local restaurants, convenience stores, and supermarkets, or earn flexible income delivering on your own schedule as a Dasher.",
        primaryColor = Color(0xFFFF3008),
        secondaryColor = Color(0xFFFF6548),
        icon = Icons.Default.Fastfood,
        mainActionLabel = "Order Food on DoorDash",
        mainUrl = "https://www.doordash.com/",
        driverEarnLabel = "Become a Dasher (Earn $)",
        driverEarnUrl = "https://www.doordash.com/dasher/signup/",
        searchUrlTemplate = "https://www.doordash.com/search/store/?query=%s",
        specialPromoBadge = "🎓 DashPass Student ($4.99/mo) Free Delivery",
        specialPromoUrl = "https://www.doordash.com/dashpass/student/",
        popularTags = listOf("Restaurants", "DashPass", "Late Night Pizza", "Convenience", "Dasher Driver")
    ),

    // 5. Grubhub (Campus Dining, Restaurants & Driver)
    TransitPlatformConfig(
        id = "grubhub",
        name = "Grubhub",
        brandBadge = "🥡 RESTAURANTS & CAMPUS DINING",
        category = PlatformTypeCategory.FOOD_DELIVERY,
        tagline = "Local Food Perks, Campus Dining & Delivery",
        description = "Local restaurant takeout, student campus meal delivery, exclusive deals, and flexible driver delivery shifts with weekly direct deposits.",
        primaryColor = Color(0xFFF63440),
        secondaryColor = Color(0xFFFF525E),
        icon = Icons.Default.Restaurant,
        mainActionLabel = "Order Meals on Grubhub",
        mainUrl = "https://www.grubhub.com/",
        driverEarnLabel = "Drive for Grubhub (Earn $)",
        driverEarnUrl = "https://driver.grubhub.com/",
        searchUrlTemplate = "https://www.grubhub.com/search?queryText=%s",
        specialPromoBadge = "🎓 Free Grubhub+ with Amazon Prime Student",
        specialPromoUrl = "https://www.grubhub.com/plus",
        popularTags = listOf("Campus Dining", "Free Delivery", "Local Deals", "Pickup", "Grubhub Driver")
    ),

    // 6. Instacart (Groceries, Supermarkets & Shopper Driver)
    TransitPlatformConfig(
        id = "instacart",
        name = "Instacart",
        brandBadge = "🛒 GROCERIES & SHOPPER DRIVER",
        category = PlatformTypeCategory.GROCERY_ESSENTIALS,
        tagline = "Same-Day Groceries, Supermarkets & Full-Service Shopper",
        description = "Shop Costco, Aldi, Kroger, and pharmacy essentials in 1 hour, or sign up as an Instacart Full-Service Shopper driver to earn money shopping & delivering.",
        primaryColor = Color(0xFF003D29),
        secondaryColor = Color(0xFFFF8200),
        icon = Icons.Default.ShoppingCart,
        mainActionLabel = "Order Groceries on Instacart",
        mainUrl = "https://www.instacart.com/",
        driverEarnLabel = "Become an Instacart Shopper Driver",
        driverEarnUrl = "https://shoppers.instacart.com/",
        searchUrlTemplate = "https://www.instacart.com/store/s?k=%s",
        specialPromoBadge = "🛒 Instacart+ Free Delivery & Same-Day Specials",
        specialPromoUrl = "https://www.instacart.com/instacart-plus",
        popularTags = listOf("Fresh Produce", "Costco Bulk", "Aldi Savings", "Supermarkets", "Shopper Driver")
    )
)

private fun launchTransitUrl(context: Context, targetUrl: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeliveryAndOrderingPortalScreen(
    profile: UserProfile,
    onAddDeliveryExpenseToBudget: (title: String, category: String, amountDollars: Double) -> Unit = { _, _, _ -> },
    onLogDeliveryInteraction: (actionType: String, detail: String, category: String) -> Unit = { _, _, _ -> },
    onShareMealOrderToWall: (headline: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var selectedCategoryFilter by remember { mutableStateOf(PlatformTypeCategory.ALL) }
    var universalSearchQuery by remember { mutableStateOf("") }
    var statusFeedback by remember { mutableStateOf<String?>(null) }

    // Quick Expense / Trip Logging Form States
    var logItemDescription by remember { mutableStateOf("") }
    var logPriceText by remember { mutableStateOf("22.50") }
    var selectedServiceForLog by remember { mutableStateOf("Uber") }
    var logCategoryType by remember { mutableStateOf("Ride") }

    val recentTripsAndOrders = remember {
        mutableStateListOf(
            RecentTransitTrip(
                id = "trip_1",
                serviceName = "Uber",
                tripDescription = "UberX Ride to Downtown / Campus",
                costDollars = 18.25,
                category = "Ride",
                timestampText = "Today • 11:20 AM",
                badgeColor = Color(0xFF06C167)
            ),
            RecentTransitTrip(
                id = "trip_2",
                serviceName = "DoorDash",
                tripDescription = "Chipotle Burrito Bowl & Drink",
                costDollars = 17.50,
                category = "Food",
                timestampText = "Yesterday • 7:45 PM",
                badgeColor = Color(0xFFFF3008)
            ),
            RecentTransitTrip(
                id = "trip_3",
                serviceName = "Lyft",
                tripDescription = "Lyft Ride from Work to Home",
                costDollars = 15.80,
                category = "Ride",
                timestampText = "Nov 7 • 5:10 PM",
                badgeColor = Color(0xFFFF00BF)
            ),
            RecentTransitTrip(
                id = "trip_4",
                serviceName = "Instacart",
                tripDescription = "Supermarket Groceries & Fresh Fruit",
                costDollars = 42.60,
                category = "Groceries",
                timestampText = "Nov 6 • 3:30 PM",
                badgeColor = Color(0xFFFF8200)
            )
        )
    }

    val filteredPlatforms = remember(selectedCategoryFilter) {
        when (selectedCategoryFilter) {
            PlatformTypeCategory.ALL -> TRANSIT_AND_DELIVERY_PLATFORMS
            PlatformTypeCategory.RIDE_DRIVING -> TRANSIT_AND_DELIVERY_PLATFORMS.filter {
                it.category == PlatformTypeCategory.RIDE_DRIVING
            }
            PlatformTypeCategory.FOOD_DELIVERY -> TRANSIT_AND_DELIVERY_PLATFORMS.filter {
                it.category == PlatformTypeCategory.FOOD_DELIVERY
            }
            PlatformTypeCategory.GROCERY_ESSENTIALS -> TRANSIT_AND_DELIVERY_PLATFORMS.filter {
                it.category == PlatformTypeCategory.GROCERY_ESSENTIALS
            }
            PlatformTypeCategory.EARN_AS_DRIVER -> TRANSIT_AND_DELIVERY_PLATFORMS
        }
    }

    val quickActionShortcuts = listOf(
        "🚗 Book Uber Ride" to "https://m.uber.com/",
        "🚙 Book Lyft Ride" to "https://ride.lyft.com/",
        "⚡ Autonomous Waymo" to "https://waymo.com/waymo-one/",
        "🍔 Order DoorDash" to "https://www.doordash.com/",
        "🥡 Order Grubhub" to "https://www.grubhub.com/",
        "🛒 Order Instacart" to "https://www.instacart.com/",
        "💼 Drive for Uber" to "https://www.uber.com/drive/",
        "🚘 Drive for Lyft" to "https://www.lyft.com/driver",
        "📦 Drive for DoorDash" to "https://www.doordash.com/dasher/signup/"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("delivery_and_driving_platforms_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero 6D Header Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
            ),
            border = BorderStroke(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        Color(0xFFFF3008), // DoorDash Red
                        Color(0xFF06C167), // Uber Green
                        Color(0xFFFF00BF), // Lyft Pink
                        Color(0xFFF63440), // Grubhub Red
                        Color(0xFFFF8200), // Instacart Carrot
                        HoloCyan
                    )
                )
            ),
            modifier = Modifier
                .fillMaxWidth()
                .sixDimensionalCardEffect(tiltFactor = 1.15f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF06C167), Color(0xFFFF00BF), Color(0xFFFF3008), Color(0xFFFF8200))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = "Rideshare, Delivery & Driving Hub",
                                tint = Color.Black,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Rideshare, Delivery & Driving Hub",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                SixDimensionalHdBadge(label = "TRANSIT & FOOD")
                            }
                            Text(
                                text = "Uber • Lyft • Waymo • DoorDash • Grubhub • Instacart • Drive & Earn",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Universal Ride & Meal Search Bar
                OutlinedTextField(
                    value = universalSearchQuery,
                    onValueChange = { universalSearchQuery = it },
                    label = { Text("Search Destination, Rides, Groceries, or Food...") },
                    placeholder = { Text("e.g. Airport, Downtown, Pizza, Chipotle, Groceries") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = HoloCyan
                        )
                    },
                    trailingIcon = {
                        if (universalSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { universalSearchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transit_universal_search_input")
                )

                // 1-Tap Quick Ride & Order Launcher Row
                if (universalSearchQuery.isNotBlank()) {
                    val encoded = URLEncoder.encode(universalSearchQuery.trim(), StandardCharsets.UTF_8.toString())
                    Text(
                        text = "1-Tap Launch for \"${universalSearchQuery.trim()}\":",
                        style = MaterialTheme.typography.labelSmall,
                        color = HoloCyan,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                launchTransitUrl(context, "https://m.uber.com/looking")
                                onLogDeliveryInteraction("BookRide", "Uber: $universalSearchQuery", "Transit")
                                statusFeedback = "✓ Launched Uber for destination: $universalSearchQuery"
                            },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF06C167),
                                contentColor = Color.Black
                            )
                        ) {
                            Text("🚗 Book Uber", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                launchTransitUrl(context, "https://ride.lyft.com/")
                                onLogDeliveryInteraction("BookRide", "Lyft: $universalSearchQuery", "Transit")
                                statusFeedback = "✓ Launched Lyft for destination: $universalSearchQuery"
                            },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF00BF),
                                contentColor = Color.White
                            )
                        ) {
                            Text("🚙 Book Lyft", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val url = String.format(Locale.US, TRANSIT_AND_DELIVERY_PLATFORMS[3].searchUrlTemplate, encoded)
                                launchTransitUrl(context, url)
                                onLogDeliveryInteraction("OrderFood", "DoorDash: $universalSearchQuery", "Delivery")
                                statusFeedback = "✓ Launched DoorDash for: $universalSearchQuery"
                            },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF3008),
                                contentColor = Color.White
                            )
                        ) {
                            Text("🍔 DoorDash", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val url = String.format(Locale.US, TRANSIT_AND_DELIVERY_PLATFORMS[5].searchUrlTemplate, encoded)
                                launchTransitUrl(context, url)
                                onLogDeliveryInteraction("OrderGroceries", "Instacart: $universalSearchQuery", "Groceries")
                                statusFeedback = "✓ Launched Instacart for: $universalSearchQuery"
                            },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF8200),
                                contentColor = Color.Black
                            )
                        ) {
                            Text("🛒 Instacart", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                // Quick One-Tap Shortcut Chips (Rides & Food & Driver Signups)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickActionShortcuts.forEach { (label, url) ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                            modifier = Modifier.clickable {
                                launchTransitUrl(context, url)
                                onLogDeliveryInteraction("QuickShortcut", label, "Transit")
                                statusFeedback = "✓ Opened $label portal"
                            }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                if (!statusFeedback.isNullOrBlank()) {
                    Surface(
                        color = CyberEmerald.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CyberEmerald,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = statusFeedback.orEmpty(),
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2. Category Filter Chips (All, Rideshare & Driving, Food Delivery, Grocery & Essentials, Earn as Driver)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                PlatformTypeCategory.ALL to "🌐 All Platforms (${TRANSIT_AND_DELIVERY_PLATFORMS.size})",
                PlatformTypeCategory.RIDE_DRIVING to "🚗 Rideshare & Driving (Uber, Lyft, Waymo)",
                PlatformTypeCategory.FOOD_DELIVERY to "🍔 Food & Meals (DoorDash, Grubhub, Uber Eats)",
                PlatformTypeCategory.GROCERY_ESSENTIALS to "🛒 Supermarkets (Instacart)",
                PlatformTypeCategory.EARN_AS_DRIVER to "💼 Drive & Earn Income ($)"
            ).forEach { (categoryKey, label) ->
                FilterChip(
                    selected = selectedCategoryFilter == categoryKey,
                    onClick = { selectedCategoryFilter = categoryKey },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selectedCategoryFilter == categoryKey) FontWeight.ExtraBold else FontWeight.Medium
                        )
                    },
                    modifier = Modifier.testTag("filter_transit_${categoryKey.name.lowercase()}")
                )
            }
        }

        // 3. Platform Cards Grid (Uber, Lyft, Waymo, DoorDash, Grubhub, Instacart)
        filteredPlatforms.forEach { platform ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, platform.primaryColor.copy(alpha = 0.7f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transit_card_${platform.id}")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(platform.primaryColor.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = platform.icon,
                                    contentDescription = platform.name,
                                    tint = platform.primaryColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = platform.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = platform.tagline,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = platform.primaryColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            color = platform.primaryColor.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = platform.brandBadge,
                                style = MaterialTheme.typography.labelSmall,
                                color = platform.primaryColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Text(
                        text = platform.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Tags
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        platform.popularTags.forEach { tag ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, platform.primaryColor.copy(alpha = 0.35f)),
                                modifier = Modifier.clickable {
                                    launchTransitUrl(context, platform.mainUrl)
                                }
                            ) {
                                Text(
                                    text = "+ $tag",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Special Perks / Promo Banner
                    Surface(
                        color = CyberEmerald.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                launchTransitUrl(context, platform.specialPromoUrl)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalOffer,
                                    contentDescription = null,
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = platform.specialPromoBadge,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyberEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "View Perk >",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberEmerald,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    // Dual Actions: Customer Order / Ride + Driver & Courier Sign-Up
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                launchTransitUrl(context, platform.mainUrl)
                                onLogDeliveryInteraction("BookOrOrder", "Launched ${platform.name}", "Transit")
                                statusFeedback = "✓ Opened ${platform.name} Portal"
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = platform.primaryColor,
                                contentColor = if (platform.primaryColor == Color(0xFFFF8200) || platform.primaryColor == Color(0xFF06C167)) Color.Black else Color.White
                            ),
                            modifier = Modifier
                                .weight(1.1f)
                                .height(46.dp)
                                .testTag("open_main_${platform.id}_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = platform.mainActionLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                launchTransitUrl(context, platform.driverEarnUrl)
                                onLogDeliveryInteraction("DriverEarn", "Drive for ${platform.name}", "Driver")
                                statusFeedback = "✓ Opened ${platform.name} Driver & Earnings Portal"
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.2.dp, platform.primaryColor),
                            modifier = Modifier
                                .weight(0.9f)
                                .height(46.dp)
                                .testTag("driver_earn_${platform.id}_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Work,
                                contentDescription = null,
                                tint = platform.primaryColor,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Drive & Earn",
                                style = MaterialTheme.typography.labelSmall,
                                color = platform.primaryColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 4. Ride & Delivery Expense Logger & Digital Wallet Sync Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
            ),
            border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalMall,
                        contentDescription = null,
                        tint = SolarAmber,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Track Rides & Deliveries in Digital Wallet & Budget",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Log rides and food orders to your WereEqual Digital Wallet (Spend category) to maintain financial literacy and balance tracking.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Service & Type Selection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Uber", "Lyft", "Waymo", "DoorDash", "Grubhub", "Instacart").forEach { plat ->
                        FilterChip(
                            selected = selectedServiceForLog == plat,
                            onClick = { selectedServiceForLog = plat },
                            label = { Text(plat, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = logItemDescription,
                        onValueChange = { logItemDescription = it },
                        label = { Text("Trip or Order Details") },
                        placeholder = { Text("e.g. Ride to Airport or Chipotle delivery") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("transit_log_desc_input")
                    )

                    OutlinedTextField(
                        value = logPriceText,
                        onValueChange = { logPriceText = it },
                        label = { Text("Total ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .width(100.dp)
                            .testTag("transit_log_price_input")
                    )
                }

                Button(
                    onClick = {
                        val cleanDesc = logItemDescription.trim().ifBlank { "$selectedServiceForLog Trip/Order" }
                        val priceDollars = logPriceText.toDoubleOrNull() ?: 22.50
                        val newTrip = RecentTransitTrip(
                            id = "trip_${System.currentTimeMillis()}",
                            serviceName = selectedServiceForLog,
                            tripDescription = cleanDesc,
                            costDollars = priceDollars,
                            category = logCategoryType,
                            timestampText = "Just now",
                            badgeColor = when (selectedServiceForLog) {
                                "Uber" -> Color(0xFF06C167)
                                "Lyft" -> Color(0xFFFF00BF)
                                "Waymo" -> Color(0xFF0052CC)
                                "DoorDash" -> Color(0xFFFF3008)
                                "Grubhub" -> Color(0xFFF63440)
                                else -> Color(0xFFFF8200)
                            }
                        )
                        recentTripsAndOrders.add(0, newTrip)
                        onAddDeliveryExpenseToBudget(
                            "[$selectedServiceForLog] $cleanDesc",
                            "Spend",
                            priceDollars
                        )
                        onLogDeliveryInteraction(
                            "TransitOrder",
                            "$selectedServiceForLog: $cleanDesc ($$priceDollars)",
                            "Transit"
                        )
                        onShareMealOrderToWall(
                            "$selectedServiceForLog: $cleanDesc",
                            "Completed a trip/order on $selectedServiceForLog for $${String.format(Locale.US, "%.2f", priceDollars)} • Synced to WereEqual Wallet!"
                        )
                        logItemDescription = ""
                        statusFeedback = "✓ Saved '$cleanDesc' ($${String.format(Locale.US, "%.2f", priceDollars)}) to Digital Wallet & Wall!"
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberEmerald,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_transit_expense_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Log Trip/Order to Digital Wallet & Wall",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Recent Activity
                if (recentTripsAndOrders.isNotEmpty()) {
                    Text(
                        text = "Recent Rides & Delivery Orders (${recentTripsAndOrders.size}):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    recentTripsAndOrders.take(5).forEach { trip ->
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, trip.badgeColor.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
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
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(trip.badgeColor)
                                    )
                                    Column {
                                        Text(
                                            text = "${trip.serviceName} • ${trip.tripDescription}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = trip.timestampText,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Text(
                                    text = String.format(Locale.US, "$%.2f", trip.costDollars),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CyberEmerald
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
