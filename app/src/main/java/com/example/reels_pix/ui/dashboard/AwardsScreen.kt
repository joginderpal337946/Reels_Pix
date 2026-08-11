package com.example.reels_pix.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reels_pix.R
import com.example.reels_pix.data.model.AwardsMockData
import com.example.reels_pix.data.model.SubscriptionPlan
import com.example.reels_pix.data.model.TopUpPackage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AwardsScreen(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("Rewards", "Shop")
    
    var showTopUpSheet by remember { mutableStateOf<TopUpPackage?>(null) }
    var showSubscriptionSheet by remember { mutableStateOf<SubscriptionPlan?>(null) }
    var showInviteSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
        ) {
            // Top Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFF121212),
                contentColor = Color.White,
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Color(0xFF673AB7),
                            height = 3.dp
                        )
                    }
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { onTabSelected(index) },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTabIndex == index) Color.White else Color.Gray,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Content
            if (selectedTabIndex == 0) {
                RewardsTabContent(
                    onInviteClick = { showInviteSheet = true }
                )
            } else {
                ShopTabContent(
                    onSubscriptionClick = { showSubscriptionSheet = it },
                    onTopUpClick = { showTopUpSheet = it }
                )
            }
        }
    }

    if (showTopUpSheet != null) {
        ModalBottomSheet(
            onDismissRequest = { showTopUpSheet = null },
            containerColor = Color(0xFF1E1E1E),
            dragHandle = null
        ) {
            TopUpBottomSheetContent(pkg = showTopUpSheet!!, onDismiss = { showTopUpSheet = null })
        }
    }
    
    if (showSubscriptionSheet != null) {
        ModalBottomSheet(
            onDismissRequest = { showSubscriptionSheet = null },
            containerColor = Color(0xFF1E1E1E),
            dragHandle = null
        ) {
            SubscriptionBottomSheetContent(sub = showSubscriptionSheet!!, onDismiss = { showSubscriptionSheet = null })
        }
    }
    
    if (showInviteSheet) {
        ModalBottomSheet(
            onDismissRequest = { showInviteSheet = false },
            containerColor = Color(0xFF3B205E), // Deep purple background for invite
            dragHandle = null
        ) {
            InviteBottomSheetContent(onDismiss = { showInviteSheet = false })
        }
    }
}

@Composable
fun RewardsTabContent(onInviteClick: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            // Balance Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Balance", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.reward_gold_coin),
                            contentDescription = "Coin",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("10", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                }
                // Treasure Chest Graphic
                Image(
                    painter = painterResource(id = R.drawable.treasure_chest_coins),
                    contentDescription = "Treasure Chest",
                    modifier = Modifier.size(100.dp)
                )
            }
        }

        item {
            // Daily Entry
            Text(
                "Daily Entry",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(7) { day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(60.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (day == 0) Color(0xFF2C2C2C) else Color(0xFF4E342E)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (day == 0) {
                                // Check mark
                                Text("✓", color = Color.Gray, fontSize = 24.sp)
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        painter = painterResource(id = R.drawable.reward_gold_coin),
                                        contentDescription = "Coin",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("+${if (day < 3) 20 else if (day < 5) 30 else 40}", color = Color.White, fontSize = 14.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            if (day == 0) "Today" else "Day ${day + 1}",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B205E)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp)
            ) {
                Text("Claim Bonuses", color = Color(0xFFB39DDB), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        item {
            // Invite Friend
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1E1E))
                    .clickable { onInviteClick() }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Invite Friend", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFC107))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+100", color = Color(0xFFFFC107), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("coins for each friend", color = Color.Gray)
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Invite",
                        tint = Color.White
                    )
                }
            }
        }

        item {
            // Today's Rewards
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Today's Rewards",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(AwardsMockData.tasks) { task ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1E1E))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(task.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        if (task.description.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(task.description, color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color.Gray, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFC107))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+${task.reward}", color = Color(0xFFFFC107), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            // Rules
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Rules and Information:\n" +
                "1. ReelPix offers both free and paid series. You decide what to watch and which content to unlock.\n" +
                "2. Reward coins can be earned by completing tasks or topping up your balance. They can be used alongside regular coins to unlock episodes.\n" +
                "3. Reward coins do not expire and are spent first when unlocking content.\n" +
                "4. A subscription provides unlimited access to all ReelPix series for the duration of the subscription period.\n" +
                "5. Subscription activation takes up to 24 hours after purchase and depends on confirmation from the Google Play.\n" +
                "6. Your subscription will automatically renew at the same price until the end of the current period unless you cancel it in advance.\n" +
                "7. To cancel your subscription, go to the Google Play app -> Payments & subscriptions -> Subscriptions -> ReelPix -> Cancel subscription, at least 24 hours before the end of the current period.\n" +
                "8. Prices may vary depending on your region.\n" +
                "9. For any questions - contact our support team: support@limexltd.com",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun ShopTabContent(
    onSubscriptionClick: (SubscriptionPlan) -> Unit,
    onTopUpClick: (TopUpPackage) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            // Balance Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Balance", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.reward_gold_coin),
                            contentDescription = "Coin",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("10", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                }
                // VIP Graphic Placeholder
                Text(
                    "VIP",
                    color = Color(0xFFFFC107),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }

        item {
            // Subscriptions
            Text(
                "Subscriptions",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            AwardsMockData.subscriptions.forEach { sub ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .height(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFD4AF37)) // Gold color
                        .clickable { onSubscriptionClick(sub) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(sub.duration, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0x66000000))
                                .padding(horizontal = 16.dp, vertical = 5.dp)
                        ) {
                            Text(sub.price, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            // Top Up Section Header
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Top Up",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Top Up Grid using rows within LazyColumn
        items(AwardsMockData.topUpPackages.chunked(2)) { rowPackages ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                rowPackages.forEach { pkg ->
                    Box(modifier = Modifier.weight(1f).padding(4.dp)) {
                        TopUpCard(pkg = pkg, onClick = { onTopUpClick(pkg) })
                    }
                }
                if (rowPackages.size == 1) {
                    Spacer(modifier = Modifier.weight(1f).padding(4.dp))
                }
            }
        }
    }
}
@Composable
fun TopUpCard(pkg: TopUpPackage, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF673AB7), RoundedCornerShape(8.dp))
            .background(Color.Black)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = pkg.baseCoins.toString(),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (pkg.bonusCoins != 0) {
                    Text(
                        text = " ${pkg.bonusCoins}",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }
            Text(
                text = pkg.price,
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
                    text = pkg.price,
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (pkg.bonusTag != "") {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp))
                    .background(Color(0xFF673AB7))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = pkg.bonusTag,
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun TopUpBottomSheetContent(pkg: TopUpPackage, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(24.dp))
            Text("Coins purchase", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Pack ${pkg.baseCoins} coins ${pkg.bonusTag} bonuses",
            color = Color(0xFFFFC107),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = pkg.price,
            color = Color(0xFFFFC107),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Buy", color = Color.White, fontSize = 18.sp)
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        Text(
            text = "By continuing, you accept our Terms of Use and Privacy Policy",
            color = Color.Gray,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun SubscriptionBottomSheetContent(sub: SubscriptionPlan, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(24.dp))
            Text("Subscription Privileges", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Access by subscription: ${sub.duration}",
            color = Color(0xFFFFC107),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = sub.price,
            color = Color(0xFFFFC107),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        val privileges = listOf(
            "Can watch from beginning to end",
            "Cancel subscription at any time",
            "Unlimited access to episode viewing",
            "Any series available until the end of subscription period"
        )
        
        privileges.forEach { priv ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(0xFFD4AF37)))
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = priv, color = Color.White, fontSize = 14.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Subscribe now", color = Color.White, fontSize = 18.sp)
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        Text(
            text = "By continuing, you accept our Terms of Use and Privacy Policy",
            color = Color.Gray,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun InviteBottomSheetContent(onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }
        
        // Placeholder for 3D Gift Box
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Text("🎁", fontSize = 100.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Invite a friend and get a reward", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("+100", color = Color(0xFFFFC107), fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(0xFFFFC107)))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("VLJR7H", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Share", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("How does it work?", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text("1. Share your invitation code with a friend", color = Color.LightGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("2. Your friend follows the link or enters the code manually", color = Color.LightGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("3. You and your friend will receive 100 coins", color = Color.LightGray, fontSize = 14.sp)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
