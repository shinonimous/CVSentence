/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.viewmodel

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvrabbit.cvsentence.model.db.WordEntity
import com.cvrabbit.cvsentence.model.repository.MainRepository
import com.cvrabbit.cvsentence.util.calendar.CalendarOperation
import com.cvrabbit.cvsentence.util.constant.Constants.NOT_INITIALIZED_DS
import com.cvrabbit.cvsentence.util.constant.RRT
import com.cvrabbit.cvsentence.util.constant.SortPattern
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

private const val TAG = "WordsListViewModel"

@HiltViewModel
class WordsListViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    private val filter = mainRepository.getFilter()

    private val sortPattern = mainRepository.getSortPattern()

    private val allReferences = mainRepository.getAllReferences()

    val allWordsSortedByDateDesc = mainRepository.getAllWordsSortedByDateDesc()

    private val wordsSortedByDateDesc = mainRepository.getWordsSortedByDateDesc(
        if (filter.green) { listOf(true)} else {listOf(true, false)},
        filter.minDS,
        filter.maxDS,
        filter.startDate,
        filter.endDate,
        if (filter.reference == "") {allReferences.value ?: listOf("")} else { listOf(filter.reference) }
    )

    private val wordsSortedByDateAsc = mainRepository.getWordsSortedByDateAsc(
        if (filter.green) { listOf(true)} else {listOf(true, false)},
        filter.minDS,
        filter.maxDS,
        filter.startDate,
        filter.endDate,
        if (filter.reference == "") {allReferences.value ?: listOf("")} else { listOf(filter.reference) }
    )

    private val wordsSortedByDSDesc = mainRepository.getWordsSortedByDSDesc(
        if (filter.green) { listOf(true)} else {listOf(true, false)},
        filter.minDS,
        filter.maxDS,
        filter.startDate,
        filter.endDate,
        if (filter.reference == "") {allReferences.value ?: listOf("")} else { listOf(filter.reference) }
    )

    private val wordsSortedByDSAsc = mainRepository.getWordsSortedByDSAsc(
        if (filter.green) { listOf(true)} else {listOf(true, false)},
        filter.minDS,
        filter.maxDS,
        filter.startDate,
        filter.endDate,
        if (filter.reference == "") {allReferences.value ?: listOf("")} else { listOf(filter.reference) }
    )

    private val wordsSortedByWordDesc = mainRepository.getWordsSortedByWordDesc(
        if (filter.green) { listOf(true)} else {listOf(true, false)},
        filter.minDS,
        filter.maxDS,
        filter.startDate,
        filter.endDate,
        if (filter.reference == "") {allReferences.value ?: listOf("")} else { listOf(filter.reference) }
    )

    private val wordsSortedByWordAsc = mainRepository.getWordsSortedByWordAsc(
        if (filter.green) { listOf(true)} else {listOf(true, false)},
        filter.minDS,
        filter.maxDS,
        filter.startDate,
        filter.endDate,
        if (filter.reference == "") {allReferences.value ?: listOf("")} else { listOf(filter.reference) }
    )

    val words = MediatorLiveData<List<WordEntity>>()

    init {
        words.addSource(allWordsSortedByDateDesc) {
            if (sortPattern == SortPattern.DATE_DESC && filter.minDS == NOT_INITIALIZED_DS) {
                it?.let {
                    words.value = it
                }
            }
        }
        words.addSource(wordsSortedByDateDesc) {
            if (sortPattern == SortPattern.DATE_DESC && filter.minDS != NOT_INITIALIZED_DS) {
                it?.let {
                    words.value = it
                }
            }
        }
        words.addSource(wordsSortedByDateAsc) {
            if (sortPattern == SortPattern.DATE_ASC) {
                it?.let {
                    words.value = it
                }
            }
        }
        words.addSource(wordsSortedByDSDesc) {
            if (sortPattern == SortPattern.DS_DESC) {
                it?.let {
                    words.value = it
                }
            }
        }
        words.addSource(wordsSortedByDSAsc) {
            if (sortPattern == SortPattern.DS_ASC) {
                it?.let {
                    words.value = it
                }
            }
        }
        words.addSource(wordsSortedByWordDesc) {
            if (sortPattern == SortPattern.WORD_DESC) {
                it?.let {
                    words.value = it
                }
            }
        }
        words.addSource(wordsSortedByWordAsc) {
            if (sortPattern == SortPattern.WORD_ASC) {
                it?.let {
                    words.value = it
                }
            }
        }
    }

    // This might be needless if sort performed only once in init() is enough.
    fun sortWords() = when(sortPattern) {
        SortPattern.DATE_DESC -> {
            if (filter.minDS == NOT_INITIALIZED_DS) {
                allWordsSortedByDateDesc.value?.let { words.value = it }
            } else {
                wordsSortedByDateDesc.value?.let { words.value = it }
            }
        }
        SortPattern.DATE_ASC -> wordsSortedByDateAsc.value?.let { words.value = it }
        SortPattern.DS_DESC -> wordsSortedByDSDesc.value?.let { words.value = it }
        SortPattern.DS_ASC -> wordsSortedByDSAsc.value?.let { words.value = it }
        SortPattern.WORD_DESC -> wordsSortedByWordDesc.value?.let { words.value = it }
        SortPattern.WORD_ASC -> wordsSortedByWordAsc.value?.let { words.value = it }
    }

    fun ifAllWordsDeleted(): Boolean = words.value?.isEmpty() ?: true

    fun deleteWordEntity(wordEntity: WordEntity) {
        viewModelScope.launch {
            mainRepository.deleteWord(wordEntity)
        }
    }

    // When certain word card color get green, use this method
    fun setGreen() {
        if (!words.value.isNullOrEmpty()) {
            for (i in words.value!!) {
                i.green = CalendarOperation.minusDateLong(Date().time, i.lastLookupDate) > RRT.getRRTValue(i.recommendedRecurTiming).longValue
            }
        }
    }
}