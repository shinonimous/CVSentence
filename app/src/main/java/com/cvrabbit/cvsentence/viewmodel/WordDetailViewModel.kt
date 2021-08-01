/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.viewmodel

import android.app.Application
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cvrabbit.cvsentence.model.db.DS
import com.cvrabbit.cvsentence.model.db.RRT
import com.cvrabbit.cvsentence.model.db.Reference
import com.cvrabbit.cvsentence.model.db.Word
import com.cvrabbit.cvsentence.model.preferences.PreferenceAccess
import com.cvrabbit.cvsentence.model.repository.MainRepository
import io.realm.Realm
import io.realm.kotlin.where

private const val TAG = "WordDetailViewModel"

class WordDetailViewModel @ViewModelInject constructor(
    application: Application,
    val mainRepository: MainRepository
) : AndroidViewModel(application) {
    private var realm = Realm.getDefaultInstance()
    private var backUpWordProperties = BackUpWordProperties()
    var liveWord: MutableLiveData<Word> = MutableLiveData()

    /**
     * Hold some word properties when updating realmDB
     * to deal with user's undo action.
     */
    class BackUpWordProperties(
        var lastRRT: RRT? = null,
        var lastDS: DS? = null,
        var notRememberedCountSinceUnderLimitReached: Int? = null
    ) {
        fun initProperties() {
            lastRRT = null
            lastDS = null
            notRememberedCountSinceUnderLimitReached = null
        }
    }

    // check before showing reference spinner
    fun ifReferenceEmpty(): Boolean {
        val references = realm.where<Reference>().findAll()
        return references.isEmpty()
    }

    // use when showing reference spinner
    fun getAllReferencesAsArrayString(): Array<String> {
        val reference = mutableListOf<String>()
        val refs = realm.where<Reference>().findAll()
        reference.add("")
        if (refs.isNotEmpty()) {
            for (i in refs) {
                reference.add(i.reference)
            }
        }
        return reference.toTypedArray()
    }

    // When reference is updated, use this method
    fun setReference(checkWord: String, reference: String) {
        var word: Word?
        realm.executeTransaction { db: Realm ->
            word = db.where<Word>().equalTo("word", checkWord).findFirst()
            word?.let {
                it.reference = reference
                liveWord.value = it
            }
        }
    }

    /**
     * When user checks NotRemembered Flag, use this method.
     * UpdatedProperties:
     *  recommendedRecurTiming,
     *  notRememberedCountSinceUnderLimitReached,
     *  difficultyScore
     *  notRememberedCount
     */
    fun updateWhenNotRememberedChecked(checkWord: String){
        Log.d(TAG, "updateWhenNotRememberedChecked() is Running")
        var word: Word?
        realm.executeTransaction { db: Realm ->
            word = db.where<Word>().equalTo("word", checkWord).findFirst()
            word?.let {
                backUpWordProperties.lastRRT = RRT.getRRTValue(it.recommendedRecurTiming)
                backUpWordProperties.lastDS = DS.getDSValue(it.difficultyScore)
                backUpWordProperties.notRememberedCountSinceUnderLimitReached = it.notRememberedCountSinceUnderLimitReached

                it.notRememberedCount += 1

                if (RRT.getRRTValue(it.recommendedRecurTiming) == RRT.FIRST) {
                    it.notRememberedCountSinceUnderLimitReached += 1
                }

                it.recommendedRecurTiming = RRT.getProperRRTWhenNotRemembered(
                    it.durationFromLastLookupTime,
                    RRT.getRRTValue(it.recommendedRecurTiming)
                ).value

                it.difficultyScore = DS.getProperDS(
                    it.notRememberedCountSinceUnderLimitReached,
                    RRT.getRRTValue(it.recommendedRecurTiming)
                ).value

                liveWord.value = it
            }
        }
    }

    /**
     * When user unchecks NotRemembered Flag, use this method.
     */
    fun updateWhenNotRememberedUnChecked(checkWord: String){
        Log.d(TAG, "updateWhenNotRememberedUnChecked() is Running")
        var word: Word?
        realm.executeTransaction { db: Realm ->
            word = db.where<Word>().equalTo("word", checkWord).findFirst()
            word?.let {
                it.notRememberedCount -= 1
                it.notRememberedCountSinceUnderLimitReached = backUpWordProperties.notRememberedCountSinceUnderLimitReached!!
                it.recommendedRecurTiming = backUpWordProperties.lastRRT!!.value
                it.difficultyScore = backUpWordProperties.lastDS!!.value

                liveWord.value = it
            }
        }
        backUpWordProperties.initProperties()
    }

    /**
     * When user checks Remembered Flag, use this method.
     * UpdatedProperties:
     *  recommendedRecurTiming,
     *  notRememberedCountSinceUnderLimitReached,
     *  difficultyScore
     *  rememberedCount
     */
    fun updateWhenRememberedChecked(checkWord: String) {
        Log.d(TAG, "updateWhenRememberedChecked() is Running")
        var word: Word?
        realm.executeTransaction { db: Realm ->
            word = db.where<Word>().equalTo("word", checkWord).findFirst()
            word?.let {
                backUpWordProperties.lastRRT = RRT.getRRTValue(it.recommendedRecurTiming)
                backUpWordProperties.lastDS = DS.getDSValue(it.difficultyScore)
                backUpWordProperties.notRememberedCountSinceUnderLimitReached = it.notRememberedCountSinceUnderLimitReached

                it.rememberedCount += 1

                it.notRememberedCountSinceUnderLimitReached = 0

                it.recommendedRecurTiming = RRT.getProperRRTWhenRemembered(
                    it.durationFromLastLookupTime,
                    RRT.getRRTValue(it.recommendedRecurTiming)
                ).value

                it.difficultyScore = DS.getProperDS(
                    it.notRememberedCountSinceUnderLimitReached,
                    RRT.getRRTValue(it.recommendedRecurTiming)
                ).value

                liveWord.value = it
            }
        }
    }

    /**
     * When user unchecks Remembered Flag, use this method.
     */
    fun updateWhenRememberedUnChecked(checkWord: String) {
        Log.d(TAG, "updateWhenRememberedUnChecked() is Running")
        var word: Word?
        realm.executeTransaction { db: Realm ->
            word = db.where<Word>().equalTo("word", checkWord).findFirst()
            word?.let {
                it.rememberedCount -= 1
                it.notRememberedCountSinceUnderLimitReached = backUpWordProperties.notRememberedCountSinceUnderLimitReached!!
                it.recommendedRecurTiming = backUpWordProperties.lastRRT!!.value
                it.difficultyScore = backUpWordProperties.lastDS!!.value

                liveWord.value = it
            }
        }
    }

    // Check if Some of the On-Demand Settings are on
    fun ifSomeOfOnDemandSettingsOn():Boolean {
        return PreferenceAccess(getApplication()).getOnDemandWordSoundSetting() || PreferenceAccess(getApplication()).getOnDemandMeaningSoundSetting()
    }

    // release the realm
    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}