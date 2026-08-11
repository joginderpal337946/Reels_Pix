package com.example.reels_pix.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopUpBottomSheetContent(
    onDismiss: () -> Unit,
    onViewAllClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
    ) {
        // Header
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "To continue watching, \ntop up your\nbalance or subscribe",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Price & Balance
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Price: 10 ", color = Color.White, fontSize = 14.sp)
            // Mock coin icon
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE5B96E))
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text("Balance: 0", color = Color.White, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Subscriptions
        SubscriptionButton("1 year", "₹10,600.00")
        Spacer(modifier = Modifier.height(8.dp))
        SubscriptionButton("1 month", "₹1,050.00")
        Spacer(modifier = Modifier.height(8.dp))
        SubscriptionButton("1 week", "₹370.00")

        Spacer(modifier = Modifier.height(24.dp))

        // Coins Grid
        val coinPackages = listOf(
            TopUpOption("500", "Coins", "₹110.00", null, null),
            TopUpOption("1000", "Coins", "₹210.00", "+100", "+10%"),
            TopUpOption("2000", "Coins", "₹370.00", "+400", "+20%"),
            TopUpOption("3000", "Coins", "₹520.00", "+900", "+30%"),
            TopUpOption("5000", "Coins", "₹950.00", "+2500", "+50%"),
            TopUpOption("10000", "Coins", "₹2,150.00", "+10000", "+100%")
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.heightIn(max = 300.dp) // bounded height
        ) {
            items(coinPackages.size) { index ->
                CoinPackageItem(option = coinPackages[index])
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // View All
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onViewAllClick() }
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "View all",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "View all",
                tint = Color.White
            )
        }
    }
}

@Composable
fun SubscriptionButton(duration: String, price: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFFD4AF37), Color(0xFFF9E596), Color(0xFFD4AF37))
                )
            )
            .clickable { },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = duration,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0x66000000))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = price,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

data class TopUpOption(
    val amount: String,
    val currency: String,
    val price: String,
    val bonus: String?,
    val bonusTag: String?
)

@Composable
fun CoinPackageItem(option: TopUpOption) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF673AB7), RoundedCornerShape(8.dp))
            .background(Color.Black)
            .clickable { }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = option.amount,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (option.bonus != null) {
                    Text(
                        text = " ${option.bonus}",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }
            Text(
                text = option.currency,
                color = Color.LightGray,
                fontSize = 12.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option.price,
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        if (option.bonusTag != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp))
                    .background(Color(0xFF673AB7))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = option.bonusTag,
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
        }
    }
}
