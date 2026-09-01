package com.dramzz.reels_pix.base.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

interface PermissionHandler {
    fun hasPermission(permission: String): Boolean
}

class PermissionHandlerImpl(private val context: Context) : PermissionHandler {
    override fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}
