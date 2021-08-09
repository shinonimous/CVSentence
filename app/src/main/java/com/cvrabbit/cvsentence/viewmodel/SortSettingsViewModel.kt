/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.cvrabbit.cvsentence.model.repository.MainRepository
import com.cvrabbit.cvsentence.util.constant.SortPattern
import com.cvrabbit.cvsentence.util.data.WordFilter
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "SortSettingsViewModel"

class SortSettingsViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    fun saveSortPattern(sortPattern: SortPattern) = mainRepository.saveSortPattern(sortPattern)

    fun saveFilter(wordFilter: WordFilter) = mainRepository.saveFilter(wordFilter)

    fun getMinDS() = mainRepository.getMinDS()

    fun getMaxDS() = mainRepository.getMaxDS()

    fun getMinDate() = mainRepository.getMinDate()

    fun getMaxDate() = mainRepository.getMaxDate()

    // use when showing reg date spinner
    fun getRegArray():Array<String> {
        val startEndDate = getStartEndRegDate()
        val mStrList = mutableListOf<String>()
        mStrList.add(0, "")
        var x = startEndDate.first
        while (x <= startEndDate.second) {
            val sdf = SimpleDateFormat("yyyy/MM", Locale.JAPAN)
            val addDate = sdf.format(x)
            mStrList.add(addDate)
            val calendar = Calendar.getInstance()
            calendar.time = x
            calendar.add(Calendar.MONTH, 1)
            x = calendar.time
        }
        return mStrList.toTypedArray()
    }

    private fun getStartEndRegDate(): Pair<Date, Date> {
        val startDate = Date(mainRepository.getMinDate())
        val endDate = Date(mainRepository.getMaxDate())
        return Pair(startDate, endDate)
    }
}
