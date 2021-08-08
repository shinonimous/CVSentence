/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.service

import android.annotation.SuppressLint
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.graphics.PixelFormat.TRANSLUCENT
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager.LayoutParams.*
import android.widget.*
import com.cvrabbit.cvsentence.R
import com.cvrabbit.cvsentence.model.repository.FloatingPosition
import com.cvrabbit.cvsentence.util.constant.Constants.CLIPBOARD_COROUTINE_DELAY_INTERVAL
import com.cvrabbit.cvsentence.util.constant.Constants.OVERLAY_MEANING_SHOWING_INTERVAL
import com.cvrabbit.cvsentence.util.device.SizeMetrics
import com.cvrabbit.cvsentence.util.internet.Internet
import com.cvrabbit.cvsentence.util.lang.GoogleTextToSpeech
import com.cvrabbit.cvsentence.viewmodel.OverlayViewModelLikeObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "OverlayView"

@AndroidEntryPoint
class OverlayView @JvmOverloads constructor(
        ctx: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : FrameLayout(ctx, attrs, defStyle){

    companion object {
        const val layoutXdpLarge = 50
        var overlayView: OverlayView? = null
            private set

        @SuppressLint("UseCompatLoadingForDrawables")
        fun create(context: Context): OverlayView {
            overlayView = View.inflate(context, R.layout.overlay_view, null) as OverlayView
            return overlayView!!
        }
    }

    @Inject
    lateinit var overlayViewModelLikeObject: OverlayViewModelLikeObject

    @Inject
    lateinit var textToSpeech: GoogleTextToSpeech

    private val pixelWindowWidth
        get() = SizeMetrics(context).getPxWindowSize().first
    private val pixelWindowHeight
        get() = SizeMetrics(context).getPxWindowSize().second
    private val xMoveLarge
        get() = (pixelWindowWidth / 2) - SizeMetrics(context).dpToPx(layoutXdpLarge / 2).toInt()
    private val yMoveLarge
        get() = (pixelWindowHeight / 2) - SizeMetrics(context).dpToPx(50 / 2).toInt()

    private val windowManager: WindowManager
        get() = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var layoutParams: WindowManager.LayoutParams =
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                WindowManager.LayoutParams(
                        WRAP_CONTENT, WRAP_CONTENT, xMoveLarge, -yMoveLarge, TYPE_APPLICATION_OVERLAY, FOCUSABLE_AUTO, TRANSLUCENT
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams(
                        WRAP_CONTENT, WRAP_CONTENT, xMoveLarge, -yMoveLarge, TYPE_APPLICATION_OVERLAY, FLAG_NOT_FOCUSABLE or FLAG_NOT_TOUCHABLE or FLAG_NOT_TOUCH_MODAL, TRANSLUCENT
                )
            } else {
                WindowManager.LayoutParams(
                        WRAP_CONTENT, WRAP_CONTENT, xMoveLarge, -yMoveLarge, TYPE_SYSTEM_ALERT, FLAG_NOT_FOCUSABLE or FLAG_NOT_TOUCHABLE or FLAG_NOT_TOUCH_MODAL, TRANSLUCENT
                )
            }
    private var layoutParamsLeft: WindowManager.LayoutParams =
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                WindowManager.LayoutParams(
                        WRAP_CONTENT, WRAP_CONTENT, -xMoveLarge, -yMoveLarge, TYPE_APPLICATION_OVERLAY, FOCUSABLE_AUTO, TRANSLUCENT
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams(
                        WRAP_CONTENT, WRAP_CONTENT, -xMoveLarge, -yMoveLarge, TYPE_APPLICATION_OVERLAY, FLAG_NOT_FOCUSABLE or FLAG_NOT_TOUCHABLE or FLAG_NOT_TOUCH_MODAL, TRANSLUCENT
                )
            } else {
                WindowManager.LayoutParams(
                        WRAP_CONTENT, WRAP_CONTENT, -xMoveLarge, -yMoveLarge, TYPE_SYSTEM_ALERT, FLAG_NOT_FOCUSABLE or FLAG_NOT_TOUCHABLE or FLAG_NOT_TOUCH_MODAL, TRANSLUCENT
                )
            }

    @Synchronized
    fun show() {
        if (!this.isShown) {
            if(overlayViewModelLikeObject.getFloatingPosition() == FloatingPosition.RIGHT) {
                windowManager.addView(this, layoutParams)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    setClipboardListener()
                } else {
                    setClipboardListenerUsingCoroutine()
                }
            } else {
                windowManager.addView(this,layoutParamsLeft)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    setClipboardListener()
                } else {
                    setClipboardListenerUsingCoroutine()
                }
            }
        }
    }

    @Synchronized
    fun hide() {
        if (this.isShown) {
            windowManager.removeView(this)
        }
    }

    @Synchronized
    fun updateLayout() {
        if (overlayViewModelLikeObject.getFloatingPosition() == FloatingPosition.RIGHT) {
            windowManager.updateViewLayout(this, layoutParams)
        } else {
            windowManager.updateViewLayout(this, layoutParamsLeft)
        }
    }

    @Synchronized
    fun updateFocusable(flag: Boolean) {
        if (this.isShown) {
            if(flag) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    when(overlayViewModelLikeObject.getFloatingPosition()) {
                        FloatingPosition.RIGHT -> { layoutParams.flags = FOCUSABLE; windowManager.updateViewLayout(this, layoutParams) }
                        FloatingPosition.LEFT -> { layoutParamsLeft.flags = FOCUSABLE; windowManager.updateViewLayout(this, layoutParamsLeft) }
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    when(overlayViewModelLikeObject.getFloatingPosition()) {
                        FloatingPosition.RIGHT -> { layoutParams.flags = FOCUSABLE_AUTO; windowManager.updateViewLayout(this, layoutParams) }
                        FloatingPosition.LEFT -> { layoutParamsLeft.flags = FOCUSABLE_AUTO; windowManager.updateViewLayout(this, layoutParamsLeft) }
                    }
                }
            }
        }
    }

    /**
     * Following is clipboard service methods
     */
    private val clipboardService = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    var prevData = ""

    // Use this when Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
    private fun setClipboardListener() {
        if(clipboardService.isEnable()) {
            Log.d(TAG, "ClipboardService is Enable")
            clipboardService.addPrimaryClipChangedListener {
                if (overlayView != null) {
                    pasteClipboardData()
                }
            }
        } else {
            Log.d(TAG, "ClipboardService is not Enable")
        }
    }

    // Use this when Build.VERSION.SDK_INT > Build.VERSION_CODES.P
    private fun setClipboardListenerUsingCoroutine() {
        overlayView!!.updateFocusable(true)
        CoroutineScope(Dispatchers.Main).launch {
            delay(CLIPBOARD_COROUTINE_DELAY_INTERVAL)
            setClipboardListener()
            overlayView!!.updateFocusable(false)
        }
    }

    private fun pasteClipboardData() {
        Log.d(TAG, "pasteClipboardData is Running")
        if(clipboardService.isEnable()) {
            val item = clipboardService.primaryClip?.getItemAt(0)
            val pasteData = item?.text.toString()
            if (prevData != pasteData) {
                if (Internet(context).checkInternetConnection()) {
                    val word = overlayViewModelLikeObject.searchWord(pasteData)
                    if (word != null) {
                        word.reference = OverlayService.focusReference?:""
                        overlayViewModelLikeObject.createNewWordEntity(word)
                        showText(word.mainMeaning)
                        textToSpeech.textToSpeechOnSelection(word)
                    } else {
                        showText(context.resources.getString(R.string.cb_word_not_found))
                    }
                } else {
                    showText(context.resources.getString(R.string.cb_internet_connection))
                }
                prevData = pasteData
            } else {
                Log.d(TAG, "SAME WORD! NO COPY EVENT")
            }
        }
    }

    private fun ClipboardManager.isEnable() : Boolean {
        return when {
            !hasPrimaryClip() -> {
                false
            }
            (primaryClipDescription!!.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) -> {
                true
            }
            (primaryClipDescription!!.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)) -> {
                true
            }
            else -> {
                false
            }
        }
    }

    private fun showText(text: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val job = launch {
                overlayView?.findViewById<TextView>(R.id.rabbitComment)?.text = text
                delay(OVERLAY_MEANING_SHOWING_INTERVAL)
            }
            job.join()
            overlayView?.findViewById<TextView>(R.id.rabbitComment)?.text = ""
        }
    }
}