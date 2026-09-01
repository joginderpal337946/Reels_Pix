package com.dramzz.reels_pix.ui.dashboard.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dramzz.reels_pix.R
import com.dramzz.reels_pix.data.model.HomeSection
import com.dramzz.reels_pix.data.model.ViewingHistoryItem
import com.dramzz.reels_pix.data.model.toMovie
import com.dramzz.reels_pix.ui.dashboard.awards.AwardsScreen
import com.dramzz.reels_pix.ui.dashboard.category.CategoryDetailScreen
import com.dramzz.reels_pix.ui.dashboard.components.TopUpBottomSheetContent
import com.dramzz.reels_pix.ui.dashboard.components.WebViewScreen
import com.dramzz.reels_pix.ui.dashboard.feed.FeedScreen
import com.dramzz.reels_pix.ui.dashboard.mylist.MyListScreen
import com.dramzz.reels_pix.ui.dashboard.profile.InvitationCodeScreen
import com.dramzz.reels_pix.ui.dashboard.profile.LanguageScreen
import com.dramzz.reels_pix.ui.dashboard.profile.ProfileScreen
import com.dramzz.reels_pix.ui.dashboard.profile.ProfileViewModel
import com.dramzz.reels_pix.ui.dashboard.profile.SignInScreen
import com.dramzz.reels_pix.ui.dashboard.search.SearchScreen
import com.dramzz.reels_pix.ui.dashboard.videoplayer.VideoPlayerScreen
import com.dramzz.reels_pix.ui.dashboard.wallet.MyWalletScreen
import com.dramzz.reels_pix.ui.dashboard.wallet.TransactionHistoryScreen
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material.icons.filled.KeyboardArrowRight
import com.dramzz.reels_pix.data.model.Movie
import com.dramzz.reels_pix.utils.CustomToast.showError


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: HomeViewModel = koinViewModel(),
    categoryViewModel: com.dramzz.reels_pix.ui.dashboard.category.CategoryViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val isLoggedIn by profileViewModel.isLoggedIn.collectAsState()
    LaunchedEffect(Unit) {
        profileViewModel.checkLoginStatus()
    }
    val homeData by viewModel.homeData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchHomeData()
    }

    var selectedBottomTab by remember { mutableStateOf(0) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var awardsTabIndex by remember { mutableStateOf(0) }

    var showSignIn by remember { mutableStateOf(false) }
    var showWallet by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var showLanguage by remember { mutableStateOf(false) }
    var showInvitationCode by remember { mutableStateOf(false) }
    var selectedCategoryForDetail by remember { mutableStateOf<HomeSection?>(null) }
    var playingVideo by remember { mutableStateOf<Movie?>(null) }
    var showSearchScreen by remember { mutableStateOf(false) }
    var showTopUpSheet by remember { mutableStateOf(false) }
    var userDismissedPopup by remember { mutableStateOf(false) }

    var webViewUrl by remember { mutableStateOf<String?>(null) }
    var webViewTitle by remember { mutableStateOf("") }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        com.dramzz.reels_pix.utils.AuthEventBus.logoutEvent.collect {
            showError(
                context,
                "Session expired. Please log in again."
            )
            showTopUpSheet = false
            showSearchScreen = false
            showHistory = false
            showWallet = false
            webViewUrl = null
            showLanguage = false
            showInvitationCode = false
            selectedCategoryForDetail = null
            playingVideo = null

            selectedBottomTab = 4
            showSignIn = true
        }
    }

    BackHandler(
        enabled = webViewUrl != null || showSearchScreen || showSignIn || showHistory || showLanguage || showInvitationCode || selectedCategoryForDetail != null || showWallet || playingVideo != null || selectedBottomTab != 0
    ) {
        when {
            webViewUrl != null -> webViewUrl = null
            showSearchScreen -> showSearchScreen = false
            showSignIn -> showSignIn = false
            showHistory -> showHistory = false
            showLanguage -> showLanguage = false
            showInvitationCode -> showInvitationCode = false
            selectedCategoryForDetail != null -> selectedCategoryForDetail = null
            showWallet -> showWallet = false
            playingVideo != null -> playingVideo = null
            selectedBottomTab != 0 -> selectedBottomTab = 0
        }
    }

    val navigateToAwardsShop = {
        awardsTabIndex = 1
        selectedBottomTab = 3
        showWallet = false
        showHistory = false
        showSearchScreen = false
    }

    val sections = remember(homeData) {
        val list = mutableListOf<HomeSection>()
        homeData?.let { data ->
            data.continueWatching?.takeIf { it.isNotEmpty() }?.let {
                list.add(
                    HomeSection(
                        null,
                        "Continue Watching",
                        it.mapNotNull { s -> s.toMovie() })
                )
            }
            data.newRelease?.takeIf { it.isNotEmpty() }?.let {
                list.add(
                    HomeSection(
                        it.firstOrNull()?.categories?.firstOrNull()?.id,
                        "New Release",
                        it.map { s -> s.toMovie() })
                )
            }
            data.editorsChoice?.takeIf { it.isNotEmpty() }?.let {
                list.add(
                    HomeSection(
                        it.firstOrNull()?.categories?.firstOrNull()?.id,
                        "Editor's Choice",
                        it.map { s -> s.toMovie() })
                )
            }
            data.originals?.takeIf { it.isNotEmpty() }?.let {
                list.add(
                    HomeSection(
                        it.firstOrNull()?.categories?.firstOrNull()?.id,
                        "Originals",
                        it.map { s -> s.toMovie() })
                )
            }
            data.grippingPlot?.takeIf { it.isNotEmpty() }?.let {
                list.add(
                    HomeSection(
                        it.firstOrNull()?.categories?.firstOrNull()?.id,
                        "Gripping Plot",
                        it.map { s -> s.toMovie() })
                )
            }
            data.loveAndPain?.takeIf { it.isNotEmpty() }?.let {
                list.add(
                    HomeSection(
                        it.firstOrNull()?.categories?.firstOrNull()?.id,
                        "Love and Pain",
                        it.map { s -> s.toMovie() })
                )
            }
            data.happyEnd?.takeIf { it.isNotEmpty() }?.let {
                list.add(
                    HomeSection(
                        it.firstOrNull()?.categories?.firstOrNull()?.id,
                        "Happy End",
                        it.map { s -> s.toMovie() })
                )
            }
            data.stillWatersRunDeep?.takeIf { it.isNotEmpty() }?.let {
                list.add(
                    HomeSection(
                        it.firstOrNull()?.categories?.firstOrNull()?.id,
                        "Still Waters Run Deep",
                        it.map { s -> s.toMovie() })
                )
            }
            data.fatalAttraction?.takeIf { it.isNotEmpty() }?.let {
                list.add(
                    HomeSection(
                        it.firstOrNull()?.categories?.firstOrNull()?.id,
                        "Fatal Attraction",
                        it.map { s -> s.toMovie() })
                )
            }
        }
        list
    }
    
    val featuredMovies = remember(homeData) {
        homeData?.featuredSeries?.map { it.toMovie() } ?: emptyList()
    }
    
    val limitedSections = remember(sections) {
        sections.map { section ->
            section.copy(movies = section.movies.take(5))
        }
    }

    val genres = listOf(
        "All",
        "Top 10",
        "Hot",
        "Cindrella",
        "Me + Boss",
        "Arranged Marriage",
        "Money and Power",
        "Dangerous Liaisons",
        "Second Chance"
    )

    if (showSearchScreen) {
        SearchScreen(onCancel = { showSearchScreen = false }, onMovieClick = { movie ->
            playingVideo = movie
            showSearchScreen = false
        })
    } else if (showSignIn) {
        SignInScreen(onBackClick = { showSignIn = false }, onSignInSuccess = {
            showSignIn = false
            selectedBottomTab = 4
        }, onWebViewClick = { url, title ->
            webViewUrl = url
            webViewTitle = title
        })
    } else if (showHistory) {
        TransactionHistoryScreen(
            onBackClick = { showHistory = false },
            onFindOutMoreClick = navigateToAwardsShop
        )
    } else if (showLanguage) {
        LanguageScreen(onBackClick = { showLanguage = false })
    } else if (showInvitationCode) {
        InvitationCodeScreen(onBackClick = { showInvitationCode = false })
    } else if (selectedCategoryForDetail != null) {
        CategoryDetailScreen(
            categoryId = selectedCategoryForDetail!!.id,
            genre = null,
            title = selectedCategoryForDetail!!.title,
            onBackClick = { selectedCategoryForDetail = null })
    } else if (showWallet) {
        MyWalletScreen(
            onBackClick = { showWallet = false },
            onHistoryClick = { showHistory = true },
            onTopUpClick = navigateToAwardsShop,
            onSignInClick = { showSignIn = true })
    } else if (playingVideo != null) {
        VideoPlayerScreen(
            seriesId = playingVideo!!.id.toIntOrNull() ?: 0,
            seriesTitle = playingVideo!!.title,
            initialEpisodeNumber = playingVideo!!.initialEpisodeNumber,
            onBackClick = { maxEpisode ->
                playingVideo?.id?.toIntOrNull()?.let { id ->
                    viewModel.updateContinueWatchingEpisode(seriesId = id, episodeNumber = maxEpisode)
                }
                playingVideo = null
                viewModel.fetchHomeData()
            },
            onLockedEpisodeClick = { 
                if (isLoggedIn) showTopUpSheet = true else showSignIn = true 
            })
    } else if (webViewUrl != null) {
        WebViewScreen(
            url = webViewUrl!!, title = webViewTitle, onBackClick = { webViewUrl = null })
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF121212), contentColor = Color.White
                ) {
                    NavigationBarItem(
                        icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.home_icon),
                            contentDescription = stringResource(id = R.string.nav_home),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                        label = { Text(stringResource(id = R.string.nav_home)) },
                        selected = selectedBottomTab == 0,
                        onClick = { selectedBottomTab = 0 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                    NavigationBarItem(
                        icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.feed_icon),
                            contentDescription = stringResource(id = R.string.nav_feed),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                        label = { Text(stringResource(id = R.string.nav_feed)) },
                        selected = selectedBottomTab == 1,
                        onClick = { selectedBottomTab = 1 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                    NavigationBarItem(
                        icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.my_list_icon),
                            contentDescription = stringResource(id = R.string.nav_my_list),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                        label = { Text(stringResource(id = R.string.nav_my_list)) },
                        selected = selectedBottomTab == 2,
                        onClick = { selectedBottomTab = 2 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                    NavigationBarItem(
                        icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.awards_icon),
                            contentDescription = stringResource(id = R.string.nav_awards),
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                        label = { Text(stringResource(id = R.string.nav_awards)) },
                        selected = selectedBottomTab == 3,
                        onClick = { selectedBottomTab = 3 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                    NavigationBarItem(
                        icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.profile_icon),
                            contentDescription = stringResource(id = R.string.nav_profile),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                        label = { Text(stringResource(id = R.string.nav_profile)) },
                        selected = selectedBottomTab == 4,
                        onClick = { selectedBottomTab = 4 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }, containerColor = Color(0xFF121212)
        ) { paddingValues ->
            if (selectedBottomTab == 0) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Search Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .height(56.dp)
                                .background(Color(0xFF2C2C2C), RoundedCornerShape(24.dp))
                                .clickable { showSearchScreen = true }
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = stringResource(id = R.string.desc_search),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                stringResource(id = R.string.desc_search) + "...",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }

                        // Categories Tab Row
                        ScrollableTabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = Color(0xFF121212),
                            contentColor = Color.White,
                            edgePadding = 16.dp,
                            indicator = { tabPositions ->
                                if (selectedTabIndex < tabPositions.size) {
                                    TabRowDefaults.Indicator(
                                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                        color = Color(0xFF673AB7), // Purple indicator like in screenshots
                                        height = 3.dp
                                    )
                                }
                            },
                            divider = {}) {
                            genres.forEachIndexed { index, title ->
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
                        val currentGenre = genres.getOrNull(selectedTabIndex) ?: "All"

                        val genreMovies by categoryViewModel.movies.collectAsState()
                        val isGenreLoading by categoryViewModel.isLoading.collectAsState()

                        LaunchedEffect(currentGenre) {
                            if (selectedTabIndex != 0) {
                                categoryViewModel.fetchByGenre(currentGenre, isRefresh = true)
                            }
                        }

                        if (isLoading && sections.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color.White)
                            }
                        } else if (selectedTabIndex == 0) {
                            val listState = androidx.compose.foundation.lazy.rememberLazyListState()
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                item(key = "featured_carousel", contentType = "carousel") {
                                    if (featuredMovies.isNotEmpty()) {
                                        FeaturedCarousel(
                                            movies = featuredMovies,
                                            onMovieClick = { movie -> playingVideo = movie })
                                    }
                                }
                                items(
                                    items = limitedSections,
                                    key = { section -> section.title },
                                    contentType = { "movie_section" }
                                ) { section ->
                                    MovieSection(
                                        section = section,
                                        showSeeAll = section.title != "Continue Watching",
                                        onSeeAllClick = { 
                                            val fullSection = sections.find { it.title == section.title } ?: section
                                            selectedCategoryForDetail = fullSection 
                                        },
                                        onMovieClick = { movie -> playingVideo = movie })
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { 
                                                selectedCategoryForDetail = HomeSection(id = null, title = currentGenre, movies = genreMovies)
                                            }
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = currentGenre,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        androidx.compose.material3.Icon(
                                            imageVector = Icons.Filled.KeyboardArrowRight,
                                            contentDescription = "See All",
                                            tint = Color.White
                                        )
                                    }
                                }
                                
                                if (isGenreLoading && genreMovies.isEmpty()) {
                                    item {
                                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(color = Color.White)
                                        }
                                    }
                                } else {
                                    items(genreMovies.chunked(2)) { rowMovies ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp)
                                        ) {
                                            rowMovies.forEach { movie ->
                                                Box(modifier = Modifier.weight(1f)) {
                                                    com.dramzz.reels_pix.ui.dashboard.home.LargeGridMovieCard(
                                                        movie = movie,
                                                        onClick = { playingVideo = movie }
                                                    )
                                                }
                                            }
                                            if (rowMovies.size == 1) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }
                        }


                    }

                    val cwItem by viewModel.continueWatchingItem.collectAsState()

                    if (!userDismissedPopup && cwItem != null) {
                        ContinueWatchingPopup(
                            item = cwItem!!,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp),
                            onDismiss = { userDismissedPopup = true },
                            onClick = { 
                                cwItem?.toMovie()?.let { playingVideo = it } 
                            }
                        )
                    }
                }

            } else if (selectedBottomTab == 1) {
                Box(modifier = Modifier.padding(paddingValues)) {
                    FeedScreen(
                        onWatchNowClick = { movie -> playingVideo = movie },
                        onSearchClick = { showSearchScreen = true },
                        onLockedEpisodeClick = { 
                            if (isLoggedIn) showTopUpSheet = true else showSignIn = true 
                        })
                }
            } else if (selectedBottomTab == 2) {
                Box(modifier = Modifier.padding(paddingValues)) {
                    MyListScreen(
                        onOpenMoreClick = { selectedBottomTab = 1 },
                        onHistoryItemClick = { movie -> playingVideo = movie },
                        onSignInClick = { showSignIn = true })
                }
            } else if (selectedBottomTab == 3) {
                Box(modifier = Modifier.padding(paddingValues)) {
                    AwardsScreen(
                        selectedTabIndex = awardsTabIndex,
                        onTabSelected = { awardsTabIndex = it },
                        onSignInClick = { showSignIn = true },
                        onInvitationClick = { showInvitationCode = true }
                    )
                }
            } else if (selectedBottomTab == 4) {
                Box(modifier = Modifier.padding(paddingValues)) {
                    ProfileScreen(
                        onSignInClick = { showSignIn = true },
                        onWalletClick = { showWallet = true },
                        onTopUpClick = navigateToAwardsShop,
                        onLanguageClick = { showLanguage = true },
                        onInvitationClick = { showInvitationCode = true },
                        onWebViewClick = { url, title ->
                            webViewUrl = url
                            webViewTitle = title
                        })
                }
            } else {
                // Other empty tabs
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(
                            id = R.string.dashboard_coming_soon,
                            selectedBottomTab.toString()
                        ), color = Color.White
                    )
                }
            }
        }
    }

    if (showTopUpSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTopUpSheet = false },
            containerColor = Color(0xFF1E1E1E),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }) {
            TopUpBottomSheetContent(onDismiss = { showTopUpSheet = false }, onViewAllClick = {
                showTopUpSheet = false
                navigateToAwardsShop()
            })
        }
    }
}

@Composable
fun ContinueWatchingPopup(
    item: ViewingHistoryItem, modifier: Modifier = Modifier, onDismiss: () -> Unit, onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color(0xFF5B308A).copy(alpha = 0.95f), RoundedCornerShape(12.dp))
            .clickable { onClick() }) {
        Row(
            modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            coil.compose.AsyncImage(
                model = item.series?.coverImage,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp, 80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Text column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.series?.title ?: "",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.dashboard_continue_watching),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Text(
                    text = stringResource(id = R.string.episode_format, item.episodeNumber),
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }

            // Play Button
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = stringResource(id = R.string.desc_play),
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))
        }

        // Dismiss button
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = stringResource(id = R.string.desc_close),
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
