package com.example.reels_pix.data.model

data class Movie(
    val id: String,
    val title: String,
    val imageUrl: String,
    val tag: String? = null, // e.g., "NEW", "FREE", null
    val viewCount: String? = null, // e.g., "130K", "17.9K", null
    val description: String? = null
)

data class HomeSection(
    val title: String,
    val movies: List<Movie>
)

object MockData {
    val categories = listOf(
        "All", "Top 10 \uD83C\uDFC6", "Hot \uD83D\uDD25", "Cinderella \uD83C\uDFF0", 
        "Me+Boss", "Arranged Marriage", "Money and Power", 
        "Dangerous Liaisons", "Second Chance"
    )

    fun getMockSections(category: String): List<HomeSection> {
        return listOf(
            HomeSection(
                title = "Love and Pain",
                movies = listOf(
                    Movie("1", "Help! I'm Falling In Love...", "https://picsum.photos/seed/${category}1/300/450", null, "130K", "Hailey takes a job as secretary to a rude CEO who has fired five people in a month..."),
                    Movie("2", "Conflicted Hearts", "https://picsum.photos/seed/${category}2/300/450", "NEW", null, "A story about conflicted hearts and tough choices."),
                    Movie("3", "The Escaping Mistress", "https://picsum.photos/seed/${category}3/300/450", "NEW", null, "She tried to escape, but he wouldn't let her go."),
                    Movie("4", "Her Safe Word is LOCK", "https://picsum.photos/seed/${category}4/300/450", "FREE", "37.7K", "A thrilling romance of danger and passion.")
                )
            ),
            HomeSection(
                title = "Happy End",
                movies = listOf(
                    Movie("5", "Love Lies and Bloodline", "https://picsum.photos/seed/${category}5/300/450", "NEW", null, "Secrets and bloodlines entwine in this epic saga."),
                    Movie("6", "Honey Gold", "https://picsum.photos/seed/${category}6/300/450", "NEW", null, "Sweet as honey, precious as gold."),
                    Movie("7", "Marry The Wrong Bride", "https://picsum.photos/seed/${category}7/300/450", null, "170K", "Betrothed since childhood to a man she's never met, Bianca tricks her sister into taking her place."),
                    Movie("8", "Cleopatra's Heart", "https://picsum.photos/seed/${category}8/300/450", null, null, "A historical romance that defies time.")
                )
            ),
            HomeSection(
                title = "Still Waters Run Deep",
                movies = listOf(
                    Movie("9", "Bought by my enemy", "https://picsum.photos/seed/${category}9/300/450", "FREE", "19.6K"),
                    Movie("10", "Inheritance Games", "https://picsum.photos/seed/${category}10/300/450", null, "26.3K"),
                    Movie("11", "I can hear my boss's thoughts", "https://picsum.photos/seed/${category}11/300/450", null, "15.8K")
                )
            ),
            HomeSection(
                title = "New release",
                movies = listOf(
                    Movie("12", "Help me, DOCTOR", "https://picsum.photos/seed/${category}12/300/450", "FREE", "17.9K"),
                    Movie("13", "The Stroganoff Legacy", "https://picsum.photos/seed/${category}13/300/450", "FREE", "13.2K"),
                    Movie("14", "Medici Love Affairs", "https://picsum.photos/seed/${category}14/300/450", null, "10.3K")
                )
            )
        )
    }

    val historyItems = listOf(
        HistoryItem("1", "Fisherman Ceo", "2/22", "https://picsum.photos/seed/hist1/300/450"),
        HistoryItem("2", "The Stroganoff Legacy", "1/9", "https://picsum.photos/seed/hist2/300/450"),
        HistoryItem("3", "Marry The Wrong Bride", "1/50", "https://picsum.photos/seed/hist3/300/450"),
        HistoryItem("4", "Married to My Brother's Ex", "1/62", "https://picsum.photos/seed/hist4/300/450")
    )
}

data class HistoryItem(
    val id: String,
    val title: String,
    val progress: String,
    val imageUrl: String
)

data class RewardTask(
    val title: String,
    val description: String,
    val reward: Int
)

data class SubscriptionPlan(
    val duration: String,
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
    val tasks = listOf(
        RewardTask("Watch an Ad", "To earn coins", 10),
        RewardTask("The reward for registration", "Create an account in the app and get rewarded!", 50),
        RewardTask("Turn on push notifications", "Turn on push notifications and get rewarded!", 50),
        RewardTask("Follow us on Facebook", "", 10),
        RewardTask("Follow us on TikTok", "", 10),
        RewardTask("Follow us on Instagram", "", 10),
        RewardTask("Follow us on Telegram", "", 10)
    )

    val subscriptions = listOf(
        SubscriptionPlan("1 year", "₹10,600.00", "play"),
        SubscriptionPlan("1 month", "₹1,050.00", "diamond"),
        SubscriptionPlan("1 week", "₹370.00", "crown")
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
