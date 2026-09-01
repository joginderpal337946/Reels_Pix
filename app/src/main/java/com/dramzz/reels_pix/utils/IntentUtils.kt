package com.dramzz.reels_pix.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

object IntentUtils {
    fun openEmail(context: Context, emailAddress: String, subject: String = "", body: String = "") {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareApp(context: Context, playStoreLink: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out this app!")
            putExtra(Intent.EXTRA_TEXT, "Hey, check out app: $playStoreLink")
        }
        try {
            context.startActivity(Intent.createChooser(intent, "Share via"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareSeries(context: Context, seriesTitle: String, seriesDescription: String? = null) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out this series: $seriesTitle")
            
            var bodyText = "I'm watching '$seriesTitle' on Dramzz. You should check it out!"
            if (!seriesDescription.isNullOrEmpty()) {
                bodyText += "\n\n$seriesDescription"
            }
            
            putExtra(Intent.EXTRA_TEXT, bodyText)
        }
        try {
            context.startActivity(Intent.createChooser(intent, "Share via"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareReferralCode(context: Context, referralCode: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Join Dramzz!")
            putExtra(Intent.EXTRA_TEXT, "Use my referral code $referralCode when you sign up on Dramzz to get free rewards!")
        }
        try {
            context.startActivity(Intent.createChooser(intent, "Share via"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
