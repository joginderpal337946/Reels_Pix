package com.dramzz.reels_pix.ui.dashboard.mylist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import com.dramzz.reels_pix.R
import com.dramzz.reels_pix.data.model.HistoryItem
import com.dramzz.reels_pix.data.model.Movie
import com.dramzz.reels_pix.data.model.toHistoryItem
import com.dramzz.reels_pix.data.model.toMovie
import com.dramzz.reels_pix.ui.dashboard.home.LargeGridMovieCard
import com.dramzz.reels_pix.ui.dashboard.profile.ProfileViewModel
import com.dramzz.reels_pix.utils.CustomToast.showError
import org.koin.androidx.compose.koinViewModel

@Composable
fun MyListScreen(
    onOpenMoreClick: () -> Unit = {},
    onHistoryItemClick: (Movie) -> Unit = {},
    historyViewModel: HistoryViewModel = koinViewModel(),
    onSignInClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
    savedSeriesViewModel: SavedSeriesViewModel = koinViewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.checkLoginStatus()
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            savedSeriesViewModel.fetchSavedSeries(isRefresh = true)
            historyViewModel.fetchHistory()
        }
    }

    var selectedTabIndex by remember { mutableStateOf(0) } // Default to Favorites (0)
    val tabs = listOf(
        stringResource(id = R.string.tab_favorites),
        stringResource(id = R.string.tab_history)
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFF121212))
                .let { if (!isLoggedIn) it.blur(16.dp) else it }) {
            // Top Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFF121212),
                contentColor = Color.White,
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Color(0xFF673AB7), // Purple indicator
                            height = 3.dp
                        )
                    }
                },
                divider = {}) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTabIndex == index) Color.White else Color.Gray,
                                style = MaterialTheme.typography.titleMedium
                            )
                        })
                }
            }

            // Content
            if (selectedTabIndex == 0) {
                val savedSeries by savedSeriesViewModel.series.collectAsState()
                val isLoading by savedSeriesViewModel.isLoading.collectAsState()
                val isPaginating by savedSeriesViewModel.isPaginating.collectAsState()

                LaunchedEffect(isLoggedIn) {
                    if (isLoggedIn) {
                        savedSeriesViewModel.fetchSavedSeries(isRefresh = true)
                    }
                }

                if (isLoading && savedSeries.isEmpty()) {
                    // Initial load — show centered circular loader
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else if (savedSeries.isEmpty() && !isLoading) {
                    FavoritesEmptyState(onOpenMoreClick = onOpenMoreClick)
                } else {
                    val listState = rememberLazyGridState()

                    LaunchedEffect(listState.firstVisibleItemIndex) {
                        val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                        if (lastVisibleItem != null && lastVisibleItem.index >= savedSeries.size - 4 && savedSeriesViewModel.hasMore) {
                            savedSeriesViewModel.fetchSavedSeries()
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(savedSeries) { series ->
                            LargeGridMovieCard(
                                movie = series.toMovie(),
                                onClick = { onHistoryItemClick(series.toMovie()) },
                                onBookmarkClick = {
                                    if (!savedSeriesViewModel.toggleWishlist(series.id)) {
                                        android.widget.Toast.makeText(
                                            context,
                                            "Please sign in first",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                        }
                        if (isPaginating) {
                            item {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                val historyResponseItems by historyViewModel.historyItems.collectAsState()
                val historyIsLoading by historyViewModel.isLoading.collectAsState()
                val historyItems = historyResponseItems.mapNotNull { it.toHistoryItem() }
                if (historyIsLoading && historyItems.isEmpty()) {
                    // Initial load — show centered circular loader
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else if (historyItems.isEmpty()) {
                    HistoryEmptyState(onOpenMoreClick = onOpenMoreClick)
                } else {
                    HistoryList(items = historyItems, onHistoryItemClick = { title ->
                        val matchingItem = historyResponseItems.find { it.series?.title == title }
                        matchingItem?.toMovie()?.let { movie ->
                            onHistoryItemClick(movie)
                        }
                    }, onBookmarkClick = { seriesId ->
                        if (!historyViewModel.toggleWishlist(seriesId)) {
                            showError(
                                context,
                                "Please sign in first"
                            )
                        }
                    })
                }
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
                    ) { }, contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onSignInClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(50.dp)
                ) {
                    Text(
                        stringResource(id = R.string.profile_sign_in),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FavoritesEmptyState(onOpenMoreClick: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.BookmarkBorder,
            contentDescription = stringResource(id = R.string.desc_empty_favorites),
            tint = Color.Gray,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.my_list_empty), color = Color.Gray, fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onOpenMoreClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(48.dp)
        ) {
            Text(
                stringResource(id = R.string.my_list_open_more),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HistoryEmptyState(onOpenMoreClick: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = "Empty History",
            tint = Color.Gray,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No history available", color = Color.Gray, fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onOpenMoreClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(48.dp)
        ) {
            Text(
                stringResource(id = R.string.my_list_open_more),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HistoryList(
    items: List<HistoryItem>,
    onHistoryItemClick: (String) -> Unit = {},
    onBookmarkClick: (Int) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
    ) {
        items(items) { item ->
            HistoryListItem(
                item = item,
                onClick = { onHistoryItemClick(item.title) },
                onBookmarkClick = { onBookmarkClick(item.id.toIntOrNull() ?: 0) })
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HistoryListItem(item: HistoryItem, onClick: () -> Unit = {}, onBookmarkClick: () -> Unit = {}) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        // Poster Image
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(80.dp)
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Text Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.progress, color = Color.Gray, fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Bookmark Icon
        IconButton(onClick = onBookmarkClick) {
            Icon(
                imageVector = if (item.isWishlisted) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = stringResource(id = R.string.desc_save),
                tint = if (item.isWishlisted) Color.Red else Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
