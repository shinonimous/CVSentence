/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cvrabbit.cvsentence.model.db.*
import com.cvrabbit.cvsentence.model.repository.MainRepository
import java.util.*

private const val TAG = "WordDetailViewModel"

class WordDetailViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

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

    fun updateLookup(word: MutableLiveData<WordEntity>) {
        focusWord = word
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
        focusWord.value!!.notRememberedCountSinceUnderLimitReached = backUp.notRememberedCountSinceUnderLimitReached!!
        focusWord.value!!.recommendedRecurTiming = backUp.lastRRT!!.value
        focusWord.value!!.difficultyScore = backUp.lastDS!!.value
    }

    // Check if Some of the On-Demand Settings are on
    fun ifSomeOfOnDemandSettingsOn():Boolean {
        return mainRepository.getOnDemandWordSoundSetting() || mainRepository.getOnDemandMeaningSoundSetting()
    }

}