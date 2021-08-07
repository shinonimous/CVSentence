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
import androidx.lifecycle.viewModelScope
import com.cvrabbit.cvsentence.model.db.RRT
import com.cvrabbit.cvsentence.model.db.Word
import com.cvrabbit.cvsentence.model.db.WordEntity
import com.cvrabbit.cvsentence.model.repository.MainRepository
import com.cvrabbit.cvsentence.util.calendar.CalendarOperation
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.coroutines.launch
import java.util.*

class WordsListViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
): ViewModel() {

    /**
     * for room
     */
    fun deleteWordEntity(wordEntity: WordEntity) {
        viewModelScope.launch {
            mainRepository.deleteWord(wordEntity)
        }
    }


    /**
     * for realm
     */
    private lateinit var realm: Realm
    private lateinit var words: MutableList<Word>
    lateinit var data: OrderedRealmCollection<Word> // It is used in WordInListAdapter

    init {
        updateUI()
    }

    // sort the list and set it to "data"
    fun updateUI() {
        realm = Realm.getDefaultInstance()
        words = realm.where<Word>().findAll() //TODO Sort
        data = (words as RealmResults<Word>?)!!
    }

    /**
     * When user taps certain word card, use this method.
     * Updated properties:
     *  lookupCount, lastLookupDate, durationFromLastLookupTime, green
     */
    fun updateLookUp(id: Long): Word? {
        var word: Word? = null
        realm.executeTransaction { db: Realm ->
            word = db.where<Word>().equalTo("id", id).findFirst()
            word?.let {
                it.lookupCount += 1
                it.durationFromLastLookupTime = Date().time - it.lastLookupDate.time
                it.lastLookupDate = Date()
                it.green = false
            }
        }
        return word
    }

    // When certain word card color get green, use this method
    fun setGreen() {
        realm.executeTransaction {
            val words = it.where<Word>().findAll()
            if (words.isNotEmpty()) {
                for (i in words) {
                    i.green = CalendarOperation.minusDate(Date(), i.lastLookupDate) > RRT.getRRTValue(i.recommendedRecurTiming).longValue
                }
            }
        }
    }

    // Delete specific word
    fun deleteSpecificWord(id: Long) {
        realm.executeTransaction { db: Realm ->
            val word = db.where<Word>().equalTo("id", id).findFirst()
            word?.deleteFromRealm()
        }
    }

    // Check if all words deleted
    fun ifAllWordsDeleted(): Boolean {
        val words = realm.where<Word>().findAll()
        return words.isEmpty()
    }

    // get all words sync
    fun getAllWordsSync(): RealmResults<Word> {
        return realm.where<Word>().findAll()
    }

    // get all words async
    fun getAllWordsAsync(): RealmResults<Word> {
        val words = realm.where<Word>().sort("id", Sort.DESCENDING)
        return words.findAllAsync()
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}