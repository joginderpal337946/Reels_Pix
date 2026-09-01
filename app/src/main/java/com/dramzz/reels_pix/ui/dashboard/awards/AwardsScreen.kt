package com.dramzz.reels_pix.ui.dashboard.awards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.dramzz.reels_pix.R
import com.dramzz.reels_pix.data.model.AwardsMockData
import com.dramzz.reels_pix.data.model.SubscriptionPlan
import com.dramzz.reels_pix.data.model.TopUpPackage
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.draw.blur
import androidx.compose.foundation.interaction.MutableInteractionSource
import org.koin.androidx.compose.koinViewModel
import com.dramzz.reels_pix.ui.dashboard.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AwardsScreen(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onSignInClick: () -> Unit,
    onInvitationClick: () -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel(),
    awardsViewModel: AwardsViewModel = koinViewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val rewardData by awardsViewModel.rewardData.collectAsState()
    val isLoading by awardsViewModel.isLoading.collectAsState()

    LaunchedEffect(isLoggedIn) {
        viewModel.checkLoginStatus()
        if (isLoggedIn) {
            awardsViewModel.fetchRewards()
        }
    }
    val tabs = listOf(stringResource(id = R.string.tab_rewards), stringResource(id = R.string.tab_shop))
    
    var showTopUpSheet by remember { mutableStateOf<TopUpPackage?>(null) }
    var showSubscriptionSheet by remember { mutableStateOf<SubscriptionPlan?>(null) }
    var showInviteSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .let { if (!isLoggedIn) it.blur(16.dp) else it }
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
                    onInviteClick = { showInviteSheet = true },
                    isInviteFriend ={onInvitationClick},
                    rewardData = rewardData,
                    onClaimClick = { id ->
                        awardsViewModel.claimReward(id) { success, message ->
                            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            } else {
                ShopTabContent(
                    onSubscriptionClick = { showSubscriptionSheet = it },
                    onTopUpClick = { showTopUpSheet = it }
                )
            }
        }

        if (!isLoggedIn) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { },
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onSignInClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(50.dp)
                ) {
                    Text(stringResource(id = R.string.profile_sign_in), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
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
            SubscriptionBottomSheetContent(
                sub = showSubscriptionSheet!!, 
                onDismiss = { showSubscriptionSheet = null },
                onSignInClick = onSignInClick
            )
        }
    }
    
    if (showInviteSheet) {
        ModalBottomSheet(
            onDismissRequest = { showInviteSheet = false },
            containerColor = Color(0xFF3B205E), // Deep purple background for invite
            dragHandle = null
        ) {
            val referralCode by viewModel.referralCode.collectAsState()
            InviteBottomSheetContent(referralCode = referralCode, onDismiss = { showInviteSheet = false })
        }
    }
}

@Composable
fun RewardsTabContent(
    onInviteClick: () -> Unit,
    isInviteFriend: () -> Unit,
    rewardData: com.dramzz.reels_pix.data.model.RewardData?,
    onClaimClick: (Int) -> Unit = {}
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
                    Text(stringResource(id = R.string.awards_balance), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.reward_gold_coin),
                            contentDescription = stringResource(id = R.string.desc_coin),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${rewardData?.balance ?: 0}", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                }
                // Treasure Chest Graphic
                Image(
                    painter = painterResource(id = R.drawable.treasure_chest_coins),
                    contentDescription = stringResource(id = R.string.desc_treasure_chest),
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
                val dailyRewards = rewardData?.dailyRewards ?: emptyList()
                items(if (dailyRewards.isNotEmpty()) dailyRewards.size else 7) { index ->
                    val dayItem = dailyRewards.getOrNull(index)
                    val dayNum = dayItem?.dayNumber ?: (index + 1)
                    val coins = dayItem?.coins ?: if (index < 3) 20 else if (index < 5) 30 else 40
                    val isClaimable = dayItem?.status == "claimable"
                    val isClaimed = dayItem?.status == "claimed"
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(60.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isClaimed) Color(0xFF2C2C2C) else Color(0xFF4E342E)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isClaimed) {
                                // Check mark
                                Text("✓", color = Color.Gray, fontSize = 24.sp)
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        painter = painterResource(id = R.drawable.reward_gold_coin),
                                        contentDescription = stringResource(id = R.string.desc_coin),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("+$coins", color = Color.White, fontSize = 14.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            if (isClaimable) stringResource(id = R.string.awards_today) else stringResource(id = R.string.awards_day, dayNum),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val claimableDaily = rewardData?.dailyRewards?.find { it.status == "claimable" }
            Button(
                onClick = { claimableDaily?.id?.let { onClaimClick(it) } },
                enabled = claimableDaily != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B205E),
                    disabledContainerColor = Color(0xFF2C2C2C)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp)
            ) {
                Text(
                    stringResource(id = R.string.awards_claim_bonuses), 
                    color = if (claimableDaily != null) Color(0xFFB39DDB) else Color.Gray, 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Bold
                )
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
                        Text(stringResource(id = R.string.awards_invite_friend), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                            Text(stringResource(id = R.string.awards_coins_for_friend), color = Color.Gray)
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = stringResource(id = R.string.desc_invite),
                        tint = Color.White
                    )
                }
            }
        }

        item {
            // Today's Rewards
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                stringResource(id = R.string.awards_todays_rewards),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        val otherRewards = rewardData?.otherRewards ?: emptyList()
        items(otherRewards) { task ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1E1E))
                    .clickable(enabled = !task.isClaimed) { if (task.title=="Invite Friend") isInviteFriend() else onClaimClick(task.id) }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(task.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        if (!task.description.isNullOrEmpty()) {
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
                                    .background(if (task.isClaimed) Color.Gray else Color(0xFFFFC107))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (task.isClaimed) "Claimed" else "+${task.coins}",
                                color = if (task.isClaimed) Color.Gray else Color(0xFFFFC107),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            // Rules
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                stringResource(id = R.string.awards_rules_info),
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
                    Text(stringResource(id = R.string.awards_balance), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.reward_gold_coin),
                            contentDescription = stringResource(id = R.string.desc_coin),
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
                        Text(stringResource(id = sub.durationRes), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        
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
            Text(stringResource(id = R.string.awards_coins_purchase), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = stringResource(id = R.string.desc_close), tint = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(id = R.string.awards_pack_format, pkg.baseCoins, pkg.bonusTag),
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
            Text(stringResource(id = R.string.awards_buy), color = Color.White, fontSize = 18.sp)
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        Text(
            text = stringResource(id = R.string.awards_terms_privacy),
            color = Color.Gray,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun SubscriptionBottomSheetContent(
    sub: SubscriptionPlan, 
    onDismiss: () -> Unit,
    onSignInClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.checkLoginStatus()
    }

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
            Text(stringResource(id = R.string.awards_subscription_privileges), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = stringResource(id = R.string.desc_close), tint = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .let { if (!isLoggedIn) it.blur(16.dp) else it },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.awards_subscription_format, stringResource(id = sub.durationRes)),
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
            stringResource(id = R.string.awards_privilege_1),
            stringResource(id = R.string.awards_privilege_2),
            stringResource(id = R.string.awards_privilege_3),
            stringResource(id = R.string.awards_privilege_4)
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
            Text(stringResource(id = R.string.awards_subscribe_now), color = Color.White, fontSize = 18.sp)
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        Text(
            text = stringResource(id = R.string.awards_terms_privacy),
            color = Color.Gray,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
            } // Close inner Column
            
            if (!isLoggedIn) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color(0x88000000))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { },
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            onDismiss()
                            onSignInClick()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp)
                    ) {
                        Text(stringResource(id = R.string.profile_sign_in), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } // Close outer Box
    }
}

@Composable
fun InviteBottomSheetContent(referralCode: String, onDismiss: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = stringResource(id = R.string.desc_close), tint = Color.White)
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
        
        Text(stringResource(id = R.string.awards_invite_title), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        
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
                Text(referralCode.ifEmpty { "N/A" }, color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Button(
                onClick = { 
                    if (referralCode.isNotEmpty()) {
                        com.dramzz.reels_pix.utils.IntentUtils.shareReferralCode(context, referralCode)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(stringResource(id = R.string.awards_share), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(id = R.string.awards_how_it_works), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(stringResource(id = R.string.awards_rule_1), color = Color.LightGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(id = R.string.awards_rule_2), color = Color.LightGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(id = R.string.awards_rule_3), color = Color.LightGray, fontSize = 14.sp)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
