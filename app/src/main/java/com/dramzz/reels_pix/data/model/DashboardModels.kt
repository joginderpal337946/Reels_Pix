package com.dramzz.reels_pix.data.model

import com.dramzz.reels_pix.R

data class Movie(
    val id: String,
    val title: String,
    val imageUrl: String,
    val tag: String? = null, // e.g., "NEW", "FREE", null
    val viewCount: String? = null, // e.g., "130K", "17.9K", null
    val description: String? = null,
    val isWishlisted: Boolean? = null,
    val initialEpisodeNumber: Int? = null
)

data class HomeSection(
    val id: Int?,
    val title: String,
    val movies: List<Movie>
)

fun FeedSeries.toMovie(initialEpisodeNumber: Int? = null): Movie {
    return Movie(
        id = id.toString(),
        title = title,
        imageUrl = coverImage ?: "",
        tag = null, 
        viewCount = null,
        description = description,
        isWishlisted = isWishlisted,
        initialEpisodeNumber = initialEpisodeNumber
    )
}
data class HistoryItem(
    val id: String,
    val title: String,
    val progress: String,
    val imageUrl: String,
    val isWishlisted: Boolean
)

fun ViewingHistoryItem.toHistoryItem(): HistoryItem? {
    if (this.series == null) return null
    return HistoryItem(
        id = this.series.id.toString(),
        title = this.series.title,
        progress = "${this.episodeNumber}/${this.series.totalEpisodes}",
        imageUrl = this.series.coverImage ?: "",
        isWishlisted = this.series.isWishlisted
    )
}

fun ViewingHistoryItem.toMovie(): Movie? {
    if (this.series == null) return null
    return Movie(
        id = this.series.id.toString(),
        title = this.series.title,
        imageUrl = this.series.coverImage ?: "",
        description = this.series.description,
        isWishlisted = this.series.isWishlisted,
        initialEpisodeNumber = this.episodeNumber
    )
}

data class SubscriptionPlan(
    val durationRes: Int,
    val price: String,
    val iconType: String // "play", "diamond", "crown"
)

data class TopUpPackage(
    val baseCoins: Int,
    val bonusCoins: Int,
    val bonusTag: String,
    val price: String
)

object AwardsMockData {

    val subscriptions = listOf(
        SubscriptionPlan(R.string.sub_1_year, "₹10,600.00", "play"),
        SubscriptionPlan(R.string.sub_1_month, "₹1,050.00", "diamond"),
        SubscriptionPlan(R.string.sub_1_week, "₹370.00", "crown")
    )

    val topUpPackages = listOf(
        TopUpPackage(500, 0, "", "₹110.00"),
        TopUpPackage(1000, 100, "+10%", "₹210.00"),
        TopUpPackage(2000, 400, "+20%", "₹370.00"),
        TopUpPackage(3000, 900, "+30%", "₹520.00"),
        TopUpPackage(5000, 2500, "+50%", "₹850.00"),
        TopUpPackage(10000, 10000, "+100%", "₹1,650.00")
    )
}
