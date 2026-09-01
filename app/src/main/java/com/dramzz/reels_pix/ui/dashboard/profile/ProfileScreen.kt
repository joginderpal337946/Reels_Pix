package com.dramzz.reels_pix.ui.dashboard.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.dramzz.reels_pix.R
import com.dramzz.reels_pix.data.model.AwardsMockData
import com.dramzz.reels_pix.data.model.SubscriptionPlan
import com.dramzz.reels_pix.ui.dashboard.awards.SubscriptionBottomSheetContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import com.dramzz.reels_pix.utils.CustomToast
import com.dramzz.reels_pix.utils.IntentUtils
import org.koin.androidx.compose.koinViewModel
import com.dramzz.reels_pix.data.api.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onSignInClick: () -> Unit,
    onWalletClick: () -> Unit,
    onTopUpClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onInvitationClick: () -> Unit,
    onWebViewClick: (url: String, title: String) -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val coins by viewModel.coins.collectAsState()
    val context = LocalContext.current
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.checkLoginStatus()
    }

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
                            contentDescription = stringResource(id = R.string.desc_avatar),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(userName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Button(
                    onClick = {
                        if (isLoggedIn) {
                            showLogoutDialog = true
                        } else {
                            onSignInClick()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        if (isLoggedIn) stringResource(id = R.string.profile_sign_out) 
                        else stringResource(id = R.string.profile_sign_in), 
                        color = Color.White
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                stringResource(id = R.string.profile_watch_restrictions),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
            )
            
            Text(
                stringResource(id = R.string.profile_unlock_episodes),
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
                        Text(stringResource(id = sub.durationRes), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        
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
                        Text(stringResource(id = R.string.wallet_title), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.ChevronRight, contentDescription = stringResource(id = R.string.desc_wallet), tint = Color.White)
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
                                    contentDescription = stringResource(id = R.string.desc_coin),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(id = R.string.wallet_coins), color = Color.Gray, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(coins, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Button(
                            onClick = onTopUpClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(stringResource(id = R.string.wallet_top_up), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            // Settings List
            Spacer(modifier = Modifier.height(24.dp))
            val settings = mutableListOf(
                stringResource(id = R.string.setting_language), 
                stringResource(id = R.string.setting_invitation_code), 
                stringResource(id = R.string.setting_feedback), 
                stringResource(id = R.string.setting_user_agreement), 
                stringResource(id = R.string.setting_privacy_policy), 
                stringResource(id = R.string.setting_share_app), 
                stringResource(id = R.string.setting_rate_app)
            ).apply {
                if (isLoggedIn) {
                    add(stringResource(id = R.string.setting_delete_account))
                }
            }
            
            val languageStr = stringResource(id = R.string.setting_language)
            val invitationCodeStr = stringResource(id = R.string.setting_invitation_code)
            val deleteAccountStr = stringResource(id = R.string.setting_delete_account)
            val privacyPolicyStr = stringResource(id = R.string.setting_privacy_policy)
            val userAgreementStr = stringResource(id = R.string.setting_user_agreement)
            val feedbackStr = stringResource(id = R.string.setting_feedback)
            val shareAppStr = stringResource(id = R.string.setting_share_app)

            
            settings.forEach { setting ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            when(setting) {
                                languageStr -> onLanguageClick()
                                invitationCodeStr -> onInvitationClick()
                                deleteAccountStr -> showDeleteAccountDialog = true
                                privacyPolicyStr -> onWebViewClick(Constants.BASE_MAIN_URL + Constants.PRIVACY_POLICY, privacyPolicyStr)
                                userAgreementStr -> onWebViewClick(Constants.BASE_MAIN_URL + Constants.USER_AGREEMENT, userAgreementStr)
                                feedbackStr -> IntentUtils.openEmail(context, "support@dramzz.com", "Feedback for Dramzz")
                                shareAppStr -> IntentUtils.shareApp(context, "Dramzz")
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
                Image(painter = painterResource(id = R.drawable.telegram_icon), contentDescription = stringResource(id = R.string.desc_telegram), modifier = Modifier.size(60.dp).clip(RoundedCornerShape(16.dp)))
                Spacer(modifier = Modifier.width(16.dp))
                Image(painter = painterResource(id = R.drawable.facebook_icon), contentDescription = stringResource(id = R.string.desc_facebook), modifier = Modifier.size(60.dp).clip(RoundedCornerShape(16.dp)))
                Spacer(modifier = Modifier.width(16.dp))
                Image(painter = painterResource(id = R.drawable.instagram_icon), contentDescription = stringResource(id = R.string.desc_instagram), modifier = Modifier.size(60.dp).clip(RoundedCornerShape(16.dp)))
                Spacer(modifier = Modifier.width(16.dp))
                Image(painter = painterResource(id = R.drawable.tiktok_icon), contentDescription = stringResource(id = R.string.desc_tiktok), modifier = Modifier.size(60.dp).clip(RoundedCornerShape(16.dp)))
            }
        }
        
        item {
            // Version
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                stringResource(id = R.string.profile_version, "1.5.3(43)"),
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
            SubscriptionBottomSheetContent(
                sub = showSubscriptionSheet!!,
                onDismiss = { showSubscriptionSheet = null },
                onSignInClick = onSignInClick
            )
        }
    }
    
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(id = R.string.dialog_logout_title)) },
            text = { Text(stringResource(id = R.string.dialog_logout_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.signOut { success, message ->
                        if (!success) {
                            CustomToast.showError(context, message)
                        } else {
                            CustomToast.showSuccess(context, message)
                        }
                    }
                }) {
                    Text(stringResource(id = R.string.dialog_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(id = R.string.dialog_cancel))
                }
            }
        )
    }

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text(stringResource(id = R.string.dialog_delete_account_title)) },
            text = { Text(stringResource(id = R.string.dialog_delete_account_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteAccountDialog = false
                    viewModel.deleteAccount { success, message ->
                        if (!success) {
                            CustomToast.showError(context, message)
                        } else {
                            CustomToast.showSuccess(context, message)
                        }
                    }
                }) {
                    Text(stringResource(id = R.string.dialog_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text(stringResource(id = R.string.dialog_cancel))
                }
            }
        )
    }
    }
}


