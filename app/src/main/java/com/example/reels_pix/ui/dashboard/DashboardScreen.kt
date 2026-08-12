package com.example.reels_pix.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reels_pix.R
import com.example.reels_pix.data.model.MockData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    var selectedBottomTab by remember { mutableStateOf(0) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var awardsTabIndex by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    
    var showSignIn by remember { mutableStateOf(false) }
    var showWallet by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var showLanguage by remember { mutableStateOf(false) }
    var showInvitationCode by remember { mutableStateOf(false) }
    var selectedCategoryForDetail by remember { mutableStateOf<String?>(null) }
    var playingVideoTitle by remember { mutableStateOf<String?>(null) }
    var showSearchScreen by remember { mutableStateOf(false) }
    var showTopUpSheet by remember { mutableStateOf(false) }
    
    val navigateToAwardsShop = {
        awardsTabIndex = 1
        selectedBottomTab = 3
        showWallet = false
        showHistory = false
        playingVideoTitle = null
        showSearchScreen = false
    }
    
    val categories = MockData.categories
    
    if (showSearchScreen) {
        SearchScreen(
            onCancel = { showSearchScreen = false },
            onMovieClick = { movie -> 
                playingVideoTitle = movie.title
                showSearchScreen = false
            }
        )
    } else if (showSignIn) {
        SignInScreen(onBackClick = { showSignIn = false })
    } else if (showHistory) {
        TransactionHistoryScreen(onBackClick = { showHistory = false }, onFindOutMoreClick = navigateToAwardsShop)
    } else if (showLanguage) {
        LanguageScreen(onBackClick = { showLanguage = false })
    } else if (showInvitationCode) {
        InvitationCodeScreen(onBackClick = { showInvitationCode = false })
    } else if (selectedCategoryForDetail != null) {
        CategoryDetailScreen(
            categoryName = selectedCategoryForDetail!!,
            onBackClick = { selectedCategoryForDetail = null }
        )
    } else if (showWallet) {
        MyWalletScreen(
            onBackClick = { showWallet = false },
            onHistoryClick = { showHistory = true },
            onTopUpClick = navigateToAwardsShop
        )
    } else if (playingVideoTitle != null) {
        VideoPlayerScreen(
            seriesTitle = playingVideoTitle!!,
            onBackClick = { playingVideoTitle = null },
            onLockedEpisodeClick = { showTopUpSheet = true }
        )
    } else {
        Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF121212),
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    icon = { Icon(painter = androidx.compose.ui.res.painterResource(id = R.drawable.home_icon), contentDescription = "Home", modifier = Modifier.size(24.dp)) },
                    label = { Text("Home") },
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
                    icon = { Icon(painter = androidx.compose.ui.res.painterResource(id = R.drawable.feed_icon), contentDescription = "Feed", modifier = Modifier.size(24.dp)) },
                    label = { Text("Feed") },
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
                    icon = { Icon(painter = androidx.compose.ui.res.painterResource(id = R.drawable.my_list_icon), contentDescription = "My list", modifier = Modifier.size(24.dp)) },
                    label = { Text("My list") },
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
                    icon = { Icon(painter = androidx.compose.ui.res.painterResource(id = R.drawable.awards_icon), contentDescription = "Awards", modifier = Modifier.size(24.dp), tint = Color.Unspecified) },
                    label = { Text("Awards") },
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
                    icon = { Icon(painter = androidx.compose.ui.res.painterResource(id = R.drawable.profile_icon), contentDescription = "Profile", modifier = Modifier.size(24.dp)) },
                    label = { Text("Profile") },
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
        },
        containerColor = Color(0xFF121212)
    ) { paddingValues ->
        if (selectedBottomTab == 0) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Search...", color = Color.Gray, fontSize = 16.sp)
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
                    divider = {}
                ) {
                    categories.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    color = if (selectedTabIndex == index) Color.White else Color.Gray,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        )
                    }
                }
                
                // Content
                val currentCategory = categories[selectedTabIndex]
                val sections = remember(currentCategory) { MockData.getMockSections(currentCategory) }
                
                if (selectedTabIndex == 0) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        item {
                            FeaturedCarousel(
                                movies = MockData.getMockSections("All").first().movies,
                                onMovieClick = { movie -> playingVideoTitle = movie.title }
                            )
                        }
                        items(sections) { section ->
                            MovieSection(
                                section = section,
                                onSeeAllClick = { selectedCategoryForDetail = section.title },
                                onMovieClick = { movie -> playingVideoTitle = movie.title }
                            )
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
                                    .clickable { selectedCategoryForDetail = currentCategory }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentCategory,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    color = Color.White
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "See All",
                                    tint = Color.White
                                )
                            }
                        }
                        
                        val allMovies = sections.flatMap { it.movies }
                        
                        items(allMovies.chunked(2)) { rowMovies ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            ) {
                                rowMovies.forEach { movie ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        LargeGridMovieCard(
                                            movie = movie,
                                            onClick = { playingVideoTitle = movie.title }
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

        } else if (selectedBottomTab == 1) {
            Box(modifier = Modifier.padding(paddingValues)) {
                FeedScreen(
                    onWatchNowClick = { title -> playingVideoTitle = title },
                    onSearchClick = { showSearchScreen = true },
                    onLockedEpisodeClick = { showTopUpSheet = true }
                )
            }
        } else if (selectedBottomTab == 2) {
            Box(modifier = Modifier.padding(paddingValues)) {
                MyListScreen(
                    onOpenMoreClick = { selectedBottomTab = 1 },
                    onHistoryItemClick = { title -> playingVideoTitle = title }
                )
            }
        } else if (selectedBottomTab == 3) {
            Box(modifier = Modifier.padding(paddingValues)) {
                AwardsScreen(
                    selectedTabIndex = awardsTabIndex,
                    onTabSelected = { awardsTabIndex = it }
                )
            }
        } else if (selectedBottomTab == 4) {
            Box(modifier = Modifier.padding(paddingValues)) {
                ProfileScreen(
                    onSignInClick = { showSignIn = true },
                    onWalletClick = { showWallet = true },
                    onTopUpClick = navigateToAwardsShop,
                    onLanguageClick = { showLanguage = true },
                    onInvitationClick = { showInvitationCode = true }
                )
            }
        } else {
            // Other empty tabs
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Content for Tab $selectedBottomTab coming soon", color = Color.White)
            }
        }
    }
    }

    if (showTopUpSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTopUpSheet = false },
            containerColor = Color(0xFF1E1E1E),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
        ) {
            TopUpBottomSheetContent(
                onDismiss = { showTopUpSheet = false },
                onViewAllClick = {
                    showTopUpSheet = false
                    navigateToAwardsShop()
                }
            )
        }
    }
}
