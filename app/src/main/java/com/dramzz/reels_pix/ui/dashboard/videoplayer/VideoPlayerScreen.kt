package com.dramzz.reels_pix.ui.dashboard.videoplayer

import android.net.Uri
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.ui.PlayerView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.dramzz.reels_pix.R
import androidx.compose.ui.res.stringResource
import com.dramzz.reels_pix.data.model.FeedSeries
import com.dramzz.reels_pix.data.model.FeedEpisode
import com.dramzz.reels_pix.ui.dashboard.feed.EpisodeBottomSheetContent
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import androidx.activity.compose.BackHandler
import androidx.media3.common.util.UnstableApi

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    seriesId: Int,
    seriesTitle: String,
    initialEpisodeNumber: Int? = null,
    onBackClick: (Int) -> Unit,
    onLockedEpisodeClick: () -> Unit = {},
    viewModel: VideoPlayerViewModel = koinViewModel()
) {
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showEpisodeSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    val episodes by viewModel.episodes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isPaginating by viewModel.isPaginating.collectAsState()

    LaunchedEffect(seriesId) {
        viewModel.fetchEpisodes(seriesId, isRefresh = true)
    }

    if (isLoading && episodes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { episodes.size })
    var previousPage by remember { mutableIntStateOf(pagerState.currentPage) }
    
    var hasJumpedToInitial by remember { mutableStateOf(false) }

    LaunchedEffect(episodes.size, initialEpisodeNumber) {
        if (!hasJumpedToInitial && initialEpisodeNumber != null && episodes.isNotEmpty()) {
            val index = episodes.indexOfFirst { it.episodeNumber == initialEpisodeNumber }
            if (index != -1) {
                pagerState.scrollToPage(index)
                hasJumpedToInitial = true
            } else if (viewModel.hasMore && !isPaginating && !isLoading) {
                viewModel.fetchEpisodes(seriesId)
            }
        }
    }

    // Pagination trigger
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage >= episodes.size - 2 && viewModel.hasMore && !isPaginating && !isLoading) {
            viewModel.fetchEpisodes(seriesId)
        }
    }

    // Locked episode handling on swipe
    LaunchedEffect(pagerState.currentPage) {
        if (episodes.isNotEmpty() && pagerState.currentPage < episodes.size) {
            val currentEpisode = episodes[pagerState.currentPage]
            if (currentEpisode.isLocked) {
                // Trigger locked flow (TopUp sheet or SignIn)
                onLockedEpisodeClick()
                // Snap back to the last unlocked page
                pagerState.scrollToPage(previousPage)
            } else {
                previousPage = pagerState.currentPage
            }
        }
    }

    // Viewing history: record each episode the user settles on (only when logged in)
    LaunchedEffect(pagerState.currentPage) {
        if (episodes.isNotEmpty() && pagerState.currentPage < episodes.size) {
            val ep = episodes[pagerState.currentPage]
            if (!ep.isLocked) {
                viewModel.updateViewingHistory(
                    seriesId = ep.seriesId,
                    episodeId = ep.id,
                    episodeNumber = ep.episodeNumber
                )
            }
        }
    }

    val triggerBack = {
        val currentEp = if (episodes.isNotEmpty() && pagerState.currentPage < episodes.size) {
            episodes[pagerState.currentPage].episodeNumber
        } else initialEpisodeNumber ?: 1
        
        onBackClick(viewModel.getMaxWatched(seriesId).takeIf { it > 0 } ?: currentEp)
    }

    // Back press: record current episode in history before leaving
    BackHandler {
        if (episodes.isNotEmpty() && pagerState.currentPage < episodes.size) {
            val ep = episodes[pagerState.currentPage]
            if (!ep.isLocked) {
                viewModel.updateViewingHistory(
                    seriesId = ep.seriesId,
                    episodeId = ep.id,
                    episodeNumber = ep.episodeNumber
                )
            }
        }
        triggerBack()
    }

    // Single shared ExoPlayer for all episodes
    val exoPlayer = remember {
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                1000,  // minBufferMs
                30000, // maxBufferMs
                500,   // bufferForPlaybackMs - play after 500ms buffered
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

    // Switch video source whenever the current page changes
    LaunchedEffect(pagerState.currentPage, episodes.size) {
        val ep = episodes.getOrNull(pagerState.currentPage)
        val videoUri = ep?.content?.let { Uri.parse(it) }
            ?: Uri.parse("android.resource://${context.packageName}/${R.raw.reels_dummy_video}")
        exoPlayer.setMediaItem(MediaItem.fromUri(videoUri))
        exoPlayer.prepare()
        exoPlayer.play()
    }
    
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }
    
    var isPlaying by remember { mutableStateOf(true) }

    // ── LAYER 1: Single persistent PlayerView, never destroyed on swipe ──
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
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

        // ── LAYER 2: Pager renders only overlay UI ──
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val currentEpisode = episodes[page]
            val episodeNumber = currentEpisode.episodeNumber

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isPlaying = !isPlaying
                        if (isPlaying) exoPlayer.play() else exoPlayer.pause()
                    }
            ) {
                // Play Icon Overlay
                if (!isPlaying) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.Center)
                    )
                }

                // Top Overlay (Gradient)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
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
                            onClick = triggerBack
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.desc_back),
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
                                text = stringResource(id = R.string.episode_format, episodeNumber),
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
                                contentDescription = stringResource(id = R.string.desc_more_options),
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
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                            )
                        )
                        .align(Alignment.BottomCenter)
                )

                // Bottom Options
                // Right side icons
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .navigationBarsPadding()
                        .padding(end = 10.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Like
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = {
                            if (!viewModel.toggleFavorite(currentEpisode.id)) {
                                android.widget.Toast.makeText(
                                    context,
                                    "Please sign in first",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            Icon(
                                imageVector = if (currentEpisode.isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = stringResource(id = R.string.desc_like),
                                tint = if (currentEpisode.isFavourite) Color.Red else Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // Bookmark
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = {
                            if (!viewModel.toggleWishlist(currentEpisode.seriesId)) {
                                android.widget.Toast.makeText(
                                    context,
                                    "Please sign in first",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            Icon(
                                imageVector = if (currentEpisode.isWishlistedSeries) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = stringResource(id = R.string.desc_bookmark),
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // Episode
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { showEpisodeSheet = true }) {
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
                            com.dramzz.reels_pix.utils.IntentUtils.shareSeries(
                                context,
                                currentEpisode.title
                            )
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
            } // end inner page Box
        } // end VerticalPager

        if (showEpisodeSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showEpisodeSheet = false },
                    containerColor = Color(0xFF1E1E1E),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
                ) {
                    val dummyReel = FeedSeries(
                        id = seriesId,
                        title = seriesTitle,
                        slug = "",
                        description = "",
                        coverImage = null,
                        status = "",
                        totalEpisodes = episodes.size,
                        episodeLockedAfterNumber = 0,
                        defaultUnlockCostEpisode = 0,
                        categories = emptyList(),
                        isWishlisted = false,
                        episodes = episodes
                    )
                    EpisodeBottomSheetContent(
                        reel = dummyReel,
                        selectedEpisodeNumber = if (episodes.isNotEmpty()) episodes[pagerState.currentPage].episodeNumber else 1,
                        onDismiss = { showEpisodeSheet = false },
                        onEpisodeSelected = { isLocked, episodeNum ->
                            showEpisodeSheet = false
                            if (isLocked) {
                                onLockedEpisodeClick()
                            } else {
                                coroutineScope.launch {
                                    val targetIndex =
                                        episodes.indexOfFirst { it.episodeNumber == episodeNum }
                                    if (targetIndex >= 0) {
                                        pagerState.scrollToPage(targetIndex)
                                    }
                                }
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
                text = stringResource(id = R.string.video_settings),
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
                    contentDescription = stringResource(id = R.string.desc_close),
                    tint = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Quality
        Text(stringResource(id = R.string.settings_quality), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val qualities = listOf(stringResource(id = R.string.video_quality_auto), "1080p", "720p", "480p")
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
            Text(stringResource(id = R.string.settings_subtitles), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
            val subtitles = listOf(
                stringResource(id = R.string.video_lang_english), 
                stringResource(id = R.string.video_lang_espanol), 
                stringResource(id = R.string.video_lang_portugues), 
                stringResource(id = R.string.video_lang_turkce)
            )
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
        Text(stringResource(id = R.string.settings_playback_speed), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
