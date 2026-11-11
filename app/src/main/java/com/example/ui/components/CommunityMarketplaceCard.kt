package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SolarAmber
import java.util.Locale

data class MarketplaceListingItem(
    val id: String,
    val title: String,
    val category: String,
    val sellerName: String,
    val accountBadge: String,
    val priceDollars: Double,
    val description: String,
    val isSponsoredAd: Boolean = false
)

private val INITIAL_MARKETPLACE_LISTINGS = listOf(
    MarketplaceListingItem(
        id = "mkt_1",
        title = "Custom 6D Holographic Profile Theme & Audio Pack",
        category = "Content & 3D Art",
        sellerName = "Maya Lin Studio",
        accountBadge = "🎬 CONTENT ACCOUNT",
        priceDollars = 15.0,
        description = "Includes 4 custom synthwave loops and FHD 1080p avatar frames for creators.",
        isSponsoredAd = true
    ),
    MarketplaceListingItem(
        id = "mkt_2",
        title = "Veteran-Owned Small Business Web & Brand Kit",
        category = "Business Services",
        sellerName = "Sterling Digital Co.",
        accountBadge = "💼 BUSINESS ACCOUNT",
        priceDollars = 45.0,
        description = "Complete startup logo, social banner & local directory setup for new founders.",
        isSponsoredAd = true
    ),
    MarketplaceListingItem(
        id = "mkt_3",
        title = "Graphing Calculator & STEM Robotics Starter Bundle",
        category = "Study & Tech Gear",
        sellerName = "Austin Youth STEM Co-Op",
        accountBadge = "🎓 STUDENT CREATOR",
        priceDollars = 20.0,
        description = "Refurbished scientific calculator + coding flashcards for high school students."
    ),
    MarketplaceListingItem(
        id = "mkt_4",
        title = "Free Community Career Resume & GI Bill Coaching",
        category = "Community Free / Swap",
        sellerName = "Elena Rostova",
        accountBadge = "🛡️ COMMUNITY EQUAL-AID",
        priceDollars = 0.0,
        description = "1-on-1 resume review and FAFSA/GI Bill application guidance — 100% free for all."
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommunityMarketplaceCard(
    userDisplayName: String,
    userRole: String,
    onPayWithDigitalWallet: (title: String, category: String, amountDollars: Double) -> Unit,
    onShareListingToWall: (headline: String, body: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listings = remember {
        mutableStateListOf<MarketplaceListingItem>().apply {
            addAll(INITIAL_MARKETPLACE_LISTINGS)
        }
    }

    var selectedFilterCategory by remember { mutableStateOf("All Market") }
    var listingTitle by remember { mutableStateOf("") }
    var listingPrice by remember { mutableStateOf("25") }
    var listingDescription by remember { mutableStateOf("") }
    var newListingCategory by remember { mutableStateOf("Business Services") }
    var promoteAsBusinessAd by remember { mutableStateOf(true) }
    var marketStatusMessage by remember { mutableStateOf<String?>(null) }

    val filterCategories = listOf(
        "All Market",
        "Business Services",
        "Content & 3D Art",
        "Study & Tech Gear",
        "Community Free / Swap"
    )

    val visibleListings = listings.filter {
        selectedFilterCategory == "All Market" || it.category == selectedFilterCategory
    }

    val currentUserBadge = when (userRole.lowercase()) {
        "business" -> "💼 BUSINESS ACCOUNT"
        "creator" -> "🎬 CONTENT ACCOUNT"
        "veteran" -> "🛡️ VETERAN BUSINESS"
        else -> "🌟 COMMUNITY SELLER"
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(listOf(SolarAmber, NeonPink, HoloCyan))
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("community_marketplace_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
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
                    Surface(
                        color = SolarAmber.copy(alpha = 0.18f),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, SolarAmber)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Wondering World Marketplace",
                            tint = SolarAmber,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "🏪 Wondering World Marketplace & Ads",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Buy, sell, swap & post Business or Content Creator Ads with your Digital Wallet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                SixDimensionalHdBadge(label = "MARKET")
            }

            // Post a Listing / Business Ad Composer
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.55f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                tint = SolarAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Post Marketplace Listing or Business Ad ($currentUserBadge)",
                                style = MaterialTheme.typography.labelLarge,
                                color = SolarAmber,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        filterCategories.drop(1).forEach { cat ->
                            FilterChip(
                                selected = newListingCategory == cat,
                                onClick = { newListingCategory = cat },
                                label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                        FilterChip(
                            selected = promoteAsBusinessAd,
                            onClick = { promoteAsBusinessAd = !promoteAsBusinessAd },
                            label = {
                                Text(
                                    text = if (promoteAsBusinessAd) "📣 Featured Ad Badge ON" else "Standard Listing",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = listingTitle,
                            onValueChange = { listingTitle = it },
                            label = { Text("Item, Service, or Business Ad Title") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(0.66f)
                                .testTag("marketplace_title_input")
                        )
                        OutlinedTextField(
                            value = listingPrice,
                            onValueChange = { listingPrice = it.filter { ch -> ch.isDigit() || ch == '.' }.take(6) },
                            label = { Text("Price $") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(0.34f)
                                .testTag("marketplace_price_input")
                        )
                    }

                    OutlinedTextField(
                        value = listingDescription,
                        onValueChange = { listingDescription = it },
                        label = { Text("Describe your product, content pack, or local service…") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("marketplace_desc_input")
                    )

                    Button(
                        onClick = {
                            val cleanTitle = listingTitle.ifBlank { "Featured $newListingCategory Offer" }
                            val cleanDesc = listingDescription.ifBlank {
                                "Verified community listing by $userDisplayName ($currentUserBadge)."
                            }
                            val parsedPrice = listingPrice.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
                            val created = MarketplaceListingItem(
                                id = "mkt_${System.currentTimeMillis()}",
                                title = cleanTitle,
                                category = newListingCategory,
                                sellerName = userDisplayName.ifBlank { "Community Member" },
                                accountBadge = currentUserBadge,
                                priceDollars = parsedPrice,
                                description = cleanDesc,
                                isSponsoredAd = promoteAsBusinessAd
                            )
                            listings.add(0, created)
                            onShareListingToWall(
                                "🏪 Marketplace Ad: $cleanTitle (${if (parsedPrice == 0.0) "FREE" else "$$parsedPrice"})",
                                "[$currentUserBadge • $newListingCategory] $cleanDesc"
                            )
                            listingTitle = ""
                            listingDescription = ""
                            marketStatusMessage = "Published '$cleanTitle' to the Marketplace & 3D Wall!"
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SolarAmber,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("publish_marketplace_listing_button")
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Publish Listing / Business Ad to Marketplace", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            if (!marketStatusMessage.isNullOrBlank()) {
                Text(
                    text = "✓ $marketStatusMessage",
                    style = MaterialTheme.typography.labelMedium,
                    color = CyberEmerald,
                    fontWeight = FontWeight.Bold
                )
            }

            // Category Filter Strip
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                filterCategories.forEach { category ->
                    FilterChip(
                        selected = selectedFilterCategory == category,
                        onClick = { selectedFilterCategory = category },
                        label = { Text(category, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            // Marketplace Listings Grid / List
            visibleListings.forEach { item ->
                val accent = when {
                    item.isSponsoredAd -> SolarAmber
                    item.priceDollars == 0.0 -> CyberEmerald
                    else -> HoloCyan
                }
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.2.dp, accent.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("marketplace_item_${item.id}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    color = accent.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = if (item.isSponsoredAd) "📣 FEATURED AD • ${item.accountBadge}" else item.accountBadge,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = accent,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Surface(
                                color = CyberEmerald.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, CyberEmerald),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = if (item.priceDollars <= 0.0) {
                                        "FREE AID"
                                    } else {
                                        String.format(Locale.US, "$%,.0f", item.priceDollars)
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = CyberEmerald,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = HoloCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${item.sellerName} • ${item.category}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HoloCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        if (item.priceDollars > 0.0) {
                                            onPayWithDigitalWallet(
                                                "Market: ${item.title}",
                                                "Spend",
                                                item.priceDollars
                                            )
                                            marketStatusMessage = "Paid $${item.priceDollars.toInt()} via Digital Wallet for '${item.title}'!"
                                        } else {
                                            marketStatusMessage = "Claimed free community resource '${item.title}'!"
                                        }
                                    },
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CyberEmerald,
                                        contentColor = Color.Black
                                    ),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(
                                        imageVector = if (item.priceDollars > 0.0) Icons.Default.AccountBalanceWallet else Icons.Default.AddShoppingCart,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (item.priceDollars > 0.0) "Wallet Pay" else "Claim Free",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        onShareListingToWall(
                                            "🏪 Marketplace Pick: ${item.title}",
                                            "${item.description} (By ${item.sellerName})"
                                        )
                                        marketStatusMessage = "Shared '${item.title}' to the 3D Wall!"
                                    },
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, NeonPink),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share Listing",
                                        tint = NeonPink,
                                        modifier = Modifier.size(14.dp)
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
