/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.viewmodel

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cvrabbit.cvsentence.model.db.*
import com.cvrabbit.cvsentence.model.repository.MainRepository
import io.realm.Realm
import io.realm.kotlin.where
import java.util.*

private const val TAG = "WordDetailViewModel"

class WordDetailViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    /**
     * for room
     */

    var focusWord: MutableLiveData<WordEntity> = MutableLiveData()
    private lateinit var backUp: BackUp

    // Back up some of the data to deal with user's undo action
    data class BackUp(
        var lastRRT: RRT? = null,
        var lastDS: DS? = null,
        var notRememberedCountSinceUnderLimitReached: Int? = null
    )

    fun setReference(reference: String) {
        focusWord.value!!.reference = reference
    }

    fun updateLookup() {
        focusWord.value!!.lookupCount += 1
        focusWord.value!!.durationFromLastLookupTime = Date().time - focusWord.value!!.lastLookupDate
        focusWord.value!!.lastLookupDate = Date().time
        focusWord.value!!.green = false
    }

    fun updateWhenNotRememberedChecked() {
        backUp = BackUp(
            RRT.getRRTValue(focusWord.value!!.recommendedRecurTiming),
            DS.getDSValue(focusWord.value!!.difficultyScore),
            focusWord.value!!.notRememberedCountSinceUnderLimitReached
        )

        focusWord.value!!.notRememberedCount += 1

        if (RRT.getRRTValue(focusWord.value!!.recommendedRecurTiming) == RRT.FIRST) {
            focusWord.value!!.notRememberedCountSinceUnderLimitReached += 1
        }

        focusWord.value!!.recommendedRecurTiming = RRT.getProperRRTWhenNotRemembered(
            focusWord.value!!.durationFromLastLookupTime,
            RRT.getRRTValue(focusWord.value!!.recommendedRecurTiming)
        ).value

        focusWord.value!!.difficultyScore = DS.getProperDS(
            focusWord.value!!.notRememberedCountSinceUnderLimitReached,
            RRT.getRRTValue(focusWord.value!!.recommendedRecurTiming)
        ).value
    }

    fun updateWhenNotRememberedUnChecked() {
        focusWord.value!!.notRememberedCount -= 1
        focusWord.value!!.notRememberedCountSinceUnderLimitReached = backUp.notRememberedCountSinceUnderLimitReached!!
        focusWord.value!!.recommendedRecurTiming = backUp.lastRRT!!.value
        focusWord.value!!.difficultyScore = backUp.lastDS!!.value
    }

    fun updateWhenRememberedChecked() {
        backUp = BackUp(
            RRT.getRRTValue(focusWord.value!!.recommendedRecurTiming),
            DS.getDSValue(focusWord.value!!.difficultyScore),
            focusWord.value!!.notRememberedCountSinceUnderLimitReached
        )

        focusWord.value!!.rememberedCount += 1

        focusWord.value!!.notRememberedCountSinceUnderLimitReached = 0

        focusWord.value!!.recommendedRecurTiming = RRT.getProperRRTWhenRemembered(
            focusWord.value!!.durationFromLastLookupTime,
            RRT.getRRTValue(focusWord.value!!.recommendedRecurTiming)
        ).value

        focusWord.value!!.difficultyScore = DS.getProperDS(
            focusWord.value!!.notRememberedCountSinceUnderLimitReached,
            RRT.getRRTValue(focusWord.value!!.recommendedRecurTiming)
        ).value
    }

    fun updateWhenRememberedUnChecked() {
        focusWord.value!!.rememberedCount -= 1
        focusWord.value!!.notRememberedCountSinceUnderLimitReached = backUpWordProperties.notRememberedCountSinceUnderLimitReached!!
        focusWord.value!!.recommendedRecurTiming = backUpWordProperties.lastRRT!!.value
        focusWord.value!!.difficultyScore = backUpWordProperties.lastDS!!.value
    }

    // Check if Some of the On-Demand Settings are on
    fun ifSomeOfOnDemandSettingsOn():Boolean {
        return mainRepository.getOnDemandWordSoundSetting() || mainRepository.getOnDemandMeaningSoundSetting()
    }

    /**
     * for realm
     */

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

    // release the realm
    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

}