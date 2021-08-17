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
import com.cvrabbit.cvsentence.util.calendar.CalendarOperation.getFirstMillisOfMonth
import com.cvrabbit.cvsentence.util.calendar.CalendarOperation.getLastMillisOfMonth
import com.cvrabbit.cvsentence.util.calendar.CalendarOperation.longDateToStringyyyyMMFormat
import com.cvrabbit.cvsentence.util.constant.Constants.NOT_INITIALIZED_DATE
import com.cvrabbit.cvsentence.util.constant.Constants.NOT_INITIALIZED_DS
import com.cvrabbit.cvsentence.util.constant.RRT
import com.cvrabbit.cvsentence.util.constant.SortPattern
import com.cvrabbit.cvsentence.util.data.WordFilter
import com.cvrabbit.cvsentence.view.MainActivity
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

    private lateinit var allReferences: List<String>

    val allWordsSortedByDateDesc get() = mainRepository.getAllWordsSortedByDateDesc()

    private val wordsSortedByDateDesc get() = mainRepository.getWordsSortedByDateDesc(
        if (filter.green) { listOf(true)} else {listOf(true, false)},
        filter.minDS,
        filter.maxDS,
        getFirstMillisOfMonth(longDateToStringyyyyMMFormat(filter.startDate)),
        getLastMillisOfMonth(longDateToStringyyyyMMFormat(filter.endDate)),
        if (filter.reference == "") {
            Log.d(TAG, "inside mainRepository.getWordsSortedByDateDesc: allReferences: $allReferences")
            allReferences } else { listOf(filter.reference) }
    )

    private val wordsSortedByDateAsc get() = mainRepository.getWordsSortedByDateAsc(
        if (filter.green) { listOf(true)} else {listOf(true, false)},
        filter.minDS,
        filter.maxDS,
        getFirstMillisOfMonth(longDateToStringyyyyMMFormat(filter.startDate)),
        getLastMillisOfMonth(longDateToStringyyyyMMFormat(filter.endDate)),
        if (filter.reference == "") {allReferences} else { listOf(filter.reference) }
    )

    private val wordsSortedByDSDesc get() = mainRepository.getWordsSortedByDSDesc(
        if (filter.green) { listOf(true)} else {listOf(true, false)},
        filter.minDS,
        filter.maxDS,
        getFirstMillisOfMonth(longDateToStringyyyyMMFormat(filter.startDate)),
        getLastMillisOfMonth(longDateToStringyyyyMMFormat(filter.endDate)),
        if (filter.reference == "") {allReferences} else { listOf(filter.reference) }
    )

    private val wordsSortedByDSAsc get() = mainRepository.getWordsSortedByDSAsc(
        if (filter.green) { listOf(true)} else {listOf(true, false)},
        filter.minDS,
        filter.maxDS,
        getFirstMillisOfMonth(longDateToStringyyyyMMFormat(filter.startDate)),
        getLastMillisOfMonth(longDateToStringyyyyMMFormat(filter.endDate)),
        if (filter.reference == "") {allReferences} else { listOf(filter.reference) }
    )

    private val wordsSortedByWordDesc get() = mainRepository.getWordsSortedByWordDesc(
        if (filter.green) { listOf(true)} else {listOf(true, false)},
        filter.minDS,
        filter.maxDS,
        getFirstMillisOfMonth(longDateToStringyyyyMMFormat(filter.startDate)),
        getLastMillisOfMonth(longDateToStringyyyyMMFormat(filter.endDate)),
        if (filter.reference == "") {allReferences} else { listOf(filter.reference) }
    )

    private val wordsSortedByWordAsc get() = mainRepository.getWordsSortedByWordAsc(
        if (filter.green) { listOf(true)} else {listOf(true, false)},
        filter.minDS,
        filter.maxDS,
        getFirstMillisOfMonth(longDateToStringyyyyMMFormat(filter.startDate)),
        getLastMillisOfMonth(longDateToStringyyyyMMFormat(filter.endDate)),
        if (filter.reference == "") {allReferences} else { listOf(filter.reference) }
    )

    val words = MediatorLiveData<List<WordEntity>>()

    fun setWordsSources(allReferences: List<String>) {

        this.allReferences = createAllReferencesWithBlank(allReferences)

        Log.d(TAG, "init is Running: sortPattern: $sortPattern ," +
                " minDS: ${filter.minDS}, maxDS: ${filter.maxDS}," +
                " minDate: ${filter.startDate}, maxDate: ${filter.endDate}, " +
                "green: ${filter.green}, references: ${filter.reference}, " +
                "allReferences: ${this.allReferences}")

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

    private fun createAllReferencesWithBlank(allReferences: List<String>): List<String> {
        val resultList: MutableList<String> = mutableListOf()
        resultList.add("")
        resultList.addAll(allReferences)
        return resultList
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

    // Delete word entity
    fun deleteWordEntity(wordEntity: WordEntity) {
        viewModelScope.launch {
            mainRepository.deleteWord(wordEntity)
        }
    }

    // When certain word card color get green, use this method
    fun setGreen(words: List<WordEntity>) {
        for (i in words) {
            val prevGreen = i.green
            val currentGreen = CalendarOperation.minusDateLong(Date().time, i.lastLookupDate) > RRT.getRRTValue(i.recommendedRecurTiming).longValue
            if(prevGreen != currentGreen) {
                i.green = currentGreen
                viewModelScope.launch {
                    mainRepository.updateWord(i)
                }
            }
        }
    }

    // reset WordFilter
    fun resetFilter() {
        mainRepository.saveFilter(
            WordFilter(
                green = false,
                minDS = NOT_INITIALIZED_DS,
                maxDS = NOT_INITIALIZED_DS,
                startDate = NOT_INITIALIZED_DATE,
                endDate = NOT_INITIALIZED_DATE,
                reference = ""
            )
        )
    }
}