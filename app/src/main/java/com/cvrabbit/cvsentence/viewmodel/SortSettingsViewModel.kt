/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.viewmodel

import androidx.lifecycle.ViewModel
import com.cvrabbit.cvsentence.model.repository.MainRepository
import com.cvrabbit.cvsentence.util.calendar.CalendarOperation.addOneMonthToLongDate
import com.cvrabbit.cvsentence.util.calendar.CalendarOperation.longDateToStringyyyyMMFormat
import com.cvrabbit.cvsentence.util.constant.SortPattern
import com.cvrabbit.cvsentence.util.data.WordFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "SortSettingsViewModel"

@HiltViewModel
class SortSettingsViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    fun saveSortPattern(sortPattern: SortPattern) = mainRepository.saveSortPattern(sortPattern)

    fun saveFilter(wordFilter: WordFilter) = mainRepository.saveFilter(wordFilter)

    // use when showing reg date spinner
    fun getRegArray(startEndDate: Pair<Long, Long>):Array<String> {
        val mStrList = mutableListOf<String>()
        mStrList.add(0, "")
        var x = startEndDate.first
        while (x <= startEndDate.second) {
            val addDate = longDateToStringyyyyMMFormat(x)
            mStrList.add(addDate)
            x = addOneMonthToLongDate(x)
        }
        return mStrList.toTypedArray()
    }
}
