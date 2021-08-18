/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvrabbit.cvsentence.model.db.*
import com.cvrabbit.cvsentence.model.repository.MainRepository
import com.cvrabbit.cvsentence.util.constant.DS
import com.cvrabbit.cvsentence.util.constant.RRT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

private const val TAG = "WordDetailViewModel"

@HiltViewModel
class WordDetailViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _focusWord: MutableLiveData<WordEntity> = MutableLiveData()
    val focusWord: LiveData<WordEntity> = _focusWord
    private lateinit var backUp: BackUp

    // Back up some of the data to deal with user's undo action
    data class BackUp(
        var lastRRT: RRT? = null,
        var lastDS: DS? = null,
        var notRememberedCountSinceUnderLimitReached: Int? = null
    )

    fun setReference(reference: String) {
        _focusWord.value?.reference = reference
        _focusWord.postValue(_focusWord.value)
    }

    fun updateLookup(word: WordEntity) {
        Log.d(TAG, "updateLookup is Running: Word ID: ${word.id}")
        _focusWord.value = word
        _focusWord.value?.let {
            it.lookupCount += 1
            it.durationFromLastLookupTime = Date().time - it.lastLookupDate
            it.lastLookupDate = Date().time
            it.green = false
        }
        _focusWord.postValue(_focusWord.value)
    }

    fun updateWhenNotRememberedChecked() {
        Log.d(TAG, "updateWhenNotRememberedChecked is Running")
        backUp = BackUp(
            RRT.getRRTValue(_focusWord.value!!.recommendedRecurTiming),
            DS.getDSValue(_focusWord.value!!.difficultyScore),
            _focusWord.value?.notRememberedCountSinceUnderLimitReached
        )

        _focusWord.value!!.notRememberedCount += 1

        if (RRT.getRRTValue(_focusWord.value!!.recommendedRecurTiming) == RRT.FIRST) {
            _focusWord.value!!.notRememberedCountSinceUnderLimitReached += 1
        }

        _focusWord.value!!.recommendedRecurTiming = RRT.getProperRRTWhenNotRemembered(
            _focusWord.value!!.durationFromLastLookupTime,
            RRT.getRRTValue(_focusWord.value!!.recommendedRecurTiming)
        ).value

        _focusWord.value!!.difficultyScore = DS.getProperDS(
            _focusWord.value!!.notRememberedCountSinceUnderLimitReached,
            RRT.getRRTValue(_focusWord.value!!.recommendedRecurTiming)
        ).value

        _focusWord.postValue(_focusWord.value)
    }

    fun updateWhenNotRememberedUnChecked() {
        Log.d(TAG, "updateWhenNotRememberedUnChecked is Running: ${_focusWord.value?.notRememberedCount}")
        _focusWord.value?.let {
            it.notRememberedCount -= 1
            it.notRememberedCountSinceUnderLimitReached = backUp.notRememberedCountSinceUnderLimitReached!!
            it.recommendedRecurTiming = backUp.lastRRT!!.value
            it.difficultyScore = backUp.lastDS!!.value
        }
        _focusWord.postValue(_focusWord.value)
    }

    fun updateWhenRememberedChecked() {
        Log.d(TAG, "updateWhenRememberedChecked is Running")
        backUp = BackUp(
            RRT.getRRTValue(_focusWord.value!!.recommendedRecurTiming),
            DS.getDSValue(_focusWord.value!!.difficultyScore),
            _focusWord.value?.notRememberedCountSinceUnderLimitReached
        )

        _focusWord.value!!.rememberedCount += 1

        _focusWord.value!!.notRememberedCountSinceUnderLimitReached = 0

        _focusWord.value!!.recommendedRecurTiming = RRT.getProperRRTWhenRemembered(
            _focusWord.value!!.durationFromLastLookupTime,
            RRT.getRRTValue(_focusWord.value!!.recommendedRecurTiming)
        ).value

        _focusWord.value!!.difficultyScore = DS.getProperDS(
            _focusWord.value!!.notRememberedCountSinceUnderLimitReached,
            RRT.getRRTValue(_focusWord.value!!.recommendedRecurTiming)
        ).value

        _focusWord.postValue(_focusWord.value)
    }

    fun updateWhenRememberedUnChecked() {
        Log.d(TAG, "updateWhenRememberedUnChecked is Running")
        _focusWord.value?.let {
            it.rememberedCount -= 1
            it.notRememberedCountSinceUnderLimitReached = backUp.notRememberedCountSinceUnderLimitReached!!
            it.recommendedRecurTiming = backUp.lastRRT!!.value
            it.difficultyScore = backUp.lastDS!!.value
        }

        _focusWord.postValue(_focusWord.value)
    }

    fun updateWord(word: WordEntity) =
        viewModelScope.launch {
            mainRepository.updateWord(word)
        }

    // Check if Some of the On-Demand Settings are on
    fun ifSomeOfOnDemandSettingsOn():Boolean {
        return mainRepository.getOnDemandWordSoundSetting() || mainRepository.getOnDemandMeaningSoundSetting()
    }

}