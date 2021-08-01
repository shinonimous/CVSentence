package com.cvrabbit.cvsentence.model.repository

import com.cvrabbit.cvsentence.model.db.ReferenceEntity
import com.cvrabbit.cvsentence.model.db.WordDAO
import com.cvrabbit.cvsentence.model.db.WordEntity
import com.cvrabbit.cvsentence.model.preferences.PreferenceAccess
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val wordDAO: WordDAO,
    private val pref: PreferenceAccess
) {

    /**
     * Room Related Operation
     */
    suspend fun insertWord(wordEntity: WordEntity) = wordDAO.insertWord(wordEntity)

    suspend fun insertReference(referenceEntity: ReferenceEntity) = wordDAO.insertReference(referenceEntity)

    suspend fun deleteWord(wordEntity: WordEntity) = wordDAO.deleteWord(wordEntity)

    suspend fun getAllWords() = wordDAO.getAllWords()

    fun getAllWordsSortedBySortedByDateDesc(
        greens: List<Boolean>,
        minDS: Float,
        maxDS: Float,
        minDate: Long,
        maxDate: Long,
        references: List<String>
    ) = wordDAO.getAllWordsSortedByDateDesc(
        greens, minDS, maxDS, minDate, maxDate, references
    )

    fun getAllWordsSortedBySortedByDateAsc(
        greens: List<Boolean>,
        minDS: Float,
        maxDS: Float,
        minDate: Long,
        maxDate: Long,
        references: List<String>
    ) = wordDAO.getAllWordsSortedByDateAsc(
        greens, minDS, maxDS, minDate, maxDate, references
    )

    fun getAllWordsSortedBySortedByDSDesc(
        greens: List<Boolean>,
        minDS: Float,
        maxDS: Float,
        minDate: Long,
        maxDate: Long,
        references: List<String>
    ) = wordDAO.getAllWordsSortedByDSDesc(
        greens, minDS, maxDS, minDate, maxDate, references
    )

    fun getAllWordsSortedBySortedByDSAsc(
        greens: List<Boolean>,
        minDS: Float,
        maxDS: Float,
        minDate: Long,
        maxDate: Long,
        references: List<String>
    ) = wordDAO.getAllWordsSortedByDSAsc(
        greens, minDS, maxDS, minDate, maxDate, references
    )

    fun getAllWordsSortedBySortedByWordDesc(
        greens: List<Boolean>,
        minDS: Float,
        maxDS: Float,
        minDate: Long,
        maxDate: Long,
        references: List<String>
    ) = wordDAO.getAllWordsSortedByWordDesc(
        greens, minDS, maxDS, minDate, maxDate, references
    )

    fun getAllWordsSortedBySortedByWordAsc(
        greens: List<Boolean>,
        minDS: Float,
        maxDS: Float,
        minDate: Long,
        maxDate: Long,
        references: List<String>
    ) = wordDAO.getAllWordsSortedByWordAsc(
        greens, minDS, maxDS, minDate, maxDate, references
    )

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

}

enum class FloatingPosition {
    LEFT, RIGHT
}
