package com.example.reels_pix.ui.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.VideoView
import android.net.Uri
import coil.compose.AsyncImage
import com.example.reels_pix.R

data class ReelMock(
    val id: String,
    val title: String,
    val description: String,
    val videoResId: Int,
    val likes: Int,
    val bookmarks: Int
)

val mockReels = listOf(
    ReelMock(
        id = "1",
        title = "Bought by my enemy",
        description = "Felicity has had it tough: mother's illness, betrayal from those closest to her, and overwhelming debts — with no one to turn to...",
        videoResId = R.raw.reels_dummy_video,
        likes = 345,
        bookmarks = 214
    ),
    ReelMock(
        id = "2",
        title = "I can hear my boss's thoughts",
        description = "After a freak accident on her first day at work, Maria can suddenly hear the thoughts of anyone she touches. Her bizarre new quirk immediately hooks the attention of her strict CEO...",
        videoResId = R.raw.reels_dummy_2,
        likes = 892,
        bookmarks = 405
    ),
    ReelMock(
        id = "3",
        title = "The Escaping Mistress",
        description = "She tried to escape, but he wouldn't let her go. A story of passion, deception, and undeniable attraction.",
        videoResId = R.raw.reels_dummy_3,
        likes = 120,
        bookmarks = 45
    ),ReelMock(
        id = "4",
        title = "The Pride Mistress",
        description = "She tried to escape, but he wouldn't let her go. A story of passion, deception, and undeniable attraction.",
        videoResId = R.raw.reels_dummy_2,
        likes = 124,
        bookmarks = 400
    )
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    modifier: Modifier = Modifier, 
    onWatchNowClick: (String) -> Unit = {}, 
    onSearchClick: () -> Unit = {},
    onLockedEpisodeClick: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { mockReels.size })
    var showEpisodeSheet by remember { mutableStateOf<ReelMock?>(null) }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            ReelItem(
                reel = mockReels[page],
                onEpisodeClick = { showEpisodeSheet = mockReels[page] },
                onWatchNowClick = { onWatchNowClick(mockReels[page].title) }
            )
        }

        // Search Icon at top right
        IconButton(
            onClick = onSearchClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(
                    end = 8.dp,
                    top = 4.dp
                )
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

    }

    showEpisodeSheet?.let { reel ->
        ModalBottomSheet(
            onDismissRequest = { showEpisodeSheet = null },
            containerColor = Color(0xFF1E1E1E),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
        ) {
            EpisodeBottomSheetContent(
                reel = reel,
                onDismiss = { showEpisodeSheet = null },
                onEpisodeSelected = { isLocked, episodeNumber ->
                    showEpisodeSheet = null
                    if (isLocked) {
                        onLockedEpisodeClick()
                    } else {
                        onWatchNowClick(reel.title)
                    }
                }
            )
        }
    }
}

@Composable
fun ReelItem(reel: ReelMock, onEpisodeClick: () -> Unit, onWatchNowClick: () -> Unit = {}) {
    var isLiked by remember { mutableStateOf(false) }
    var isBookmarked by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Video Player
        AndroidView(
            factory = { context ->
                VideoView(context).apply {
                    val videoUri = Uri.parse("android.resource://${context.packageName}/${reel.videoResId}")
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
        
        // Dark gradient overlay at the bottom for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                        startY = 600f
                    )
                )
        )

        // Right side icons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 24.dp),
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
                    text = "${if (isLiked) reel.likes + 1 else reel.likes}",
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
                    text = "${if (isBookmarked) reel.bookmarks + 1 else reel.bookmarks}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Episode
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onEpisodeClick) {
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

        // Bottom left details
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.75f)
                .padding(start = 16.dp, bottom = 24.dp)
        ) {
            Text(
                text = reel.title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = reel.description,
                color = Color.LightGray,
                fontSize = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onWatchNowClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
            ) {
                Text("Watch Now", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EpisodeBottomSheetContent(reel: ReelMock, onDismiss: () -> Unit, onEpisodeSelected: (isLocked: Boolean, episodeNumber: Int) -> Unit = { _, _ -> }) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f) // Occupy 80% of screen height
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = reel.title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Exclusive Tag
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF333333))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text("Exclusive", color = Color.LightGray, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = reel.description,
            color = Color.Gray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        Text("1–20", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Episodes Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(20) { index ->
                val episodeNumber = index + 1
                val isLocked = episodeNumber > 7 // Episodes 8-20 are locked based on screenshot
                
                Box(
                    modifier = Modifier
                        .aspectRatio(0.8f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (episodeNumber == 1) Color(0xFF673AB7) else Color(0xFF333333))
                        .clickable { onEpisodeSelected(isLocked, episodeNumber) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = episodeNumber.toString(),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (isLocked) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color(0xFFB39DDB), // Light purple lock
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
