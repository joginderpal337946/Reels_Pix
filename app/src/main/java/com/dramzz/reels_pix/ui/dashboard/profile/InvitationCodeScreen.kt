package com.dramzz.reels_pix.ui.dashboard.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dramzz.reels_pix.R
import androidx.compose.ui.res.stringResource

import com.dramzz.reels_pix.utils.CustomToast
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationCodeScreen(
    onBackClick: () -> Unit,
    viewModel: InvitationViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.checkLoginStatus()
    }
    var invitationCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .systemBarsPadding()
    ) {

        // Top App Bar
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.desc_back),
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Space below toolbar
        Spacer(modifier = Modifier.height(20.dp))

        // Mailbox Illustration
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(
                    id = R.drawable.mailbox_invitation_illustration
                ),
                contentDescription = stringResource(id = R.string.desc_mailbox_illustration),
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )
        }

        // Space between icon and card
        Spacer(modifier = Modifier.height(20.dp))

        // White Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {

                Text(
                    text = stringResource(id = R.string.invitation_message),
                    color = Color.Black,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = invitationCode,
                    onValueChange = {
                        invitationCode = it
                    },
                    label = {
                        Text(
                            text = stringResource(id = R.string.invitation_label),
                            color = Color.Gray
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.LightGray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(15.dp))

                Button(
                    onClick = {
                        if (!isLoggedIn) {
                            CustomToast.showError(context, "Please sign in first")
                        } else if (invitationCode.isBlank()) {
                            CustomToast.showError(context, "Please enter an invitation code")
                        } else {
                            viewModel.activateInvitationCode(invitationCode) { success, message ->
                                if (success) {
                                    CustomToast.showSuccess(context, message)
                                    invitationCode = ""
                                } else {
                                    CustomToast.showError(context, message)
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(

                        containerColor = Color(0xFFC4B5FD)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.invitation_activate),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
