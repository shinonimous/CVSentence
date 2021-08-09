/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvrabbit.cvsentence.model.db.WordEntity
import com.cvrabbit.cvsentence.model.repository.MainRepository
import com.cvrabbit.cvsentence.util.calendar.CalendarOperation
import com.cvrabbit.cvsentence.util.constant.RRT
import com.cvrabbit.cvsentence.util.constant.SortPattern
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*

private const val TAG = "WordsListViewModel"

class WordsListViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    lateinit var words: LiveData<List<WordEntity>>

    init {
        if(!ifAllWordsDeleted()) {
            updateLiveData()
        }
    }

    private fun updateLiveData() {
        val filter = mainRepository.getFilter()
        when(mainRepository.getSortPattern()) {
            SortPattern.DATE_DESC -> {
                words = mainRepository.getAllWordsSortedBySortedByDateDesc(
                    if (filter.green) { listOf(true)} else {listOf(true, false)},
                    filter.minDS,
                    filter.maxDS,
                    filter.startDate,
                    filter.endDate,
                    if (filter.reference == "") {mainRepository.getAllReferences()} else { listOf(filter.reference) }
                )
            }
            SortPattern.DATE_ASC -> {
                words = mainRepository.getAllWordsSortedBySortedByDateAsc(
                    if (filter.green) { listOf(true)} else {listOf(true, false)},
                    filter.minDS,
                    filter.maxDS,
                    filter.startDate,
                    filter.endDate,
                    if (filter.reference == "") {mainRepository.getAllReferences()} else { listOf(filter.reference) }
                )
            }
            SortPattern.DS_DESC -> {
                words = mainRepository.getAllWordsSortedBySortedByDSDesc(
                    if (filter.green) { listOf(true)} else {listOf(true, false)},
                    filter.minDS,
                    filter.maxDS,
                    filter.startDate,
                    filter.endDate,
                    if (filter.reference == "") {mainRepository.getAllReferences()} else { listOf(filter.reference) }
                )
            }
            SortPattern.DS_ASC -> {
                words = mainRepository.getAllWordsSortedBySortedByDSAsc(
                    if (filter.green) { listOf(true)} else {listOf(true, false)},
                    filter.minDS,
                    filter.maxDS,
                    filter.startDate,
                    filter.endDate,
                    if (filter.reference == "") {mainRepository.getAllReferences()} else { listOf(filter.reference) }
                )
            }
            SortPattern.WORD_DESC -> {
                words = mainRepository.getAllWordsSortedBySortedByWordDesc(
                    if (filter.green) { listOf(true)} else {listOf(true, false)},
                    filter.minDS,
                    filter.maxDS,
                    filter.startDate,
                    filter.endDate,
                    if (filter.reference == "") {mainRepository.getAllReferences()} else { listOf(filter.reference) }
                )
            }
            SortPattern.WORD_ASC -> {
                words = mainRepository.getAllWordsSortedBySortedByWordAsc(
                    if (filter.green) { listOf(true)} else {listOf(true, false)},
                    filter.minDS,
                    filter.maxDS,
                    filter.startDate,
                    filter.endDate,
                    if (filter.reference == "") {mainRepository.getAllReferences()} else { listOf(filter.reference) }
                )
            }
        }
    }

    fun ifAllWordsDeleted(): Boolean = runBlocking {
        withContext(viewModelScope.coroutineContext) {
            val words = mainRepository.getAllWords()
            words.isEmpty()
        }
    }

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