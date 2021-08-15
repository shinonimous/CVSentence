/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cvrabbit.cvsentence.model.repository.MainRepository
import com.cvrabbit.cvsentence.util.constant.SortPattern
import com.cvrabbit.cvsentence.util.data.WordFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

private const val TAG = "SortSettingsViewModel"

@HiltViewModel
class SortSettingsViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    fun saveSortPattern(sortPattern: SortPattern) = mainRepository.saveSortPattern(sortPattern)

    fun saveFilter(wordFilter: WordFilter) = mainRepository.saveFilter(wordFilter)

    // use when showing reg date spinner
    fun getRegArray(startEndDate: Pair<Date, Date>):Array<String> {
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
}
