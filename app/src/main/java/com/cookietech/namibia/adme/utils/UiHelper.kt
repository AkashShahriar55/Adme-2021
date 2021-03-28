package com.cookietech.namibia.adme.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup.MarginLayoutParams

class UiHelper(context: Context) {
    private val context: Context? = null
    private val displayMetrics: DisplayMetrics
    fun getPercentOfDisplayWidthInPixels(percent: Float): Int {
        return (displayMetrics.widthPixels * percent).toInt()
    }

    val displayWidthInPixels: Int
        get() = displayMetrics.widthPixels
    val displayHeightInPixels: Int
        get() = displayMetrics.heightPixels
    val displayWidth: Float
        get() = convertPixelsToDp(displayMetrics.widthPixels.toFloat())
    val displayHeight: Float
        get() = convertPixelsToDp(displayMetrics.heightPixels.toFloat())
    val displayDensityInDpi: Int
        get() = displayMetrics.densityDpi

    fun setViewWidth(view: View, widthInPixels: Int) {
        val layoutParams = view.layoutParams
        layoutParams.width = widthInPixels
        view.layoutParams = layoutParams
    }

    fun setViewHeight(view: View, widthInPixels: Int) {
        val layoutParams = view.layoutParams
        layoutParams.width = widthInPixels
        view.layoutParams = layoutParams
    }

    fun getViewWidth(view: View): Int {
        val layoutParams = view.layoutParams
        return layoutParams.width
    }

    fun getViewHeight(view: View): Int {
        val layoutParams = view.layoutParams
        return layoutParams.height
    }

    fun convertDpToPixels(dp: Int): Float {
        return dp * displayDensityInDpi / 160f
    }

    fun convertPixelsToDp(px: Float): Float {
        return px / displayDensityInDpi
    }

    fun setMargins(v: View, l: Int, t: Int, r: Int, b: Int) {
        var l = l
        var t = t
        var r = r
        var b = b
        l = convertDpToPixels(l).toInt()
        t = convertDpToPixels(t).toInt()
        r = convertDpToPixels(r).toInt()
        b = convertDpToPixels(b).toInt()
        if (v.layoutParams is MarginLayoutParams) {
            val p = v.layoutParams as MarginLayoutParams
            p.setMargins(l, t, r, b)
            v.requestLayout()
        }
    }

    init {
        displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
    }
}