package com.dramzz.reels_pix.ui.dashboard.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dramzz.reels_pix.R
import com.dramzz.reels_pix.utils.CustomToast
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.text.ClickableText
import com.dramzz.reels_pix.data.api.Constants
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignInScreen(
    onBackClick: () -> Unit,
    onSignInSuccess: () -> Unit = {},
    onWebViewClick: (url: String, title: String) -> Unit = { _, _ -> },
    viewModel: SignInViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val isLoading by viewModel.isLoading.collectAsState()
    val signInSuccess by viewModel.signInSuccess.collectAsState()
    val signInError by viewModel.signInError.collectAsState()

    LaunchedEffect(signInSuccess) {
        if (signInSuccess) {
            CustomToast.showSuccess(context, "Sign in successful!")
            onSignInSuccess()
        }
    }

    LaunchedEffect(signInError) {
        signInError?.let {
            CustomToast.showError(context, it)
        }
    }

    // Local loading state for Google Auth UI progress
    var isGoogleLoading by remember { mutableStateOf(false) }
    val authClient = remember { GoogleAuthClient(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.desc_back), tint = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Film strip background effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.reels_film_strip_banner),
                contentDescription = stringResource(id = R.string.desc_background),
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // ReelPix Logo
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .background(Color.White, shape = androidx.compose.foundation.shape.CircleShape)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = stringResource(id = R.string.desc_reelpix_logo),
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
//                Text(stringResource(id = R.string.app_name), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Sign in with Google Button
        Button(
            onClick = {
                if (isLoading || isGoogleLoading) return@Button
                isGoogleLoading = true
                coroutineScope.launch {
                    val result = authClient.signIn()
                    if (result.isSuccess) {
                        val googleUser = result.getOrNull()
                        if (googleUser != null) {
                            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                val token = if (task.isSuccessful) task.result else "dummy_token"
                                viewModel.performSocialLogin(googleUser, token)
                                isGoogleLoading = false
                            }
                        } else {
                            CustomToast.showError(context, "Google user details missing")
                            isGoogleLoading = false
                        }
                    } else {
                        CustomToast.showError(context, "Sign in failed")
                        isGoogleLoading = false
                    }
                }
            },
            enabled = !isLoading && !isGoogleLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                disabledContainerColor = Color.LightGray
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isLoading || isGoogleLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.google_icon),
                        contentDescription = stringResource(id = R.string.desc_google),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isLoading || isGoogleLoading) "Signing in..." else stringResource(id = R.string.signin_google),
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        val annotatedText = androidx.compose.ui.text.buildAnnotatedString {
            append("By continuing, you accept our ")
            pushStringAnnotation(tag = "TERMS", annotation = "TERMS")
            withStyle(style = androidx.compose.ui.text.SpanStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)) {
                append("Terms of Use")
            }
            pop()
            append(" and ")
            pushStringAnnotation(tag = "PRIVACY", annotation = "PRIVACY")
            withStyle(style = androidx.compose.ui.text.SpanStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)) {
                append("Privacy Policy")
            }
            pop()
        }
        
        ClickableText(
            text = annotatedText,
            style = androidx.compose.ui.text.TextStyle(color = Color.Gray, fontSize = 12.sp),
            modifier = Modifier.padding(bottom = 32.dp),
            onClick = { offset ->
                annotatedText.getStringAnnotations(tag = "TERMS", start = offset, end = offset).firstOrNull()?.let {
                    onWebViewClick(Constants.BASE_MAIN_URL + Constants.USER_AGREEMENT, "Terms of Use")
                }
                annotatedText.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset).firstOrNull()?.let {
                    onWebViewClick(Constants.BASE_MAIN_URL + Constants.PRIVACY_POLICY, "Privacy Policy")
                }
            }
        )
    }
}
