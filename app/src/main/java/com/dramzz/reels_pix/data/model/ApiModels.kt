package com.dramzz.reels_pix.data.model

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?
)

data class GenericResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("userId") val userId: String
)

data class UserProfile(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("avatarUrl") val avatarUrl: String?
)

data class UpdateProfileRequest(
    @SerializedName("name") val name: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?
)

data class RegisterData(
    @SerializedName("id") val id: Int,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("profile_picture") val profilePicture: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("token_type") val tokenType: String?,
    @SerializedName("referral_code") val referralCode: String?,
    @SerializedName("coins") val coins: String?
)

data class RewardResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: RewardData?
)

data class RewardData(
    @SerializedName("balance") val balance: Int,
    @SerializedName("daily_streak") val dailyStreak: Int,
    @SerializedName("daily_rewards") val dailyRewards: List<DailyReward>,
    @SerializedName("other_rewards") val otherRewards: List<OtherReward>
)

data class DailyReward(
    @SerializedName("id") val id: Int,
    @SerializedName("key") val key: String,
    @SerializedName("title") val title: String,
    @SerializedName("coins") val coins: Int,
    @SerializedName("day_number") val dayNumber: Int,
    @SerializedName("status") val status: String
)

data class OtherReward(
    @SerializedName("id") val id: Int,
    @SerializedName("key") val key: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("coins") val coins: Int,
    @SerializedName("type") val type: String,
    @SerializedName("is_claimed") val isClaimed: Boolean
)

data class FeedResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<FeedSeries>?
)

data class FeedSeries(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("description") val description: String,
    @SerializedName("cover_image") val coverImage: String?,
    @SerializedName("status") val status: String,
    @SerializedName("total_episodes") val totalEpisodes: Int,
    @SerializedName("episode_locked_after_number") val episodeLockedAfterNumber: Int,
    @SerializedName("default_unlock_cost_episode") val defaultUnlockCostEpisode: Int,
    @SerializedName("categories") val categories: List<FeedCategory>? = emptyList(),
    @SerializedName("is_wishlisted") val isWishlisted: Boolean,
    @SerializedName("episodes") val episodes: List<FeedEpisode>? = emptyList()
)

data class FeedCategory(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class FeedEpisode(
    @SerializedName("id") val id: Int,
    @SerializedName("series_id") val seriesId: Int,
    @SerializedName("episode_number") val episodeNumber: Int,
    @SerializedName("title") val title: String,
    @SerializedName("cover_image") val coverImage: String?,
    @SerializedName("is_locked") val isLocked: Boolean,
    @SerializedName("has_unlocked") val hasUnlocked: Boolean,
    @SerializedName("unlock_cost") val unlockCost: Int,
    @SerializedName("views_count") val viewsCount: Int,
    @SerializedName("free_at") val freeAt: String?,
    @SerializedName("published_at") val publishedAt: String?,
    @SerializedName("is_favourite") val isFavourite: Boolean,
    @SerializedName("is_wishlisted_series") val isWishlistedSeries: Boolean,
    @SerializedName("is_unlocked_episode") val isUnlockedEpisode: Boolean,
    @SerializedName("unlocked_by") val unlockedBy: String?,
    @SerializedName("content") val content: String?
)

data class HomeResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: HomeData
)

data class HomeData(
    @SerializedName("continue_watching") val continueWatching: List<ViewingHistoryItem>?,
    @SerializedName("featured_series") val featuredSeries: List<FeedSeries>?,
    @SerializedName("new_release") val newRelease: List<FeedSeries>?,
    @SerializedName("editors_choice") val editorsChoice: List<FeedSeries>?,
    @SerializedName("originals") val originals: List<FeedSeries>?,
    @SerializedName("gripping_plot") val grippingPlot: List<FeedSeries>?,
    @SerializedName("love_and_pain") val loveAndPain: List<FeedSeries>?,
    @SerializedName("happy_end") val happyEnd: List<FeedSeries>?,
    @SerializedName("still_waters_run_deep") val stillWatersRunDeep: List<FeedSeries>?,
    @SerializedName("fatal_attraction") val fatalAttraction: List<FeedSeries>?
)

data class PaginatedSeriesResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: PaginatedSeriesData
)

data class PaginatedSeriesData(
    @SerializedName("data") val seriesList: List<FeedSeries>,
    @SerializedName("meta") val meta: PaginationMeta?
)

data class PaginationMeta(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("last_page") val lastPage: Int
)

data class PaginatedEpisodesResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: PaginatedEpisodesData
)

data class PaginatedEpisodesData(
    @SerializedName("data") val episodesList: List<FeedEpisode>,
    @SerializedName("meta") val meta: PaginationMeta?
)

data class ViewingHistoryResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<ViewingHistoryItem>?
)

data class ViewingHistoryItem(
    @SerializedName("id") val id: Int,
    @SerializedName("series_id") val seriesId: Int,
    @SerializedName("episode_id") val episodeId: Int,
    @SerializedName("episode_number") val episodeNumber: Int,
    @SerializedName("series") val series: HistorySeries?,
    @SerializedName("episode") val episode: FeedEpisode?
)

data class HistorySeries(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("description") val description: String,
    @SerializedName("cover_image") val coverImage: String?,
    @SerializedName("status") val status: String,
    @SerializedName("total_episodes") val totalEpisodes: Int,
    @SerializedName("episode_locked_after_number") val episodeLockedAfterNumber: Int,
    @SerializedName("default_unlock_cost_episode") val defaultUnlockCostEpisode: Int,
    @SerializedName("is_wishlisted") val isWishlisted: Boolean
)

data class PaginatedContinueWatchingResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: PaginatedContinueWatchingData?
)

data class PaginatedContinueWatchingData(
    @SerializedName("data") val data: List<ViewingHistoryItem>?,
    @SerializedName("meta") val meta: PaginationMeta?
)
