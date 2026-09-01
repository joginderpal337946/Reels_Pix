package com.dramzz.reels_pix.ui.dashboard.feed

import android.net.Uri
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.ui.PlayerView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import com.dramzz.reels_pix.R
import com.dramzz.reels_pix.data.model.FeedSeries
import com.dramzz.reels_pix.data.model.toMovie
import org.koin.androidx.compose.koinViewModel

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    onWatchNowClick: (com.dramzz.reels_pix.data.model.Movie) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onLockedEpisodeClick: () -> Unit = {},
    viewModel: FeedViewModel = koinViewModel()
) {
    val feedSeriesList by viewModel.feedSeriesList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var showEpisodeSheet by remember { mutableStateOf<FeedSeries?>(null) }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        if (isLoading && feedSeriesList.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        } else if (feedSeriesList.isNotEmpty()) {
            val pagerState = rememberPagerState(pageCount = { feedSeriesList.size })
            val context = androidx.compose.ui.platform.LocalContext.current
            
            // Single shared ExoPlayer instance
            val exoPlayer = remember {
                val loadControl = DefaultLoadControl.Builder()
                    .setBufferDurationsMs(
                        1000,  // minBufferMs
                        30000, // maxBufferMs
                        500,   // bufferForPlaybackMs - start after 500ms buffered
                        1000   // bufferForPlaybackAfterRebufferMs
                    )
                    .build()
                ExoPlayer.Builder(context)
                    .setLoadControl(loadControl)
                    .build().apply {
                        repeatMode = Player.REPEAT_MODE_ONE
                        playWhenReady = true
                    }
            }

            // Switch video source on page change
            LaunchedEffect(pagerState.currentPage) {
                val currentReel = feedSeriesList.getOrNull(pagerState.currentPage)
                val url = currentReel?.episodes?.firstOrNull()?.content
                val videoUri = if (url != null) Uri.parse(url)
                    else Uri.parse("android.resource://${context.packageName}/${R.raw.reels_dummy_video}")
                exoPlayer.setMediaItem(MediaItem.fromUri(videoUri))
                exoPlayer.prepare()
                exoPlayer.play()
            }

            DisposableEffect(Unit) {
                onDispose { exoPlayer.release() }
            }

            // ── LAYER 1: Single persistent PlayerView (never destroyed) ──
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // ── LAYER 2: Pager renders only overlay UI (no video view per page) ──
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                ReelItem(
                    reel = feedSeriesList[page],
                    context = context,
                    onEpisodeClick = { showEpisodeSheet = feedSeriesList[page] },
                    onWatchNowClick = { onWatchNowClick(feedSeriesList[page].toMovie()) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onToggleWishlist = { viewModel.toggleWishlist(it) }
                )
            }
        } else if (error != null) {
            Text(
                text = error ?: "",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Search Icon at top right
        IconButton(
            onClick = onSearchClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    end = 8.dp,
                    top = 4.dp
                )
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(id = R.string.desc_search),
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
                selectedEpisodeNumber = reel.episodes?.firstOrNull()?.episodeNumber,
                onDismiss = { showEpisodeSheet = null },
                onEpisodeSelected = { isLocked, episodeNumber ->
                    showEpisodeSheet = null
                    if (isLocked) {
                        onLockedEpisodeClick()
                    } else {
                        onWatchNowClick(reel.toMovie(episodeNumber))
                    }
                }
            )
        }
    }
}

@Composable
fun ReelItem(
    reel: FeedSeries,
    context: android.content.Context,
    onEpisodeClick: () -> Unit,
    onWatchNowClick: () -> Unit = {},
    onToggleFavorite: (Int) -> Boolean = { true },
    onToggleWishlist: (Int) -> Boolean = { true }
) {
    // Overlay only - no VideoView/PlayerView here
    Box(modifier = Modifier.fillMaxSize()) {
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
            val firstEpisode = reel.episodes?.firstOrNull()
            // Like
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { 
                    if (firstEpisode != null) {
                        if (!onToggleFavorite(firstEpisode.id)) {
                            android.widget.Toast.makeText(context, "Please sign in first", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    } 
                }) {
                    Icon(
                        imageVector = if (firstEpisode?.isFavourite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(id = R.string.desc_like),
                        tint = if (firstEpisode?.isFavourite == true) Color.Red else Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Bookmark
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { 
                    if (!onToggleWishlist(reel.id)) {
                        android.widget.Toast.makeText(context, "Please sign in first", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Icon(
                        imageVector = if (reel.isWishlisted) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = stringResource(id = R.string.desc_bookmark),
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Episode
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onEpisodeClick) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = stringResource(id = R.string.desc_episode),
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = stringResource(id = R.string.video_episode),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Send
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { 
                    com.dramzz.reels_pix.utils.IntentUtils.shareSeries(context, reel.title, reel.description) 
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = stringResource(id = R.string.desc_send),
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = stringResource(id = R.string.video_send),
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
                Text(stringResource(id = R.string.watch_now), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Composable
fun EpisodeBottomSheetContent(
    reel: FeedSeries, 
    selectedEpisodeNumber: Int? = null,
    onDismiss: () -> Unit, 
    onEpisodeSelected: (isLocked: Boolean, episodeNumber: Int) -> Unit = { _, _ -> }
) {
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
                Icon(Icons.Default.Close, contentDescription = stringResource(id = R.string.desc_close), tint = Color.White)
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
            Text(stringResource(id = R.string.search_exclusive), color = Color.LightGray, fontSize = 12.sp)
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
        
        Text("1–${reel.totalEpisodes}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Episodes Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(reel.totalEpisodes) { index ->
                val episodeNumber = index + 1
                val episode = reel.episodes?.find { it.episodeNumber == episodeNumber }
                val isLocked = episode?.isLocked ?: (episodeNumber > reel.episodeLockedAfterNumber)
                val isSelected = episodeNumber == (selectedEpisodeNumber ?: 1)
                
                Box(
                    modifier = Modifier
                        .aspectRatio(0.8f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFF673AB7) else Color(0xFF333333))
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
                                contentDescription = stringResource(id = R.string.desc_locked),
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
