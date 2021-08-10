/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.model.repository

import com.cvrabbit.cvsentence.model.db.ReferenceEntity
import com.cvrabbit.cvsentence.model.db.WordDAO
import com.cvrabbit.cvsentence.model.db.WordEntity
import com.cvrabbit.cvsentence.model.internet.WordSearch
import com.cvrabbit.cvsentence.model.preferences.PreferenceAccess
import com.cvrabbit.cvsentence.util.constant.FloatingPosition
import com.cvrabbit.cvsentence.util.constant.SortPattern
import com.cvrabbit.cvsentence.util.data.WordFilter
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val wordSearch: WordSearch,
    private val wordDAO: WordDAO,
    private val pref: PreferenceAccess
) {

    /**
     * Internet Related Operation
     */
    fun searchWord(requestWord: String) = wordSearch.searchWord(requestWord)

    /**
     * Room Related Operation
     */
    suspend fun insertWord(wordEntity: WordEntity) = wordDAO.insertWord(wordEntity)

    suspend fun insertReference(referenceEntity: ReferenceEntity) = wordDAO.insertReference(referenceEntity)

    suspend fun deleteWord(wordEntity: WordEntity) = wordDAO.deleteWord(wordEntity)

    suspend fun getAllWords() = wordDAO.getAllWords()

    suspend fun updateWord(wordEntity: WordEntity) = wordDAO.updateWord(wordEntity)

    suspend fun getCertainWord(word: String) = wordDAO.getCertainWord(word)

    fun getLiveWordEntity(id: Int) = wordDAO.getLiveWordEntity(id)

    fun getAllWordsSortedByDateDesc() = wordDAO.getAllWordsSortedByDateDesc()

    fun getWordsSortedByDateDesc(
        greens: List<Boolean>,
        minDS: Float,
        maxDS: Float,
        minDate: Long,
        maxDate: Long,
        references: List<String>
    ) = wordDAO.getWordsSortedByDateDesc(
        greens, minDS, maxDS, minDate, maxDate, references
    )

    fun getWordsSortedByDateAsc(
        greens: List<Boolean>,
        minDS: Float,
        maxDS: Float,
        minDate: Long,
        maxDate: Long,
        references: List<String>
    ) = wordDAO.getWordsSortedByDateAsc(
        greens, minDS, maxDS, minDate, maxDate, references
    )

    fun getWordsSortedByDSDesc(
        greens: List<Boolean>,
        minDS: Float,
        maxDS: Float,
        minDate: Long,
        maxDate: Long,
        references: List<String>
    ) = wordDAO.getWordsSortedByDSDesc(
        greens, minDS, maxDS, minDate, maxDate, references
    )

    fun getWordsSortedByDSAsc(
        greens: List<Boolean>,
        minDS: Float,
        maxDS: Float,
        minDate: Long,
        maxDate: Long,
        references: List<String>
    ) = wordDAO.getWordsSortedByDSAsc(
        greens, minDS, maxDS, minDate, maxDate, references
    )

    fun getWordsSortedByWordDesc(
        greens: List<Boolean>,
        minDS: Float,
        maxDS: Float,
        minDate: Long,
        maxDate: Long,
        references: List<String>
    ) = wordDAO.getWordsSortedByWordDesc(
        greens, minDS, maxDS, minDate, maxDate, references
    )

    fun getWordsSortedByWordAsc(
        greens: List<Boolean>,
        minDS: Float,
        maxDS: Float,
        minDate: Long,
        maxDate: Long,
        references: List<String>
    ) = wordDAO.getWordsSortedByWordAsc(
        greens, minDS, maxDS, minDate, maxDate, references
    )

    fun getMinDate() = wordDAO.getMinDate()

    fun getMaxDate() = wordDAO.getMaxDate()

    fun getMinDS() = wordDAO.getMinDS()

    fun getMaxDS() = wordDAO.getMaxDS()

    fun getAllReferences() = wordDAO.getAllReferences()

    /**
     * Preference Related Operation
     */

    fun getFloatingPosition(): FloatingPosition {
        return if (pref.getFloatingPosition()) {
            FloatingPosition.RIGHT
        } else {
            FloatingPosition.LEFT
        }
    }

    fun saveFloatingPosition(intendedFloatingPosition: FloatingPosition) {
        pref.saveIfFloatingRight(intendedFloatingPosition == FloatingPosition.RIGHT)
    }

    fun getOnSelectWordSoundSetting() = pref.getOnSelectWordSoundSetting()

    fun getOnSelectMeaningSoundSetting() = pref.getOnSelectMeaningSoundSetting()

    fun getOnDemandWordSoundSetting() = pref.getOnDemandWordSoundSetting()

    fun getOnDemandMeaningSoundSetting() = pref.getOnDemandMeaningSoundSetting()

    fun saveOnSelectWordSoundSetting(isChecked: Boolean) = pref.saveOnSelectWordSoundSetting(isChecked)

    fun saveOnSelectMeaningSoundSetting(isChecked: Boolean) = pref.saveOnSelectMeaningSoundSetting(isChecked)

    fun saveOnDemandWordSoundSetting(isChecked: Boolean) = pref.saveOnDemandWordSoundSetting(isChecked)

    fun saveOnDemandMeaningSoundSetting(isChecked: Boolean) = pref.saveOnDemandMeaningSoundSetting(isChecked)

    fun getIfShowMainFirstTime() = pref.getIfShowMainFirstTime()

    fun setIfShowMainFirstTime() = pref.setIfShowMainFirstTime()

    fun getTwitterAccessToken() = pref.getTwitterAccessToken()

    fun saveTwitterAccessToken(token: String, tokenSecret: String) = pref.saveTwitterAccessToken(token, tokenSecret)

    fun saveSortPattern(sortPattern: SortPattern) = pref.saveSortPattern(sortPattern)

    fun getSortPattern() = pref.getSortPattern()

    fun saveFilter(filter: WordFilter) = pref.saveFilter(filter)

    fun getFilter() = pref.getFilter()

}

