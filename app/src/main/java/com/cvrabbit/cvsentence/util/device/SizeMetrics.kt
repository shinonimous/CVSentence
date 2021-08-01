/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.util.device

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

class SizeMetrics(context: Context) {
    private val appContext = context.applicationContext

    companion object {
        fun newInstance(context: Context) = SizeMetrics(context)
    }

    fun dpToPx(dp: Int): Float {
        val displayMetrics = appContext.resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt().toFloat()
    }

    fun pxToDp(px: Int): Float {
        val displayMetrics = appContext.resources.displayMetrics
        return (px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt().toFloat()
    }

    fun getPxWindowSize(): Pair<Int, Int> {
        val windowsManager = appContext.getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager

        val deviceWidth:Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowsManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            (windowMetrics.bounds.width() - insets.left - insets.right)
        } else {
            val displayMetrics = DisplayMetrics()
            displayMetrics.widthPixels
        }

        val deviceHeight:Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowsManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            displayMetrics.heightPixels
        }

        return Pair(deviceWidth, deviceHeight)
    }
}