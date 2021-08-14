package com.cvrabbit.cvsentence.di

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.cvrabbit.cvsentence.model.db.WordDatabase
import com.cvrabbit.cvsentence.model.internet.WordSearch
import com.cvrabbit.cvsentence.model.internet.edict.EdictSearch
import com.cvrabbit.cvsentence.model.internet.wordnik.WordNikSearch
import com.cvrabbit.cvsentence.model.preferences.PreferenceAccess
import com.cvrabbit.cvsentence.model.repository.MainRepository
import com.cvrabbit.cvsentence.util.constant.Constants.RUNNING_DATABASE_NAME
import com.cvrabbit.cvsentence.util.constant.WordSearchType
import com.cvrabbit.cvsentence.util.lang.GoogleTextToSpeech
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWordSearch(
        @ApplicationContext app: Context
    ): WordSearch = when(WordSearchType.current) {
        WordSearchType.EN_EN -> {
            WordNikSearch(app)
        }
        WordSearchType.EN_JP -> {
            EdictSearch(app)
        }
    }

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

    @Provides
    @Singleton
    fun provideTTS(
        @ApplicationContext app: Context,
        mainRepository: MainRepository
    ) = GoogleTextToSpeech(
        app,
        mainRepository
    )

}