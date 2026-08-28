package com.example.reels_pix.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onSignInClick: () -> Unit,
    onWalletClick: () -> Unit,
    onTopUpClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onInvitationClick: () -> Unit
) {
    var showSubscriptionSheet by remember { mutableStateOf<SubscriptionPlan?>(null) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF000000)),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
        item {
            // User Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // App Logo as avatar placeholder
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "Avatar",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text("Anonymous", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("196429368", color = Color.Gray, fontSize = 14.sp)
                    }
                }
                
                Button(
                    onClick = onSignInClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Sign In", color = Color.White)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Watch without restrictions",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
            )
            
            Text(
                "Unlock episodes:",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            AwardsMockData.subscriptions.forEach { sub ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .height(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFD4AF37)) // Gold color
                        .clickable { showSubscriptionSheet = sub }
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
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(sub.price, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            // My Wallet
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E1E1E))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onWalletClick() },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("My Wallet", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.ChevronRight, contentDescription = "Wallet", tint = Color.White)
                    }
                    
                    Divider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.reward_gold_coin),
                                    contentDescription = "Coin",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Coins", color = Color.Gray, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("10", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Button(
                            onClick = onTopUpClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text("Top Up", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            // Settings List
            Spacer(modifier = Modifier.height(24.dp))
            val settings = listOf(
                "Language", 
                "Invitation code", 
                "Feedback", 
                "User Agreement", 
                "Privacy Policy", 
                "Share App", 
                "Rate App",
//                "Delete Account",
//                "Logout"
            )
            
            settings.forEach { setting ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            when(setting) {
                                "Language" -> onLanguageClick()
                                "Invitation code" -> onInvitationClick()
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(setting, color = Color.White, fontSize = 16.sp)
                    Icon(Icons.Default.ChevronRight, contentDescription = setting, tint = Color.White)
                }
            }
        }
        
        item {
            // Social Icons
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painter = painterResource(id = R.drawable.telegram_icon), contentDescription = "Telegram", modifier = Modifier.size(60.dp).clip(RoundedCornerShape(16.dp)))
                Spacer(modifier = Modifier.width(16.dp))
                Image(painter = painterResource(id = R.drawable.facebook_icon), contentDescription = "Facebook", modifier = Modifier.size(60.dp).clip(RoundedCornerShape(16.dp)))
                Spacer(modifier = Modifier.width(16.dp))
                Image(painter = painterResource(id = R.drawable.instagram_icon), contentDescription = "Instagram", modifier = Modifier.size(60.dp).clip(RoundedCornerShape(16.dp)))
                Spacer(modifier = Modifier.width(16.dp))
                Image(painter = painterResource(id = R.drawable.tiktok_icon), contentDescription = "TikTok", modifier = Modifier.size(60.dp).clip(RoundedCornerShape(16.dp)))
            }
        }
        
        item {
            // Version
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Version 1.5.3(43)",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
            )
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
    }
}


