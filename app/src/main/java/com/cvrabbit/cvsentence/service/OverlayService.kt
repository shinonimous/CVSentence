/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.cvrabbit.cvsentence.R
import com.cvrabbit.cvsentence.model.db.WordEntity
import com.cvrabbit.cvsentence.model.db.initializeWordEntity
import com.cvrabbit.cvsentence.util.constant.Constants.ACTION_HIDE
import com.cvrabbit.cvsentence.util.constant.Constants.ACTION_SHOW
import com.cvrabbit.cvsentence.util.constant.Constants.NOTIFICATION_CHANEL_NAME
import com.cvrabbit.cvsentence.util.constant.Constants.NOTIFICATION_CHANNEL_ID
import com.cvrabbit.cvsentence.util.constant.Constants.NOTIFICATION_ID
import com.cvrabbit.cvsentence.viewmodel.OverlayViewModelLikeObject
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "OverlayService"

@AndroidEntryPoint
class OverlayService : LifecycleService() {

    companion object {
        val overlayView = MutableLiveData<OverlayView?>(null)
        val newWord = MutableLiveData<WordEntity>()
        var focusReference: String? = null

        fun start(context: Context) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_SHOW
            }
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_HIDE
            }
            context.startService(intent)
        }
    }

    private var ifFirstRun = true
    @Inject
    private lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        overlayView.value = OverlayView.create(this)
    }

    // Start or Stop the Service according to intent
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.let {
            when (it.action) {
                ACTION_SHOW -> {
                    if(ifFirstRun) {
                        startForegroundService()
                        ifFirstRun = false
                    }
                    overlayView.value!!.show()
                }
                ACTION_HIDE -> {
                    overlayView.value!!.hide()
                    overlayView.value = null
                    stopSelf()
                }
            }
        }
        return START_STICKY
    }

    // Initialize the LiveData Object
    private fun addEmptyWord() = newWord.value?.apply {
        initializeWordEntity(this)
        newWord.postValue(this)
    } ?: newWord.postValue(WordEntity())

    // Start ForeGroundService (Notification)
    private fun startForegroundService() {

        addEmptyWord()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID,  notificationBuilder.build())
    }

    // For Above Version Oreo: Foreground Service needs notification
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    // Cleans up views just in case.
    override fun onDestroy() {
        super.onDestroy()
        overlayView.value?.hide()
        overlayView.value = null
    }

    // This service does not support binding.
    override fun onBind(intent: Intent): Nothing? {
        super.onBind(intent)
        return null
    }
}
