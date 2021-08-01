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
import com.cvrabbit.cvsentence.model.db.Reference
import com.cvrabbit.cvsentence.model.db.Word
import com.cvrabbit.cvsentence.model.repository.MainRepository
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.where
import java.text.SimpleDateFormat
import java.util.*

class SortSettingsViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
): ViewModel() {
    private var realm = Realm.getDefaultInstance()

    // get the range of registered date
    private fun getStartEndDate(): Pair<Date?, Date?> {
        val startID = realm.where<Word>().min("id")?.toLong()
        val startWord = realm.where<Word>().equalTo("id", startID).findFirst()
        val startDate = startWord?.registeredDate
        val endID = realm.where<Word>().max("id")?.toLong()
        val endWord = realm.where<Word>().equalTo("id", endID).findFirst()
        val endDate = endWord?.registeredDate
        return Pair(startDate, endDate)
    }

    // sort word by sort pattern
    fun sortBySortPattern(sortPattern: SortPattern, words: RealmResults<Word>): OrderedRealmCollection<Word> {
        return words.sort(sortPattern.fieldName, sortPattern.ascOrDes)
    }

    // filter word by filter pattern
    fun filterByFilterPattern(filterPatterns: List<FilterPattern>): RealmResults<Word> {
        var updateWord = realm.where<Word>()
        for (i in filterPatterns) {
            when(i) {
                FilterPattern.DS_RANGE -> {
                    updateWord = updateWord.between(i.fieldName, i.dsRangeFilter.first, i.dsRangeFilter.second)
                }
                FilterPattern.GREEN -> {
                    if (i.greenFilter) {
                        updateWord = updateWord.equalTo(i.fieldName,i.greenFilter)
                    }
                }
                FilterPattern.REG_DATE_RANGE -> {
                    updateWord = updateWord.between(i.fieldName, i.regDateRangeFilter.first, i.regDateRangeFilter.second)
                }
                FilterPattern.REFERENCE -> {
                    updateWord = updateWord.equalTo(i.fieldName, i.reference)
                }
            }
        }
        return updateWord.findAllAsync()
    }

    // use when showing reference spinner
    fun getAllReferencesAsArrayString(): Array<String> {
        val reference = mutableListOf<String>()
        val refs = realm.where<Reference>().findAll()
        reference.add("")
        if (refs.isNotEmpty()) {
            for (i in refs) {
                reference.add(i.reference)
            }
        }
        return reference.toTypedArray()
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    fun getRegArray():Array<String> {
        val startEndDate = getStartEndDate()
        val mStrList = mutableListOf<String>()
        mStrList.add(0, "")
        if (startEndDate.first != null) {
            var x = startEndDate.first!!
            while (x <= startEndDate.second!!) {
                val sdf = SimpleDateFormat("yyyy/MM", Locale.JAPAN)
                val addDate = sdf.format(x)
                mStrList.add(addDate)
                val calendar = Calendar.getInstance()
                calendar.time = x
                calendar.add(Calendar.MONTH, 1)
                x = calendar.time
            }
        }
        return mStrList.toTypedArray()
    }

}

enum class SortPattern() {
    REG_DATE_DES {
        override val arrayPosition = 0
        override var strValue = ""
        override val fieldName = "id"
        override val ascOrDes = Sort.DESCENDING
    },
    REG_DATE_ASC {
        override val arrayPosition = 1
        override var strValue = ""
        override val fieldName = "id"
        override val ascOrDes = Sort.ASCENDING
    },
    DS_DES {
        override val arrayPosition = 2
        override var strValue = ""
        override val fieldName = "difficultyScore"
        override val ascOrDes = Sort.DESCENDING
    },
    DS_ASC {
        override val arrayPosition = 3
        override var strValue = ""
        override val fieldName = "difficultyScore"
        override val ascOrDes = Sort.ASCENDING
    },
    RRT_ASC {
        override val arrayPosition = 4
        override var strValue = ""
        override val fieldName = "recommendedRecurTiming"
        override val ascOrDes = Sort.ASCENDING
    },
    RRT_DES {
        override val arrayPosition = 5
        override var strValue = ""
        override val fieldName = "recommendedRecurTiming"
        override val ascOrDes = Sort.DESCENDING
    },
    ABC_ASC {
        override val arrayPosition = 6
        override var strValue = ""
        override val fieldName = "word"
        override val ascOrDes = Sort.ASCENDING
    },
    ABC_DES {
        override val arrayPosition = 7
        override var strValue = ""
        override val fieldName = "word"
        override val ascOrDes = Sort.DESCENDING
    };
    abstract val arrayPosition: Int
    abstract var strValue: String
    abstract val fieldName: String
    abstract val ascOrDes: Sort

    companion object {
        fun setSortTypeStrArray(sortTypeStrArray: Array<String>){
            REG_DATE_DES.strValue = sortTypeStrArray[REG_DATE_DES.arrayPosition]
            REG_DATE_ASC.strValue = sortTypeStrArray[REG_DATE_ASC.arrayPosition]
            DS_DES.strValue = sortTypeStrArray[DS_DES.arrayPosition]
            DS_ASC.strValue = sortTypeStrArray[DS_ASC.arrayPosition]
            RRT_DES.strValue = sortTypeStrArray[RRT_DES.arrayPosition]
            RRT_ASC.strValue = sortTypeStrArray[RRT_ASC.arrayPosition]
            ABC_ASC.strValue = sortTypeStrArray[ABC_ASC.arrayPosition]
            ABC_DES.strValue = sortTypeStrArray[ABC_DES.arrayPosition]
        }
        fun getSortPatternByStrValue(strValue: String):SortPattern {
            var returnPattern = REG_DATE_DES
            when(strValue) {
                REG_DATE_DES.strValue -> {
                    returnPattern = REG_DATE_DES
                }
                REG_DATE_ASC.strValue -> {
                    returnPattern = REG_DATE_ASC
                }
                DS_DES.strValue -> {
                    returnPattern = DS_DES
                }
                DS_ASC.strValue -> {
                    returnPattern = DS_ASC
                }
                RRT_DES.strValue -> {
                    returnPattern = RRT_DES
                }
                RRT_ASC.strValue -> {
                    returnPattern = RRT_ASC
                }
                ABC_ASC.strValue -> {
                    returnPattern = ABC_ASC
                }
                ABC_DES.strValue -> {
                    returnPattern = ABC_DES
                }
            }
            return returnPattern
        }
    }
}

enum class FilterPattern() {
    DS_RANGE {
        override val fieldName = "difficultyScore"
        override lateinit var dsRangeFilter: Pair<Float,Float>
        override var greenFilter = false
        override lateinit var regDateRangeFilter: Pair<Date, Date>
        override var reference = ""
    },
    GREEN {
        override val fieldName = "green"
        override lateinit var dsRangeFilter: Pair<Float,Float>
        override var greenFilter = false
        override lateinit var regDateRangeFilter: Pair<Date, Date>
        override var reference = ""
    },
    REG_DATE_RANGE {
        override val fieldName = "registeredDate"
        override lateinit var dsRangeFilter: Pair<Float,Float>
        override var greenFilter = false
        override lateinit var regDateRangeFilter: Pair<Date, Date>
        override var reference = ""
    },
    REFERENCE {
        override val fieldName = "reference"
        override lateinit var dsRangeFilter: Pair<Float,Float>
        override var greenFilter = false
        override lateinit var regDateRangeFilter: Pair<Date, Date>
        override var reference = ""
    }
    ;
    abstract val fieldName: String
    abstract var dsRangeFilter: Pair<Float, Float>
    abstract var greenFilter: Boolean
    abstract var regDateRangeFilter: Pair<Date, Date>
    abstract var reference: String
}
