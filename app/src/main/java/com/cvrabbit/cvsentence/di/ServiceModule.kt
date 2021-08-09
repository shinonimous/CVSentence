package com.cvrabbit.cvsentence.di

import android.content.Context
import androidx.core.app.NotificationCompat
import com.cvrabbit.cvsentence.R
import com.cvrabbit.cvsentence.util.constant.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    @ServiceScoped
    fun provideNotificationBuilder(
        @ApplicationContext app: Context
    ) = NotificationCompat.Builder(app , Constants.NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.vector_swipe_to_right)
        .setContentTitle(app.getString(R.string.app_name))
        .setContentText(app.getString(R.string.notification_content))
}