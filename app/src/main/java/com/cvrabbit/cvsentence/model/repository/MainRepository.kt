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

    suspend fun updateWord(wordEntity: WordEntity) = wordDAO.updateWord(wordEntity)

    suspend fun getCertainWord(word: String) = wordDAO.getCertainWord(word)

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

}

enum class FloatingPosition {
    LEFT, RIGHT
}

enum class SortPattern {
    DATE_DESC { override var strValue = ""},
    DATE_ASC {override var strValue = ""},
    DS_DESC {override var strValue = ""},
    DS_ASC {override var strValue = ""},
    WORD_DESC {override var strValue = ""},
    WORD_ASC {override var strValue = ""};

    abstract var strValue: String

    companion object {
        fun setSortTypeStrArray(sortTypeStrArray: Array<String>) {
            DATE_DESC.strValue = sortTypeStrArray[0]
            DATE_ASC.strValue = sortTypeStrArray[1]
            DS_DESC.strValue = sortTypeStrArray[2]
            DS_ASC.strValue = sortTypeStrArray[3]
            WORD_ASC.strValue = sortTypeStrArray[4]
            WORD_DESC.strValue = sortTypeStrArray[5]
        }

        fun getSortPatternByStrValue(strValue: String): SortPattern {
            var returnPattern = DATE_DESC
            when (strValue) {
                DATE_DESC.strValue -> {returnPattern = DATE_DESC}
                DATE_ASC.strValue -> {returnPattern = DATE_ASC}
                DS_DESC.strValue -> {returnPattern = DS_DESC}
                DS_ASC.strValue -> {returnPattern = DS_ASC}
                WORD_ASC.strValue -> {returnPattern = WORD_ASC}
                WORD_DESC.strValue -> {returnPattern = WORD_DESC}
            }
            return returnPattern
        }
    }
}

data class WordFilter(
    var green: Boolean = false,
    var minDS: Float,
    var maxDS: Float,
    var startDate: Long,
    var endDate: Long,
    var reference: List<String>
)
