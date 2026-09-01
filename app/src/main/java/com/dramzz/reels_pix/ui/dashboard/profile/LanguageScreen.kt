package com.dramzz.reels_pix.ui.dashboard.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dramzz.reels_pix.R
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import com.dramzz.reels_pix.utils.LanguageManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val languageManager = remember { LanguageManager(context) }
    
    val languageMap = mapOf(
        "Русский" to "ru",
        "English" to "en",
        "Türkçe" to "tr",
        "Português" to "pt",
        "Español" to "es",
        "العربية" to "ar"
    )
    
    val currentLangCode = languageManager.getLanguage()
    val initialLanguageName = languageMap.entries.find { it.value == currentLangCode }?.key ?: "English"
    
    var selectedLanguage by remember { mutableStateOf(initialLanguageName) }
    
    val languages = listOf(
        "Русский",
        "English",
        "Türkçe",
        "Português",
        "Español",
        "العربية"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .systemBarsPadding()
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(stringResource(id = R.string.language_title), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 48.dp))
                }
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.desc_back), tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E1E1E))
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(languages) { language ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            selectedLanguage = language 
                            languageMap[language]?.let { code ->
                                languageManager.setLanguage(code)
                                (context as? Activity)?.recreate()
                            }
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = language,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = if (selectedLanguage == language) FontWeight.Bold else FontWeight.Normal
                    )
                    
                    if (selectedLanguage == language) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = stringResource(id = R.string.desc_selected),
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Circle,
                            contentDescription = stringResource(id = R.string.desc_unselected),
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                HorizontalDivider(color = Color(0xFF1E1E1E), thickness = 1.dp)
            }
        }
    }
}
