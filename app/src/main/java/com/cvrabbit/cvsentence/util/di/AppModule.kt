package com.cvrabbit.cvsentence.util.di

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.cvrabbit.cvsentence.model.db.WordDatabase
import com.cvrabbit.cvsentence.model.preferences.PreferenceAccess
import com.cvrabbit.cvsentence.util.constant.Constants.RUNNING_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWordDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        WordDatabase::class.java,
        RUNNING_DATABASE_NAME
    ).build()

    @Provides
    @Singleton
    fun provideWordDAO(db: WordDatabase) = db.getWordDAO()

    @Provides
    @Singleton
    fun providePref(
        @ApplicationContext app: Context
    ) = PreferenceAccess(
        PreferenceManager.getDefaultSharedPreferences(app)
    )

}