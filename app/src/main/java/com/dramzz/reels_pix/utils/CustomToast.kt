package com.dramzz.reels_pix.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

object CustomToast {
    private var currentToast: Toast? = null

    fun showSuccess(context: Context, message: String) {
        showCustomToast(context, message, Color.parseColor("#4CAF50"), 1) // Green
    }

    fun showError(context: Context, message: String) {
        showCustomToast(context, message, Color.parseColor("#F44336"), 2) // Red
    }

    fun showInfo(context: Context, message: String) {
        showCustomToast(context, message, Color.parseColor("#2196F3"), 3) // Blue
    }

    private fun showCustomToast(context: Context, message: String, backgroundColor: Int, iconType: Int) {
        currentToast?.cancel()
        val toast = Toast(context)
        currentToast = toast
        toast.duration = Toast.LENGTH_SHORT
        // Show at the top of the screen
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 50)

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(20, 10, 20, 10)
            background = GradientDrawable().apply {
                setColor(backgroundColor)
                cornerRadius = 100f
            }
        }

        val iconView = ToastIconView(context, iconType).apply {
            layoutParams = LinearLayout.LayoutParams(50, 50).apply {
                marginEnd = 10
            }
        }

        val textView = TextView(context).apply {
            text = message
            setTextColor(Color.WHITE)
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
        }

        layout.addView(iconView)
        layout.addView(textView)

        // Animate icon (pop-in effect)
        iconView.alpha = 0f
        iconView.scaleX = 0f
        iconView.scaleY = 0f
        iconView.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(400)
            .setInterpolator(OvershootInterpolator())
            .start()

        // Animate text (slide-in effect)
        textView.alpha = 0f
        textView.translationX = 30f
        textView.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(300)
            .setStartDelay(100)
            .start()

        toast.view = layout
        toast.show()
    }

    // Custom view to draw the icons (Tick, Cross, Info)
    private class ToastIconView(context: Context, val type: Int) : View(context) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 8f
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val w = width.toFloat()
            val h = height.toFloat()

            when (type) {
                1 -> { // Success (Tick)
                    canvas.drawLine(w * 0.2f, h * 0.5f, w * 0.45f, h * 0.75f, paint)
                    canvas.drawLine(w * 0.45f, h * 0.75f, w * 0.85f, h * 0.25f, paint)
                }
                2 -> { // Error (Cross)
                    canvas.drawLine(w * 0.25f, h * 0.25f, w * 0.75f, h * 0.75f, paint)
                    canvas.drawLine(w * 0.75f, h * 0.25f, w * 0.25f, h * 0.75f, paint)
                }
                3 -> { // Info (i)
                    paint.style = Paint.Style.FILL
                    canvas.drawCircle(w * 0.5f, h * 0.2f, 6f, paint)
                    paint.style = Paint.Style.STROKE
                    canvas.drawLine(w * 0.5f, h * 0.4f, w * 0.5f, h * 0.8f, paint)
                }
            }
        }
    }
}
