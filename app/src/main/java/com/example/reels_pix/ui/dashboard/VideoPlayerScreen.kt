package com.example.reels_pix.ui.dashboard

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.reels_pix.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    seriesTitle: String,
    onBackClick: () -> Unit,
    onLockedEpisodeClick: () -> Unit = {}
) {
    var showSettingsSheet by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }
    var isBookmarked by remember { mutableStateOf(false) }
    var showEpisodeSheet by remember { mutableStateOf(false) }
    var episodeNumber by remember { mutableIntStateOf(1) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                var dragOffsetY = 0f
                detectVerticalDragGestures(
                    onDragStart = { dragOffsetY = 0f },
                    onDragEnd = {
                        if (dragOffsetY < -50f) { // Swiped up (scrolling to bottom)
                            episodeNumber++
                        }
                        // Do nothing on swipe down (scroll to top)
                    },
                    onVerticalDrag = { change, dragAmount ->
                        dragOffsetY += dragAmount
                        change.consume()
                    }
                )
            }
    ) {
        // Video Player
        AndroidView(
            factory = { context ->
                VideoView(context).apply {
                    val videoUri = Uri.parse("android.resource://${context.packageName}/${R.raw.reels_dummy_video}")
                    setVideoURI(videoUri)
                    setOnPreparedListener { mp ->
                        mp.isLooping = true
                        start()
                        post {
                            if (width > 0 && height > 0) {
                                val videoRatio = mp.videoWidth / mp.videoHeight.toFloat()
                                val screenRatio = width / height.toFloat()
                                if (videoRatio > screenRatio) {
                                    scaleX = videoRatio / screenRatio
                                    scaleY = 1f
                                } else {
                                    scaleX = 1f
                                    scaleY = screenRatio / videoRatio
                                }
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Top Overlay (Gradient)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.dp)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                    )
                )
                .align(Alignment.TopCenter)
        )

        // Top Content
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth().statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Back button - Left
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                // Title + Episode - Middle
                Column(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = seriesTitle,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )

                    Text(
                        text = "Episode $episodeNumber",
                        color = Color.LightGray,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }

                // Three dots - Right
                IconButton(
                    onClick = { showSettingsSheet = true },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = Color.White
                    )
                }
            }
        }

        // Bottom Overlay (Gradient)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                    )
                )
                .align(Alignment.BottomCenter)
        )

        // Bottom Options
        // Right side icons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Like
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { isLiked = !isLiked }) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.Red else Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = "${if (isLiked) 346 else 345}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Bookmark
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { isBookmarked = !isBookmarked }) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = "${if (isBookmarked) 215 else 214}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Episode
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { showEpisodeSheet = true }) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Episode",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = "Episode",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Send
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { /* TODO: Send */ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = "Send",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showEpisodeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showEpisodeSheet = false },
            containerColor = Color(0xFF1E1E1E),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
        ) {
            val dummyReel = ReelMock(
                id = "dummy",
                title = seriesTitle,
                description = "Description for $seriesTitle.",
                videoResId = R.raw.reels_dummy_video,
                likes = 0,
                bookmarks = 0
            )
            EpisodeBottomSheetContent(
                reel = dummyReel,
                onDismiss = { showEpisodeSheet = false },
                onEpisodeSelected = { isLocked, episodeNum ->
                    showEpisodeSheet = false
                    if (isLocked) {
                        onLockedEpisodeClick()
                    } else {
                        episodeNumber = episodeNum
                    }
                }
            )
        }
    }

    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            containerColor = Color(0xFF1E1E1E),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
        ) {
            SettingsBottomSheetContent(onDismiss = { showSettingsSheet = false })
        }
    }
}

@Composable
fun SettingsBottomSheetContent(onDismiss: () -> Unit) {
    var selectedQuality by remember { mutableStateOf("Auto") }
    var subtitlesEnabled by remember { mutableStateOf(true) }
    var selectedSubtitle by remember { mutableStateOf("English") }
    var selectedSpeed by remember { mutableStateOf("1.0x") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 24.dp)
    ) {
        // Header
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Quality
        Text("Quality", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val qualities = listOf("Auto", "1080p", "720p", "480p")
            qualities.forEach { quality ->
                SettingsOptionButton(
                    text = quality,
                    isSelected = selectedQuality == quality,
                    onClick = { selectedQuality = quality },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Subtitles Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Subtitles", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Switch(
                checked = subtitlesEnabled,
                onCheckedChange = { subtitlesEnabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF673AB7),
                    uncheckedThumbColor = Color.LightGray,
                    uncheckedTrackColor = Color.DarkGray
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        
        // Subtitles Options
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val subtitles = listOf("English", "Español", "Português", "Türkçe")
            subtitles.forEach { subtitle ->
                SettingsOptionButton(
                    text = subtitle,
                    isSelected = selectedSubtitle == subtitle && subtitlesEnabled,
                    onClick = { if (subtitlesEnabled) selectedSubtitle = subtitle },
                    modifier = Modifier.weight(1f),
                    enabled = subtitlesEnabled
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        // Playback Speed
        Text("Playback Speed", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val speeds = listOf("1.0x", "1.25x", "1.5x", "2.0x")
            speeds.forEach { speed ->
                SettingsOptionButton(
                    text = speed,
                    isSelected = selectedSpeed == speed,
                    onClick = { selectedSpeed = speed },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SettingsOptionButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val backgroundColor = if (isSelected) Color(0xFF673AB7) else Color(0xFF333333)
    val alpha = if (enabled) 1f else 0.5f

    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor.copy(alpha = alpha))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White.copy(alpha = alpha),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
