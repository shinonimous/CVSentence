/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.viewmodel

import android.util.Log
import com.cvrabbit.cvsentence.model.db.Word
import com.cvrabbit.cvsentence.model.db.WordEntity
import com.cvrabbit.cvsentence.model.repository.MainRepository
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * I searched for a way to make a view model for a service,
 * but there are some descriptions that it's not a good practice to make a view model for a service.
 * https://github.com/android/architecture-components-samples/issues/137
 *
 * Also, sharing a view model between activities is not recommended,
 * therefore I thought I shouldn't reuse mainActivityViewModel within a service class.
 * https://stackoverflow.com/questions/62845238/how-can-i-share-viewmodel-between-activities#:~:text=You%20can't%20share%20a,per%20the%20Single%20Activity%20talk.
 *
 * Therefore, I decided to make this class to hold methods that are otherwise implemented in a view model class.
 */

private const val TAG = "OverlayViewModel"

class OverlayViewModelLikeObject @Inject constructor(
    private val mainRepository: MainRepository
) {

    private var realm = Realm.getDefaultInstance()

    fun createNewWordEntity(wordEntity: WordEntity) =
        runBlocking {
            withContext(IO){
                mainRepository.insertWord(wordEntity)
            }
        }

    fun ifSameWordExist(wordEntity: WordEntity):Boolean =
        runBlocking {
            withContext(IO){
                val words = mainRepository.getCertainWord(wordEntity.word)
                if (words.isNotEmpty()) {
                    for(i in words) {
                        i.tryAddSameWordCount += 1
                        mainRepository.updateWord(i)
                    }
                    true
                } else {
                    false
                }
            }
        }

    fun getFloatingPosition() = mainRepository.getFloatingPosition()

    fun createNewWord(
        newWord: String,
        mainMeaning: String,
        verb: String,
        noun: String,
        adjective: String,
        adverb: String,
        expression: String,
        prefix: String,
        suffix: String,
        others: String,
        reference: String
    ): Word {

        Log.d(TAG, "createNewWord is Running! reference: $reference")

        val sameWord = getIfSameWordExist(newWord)
        if(sameWord != null){
            return sameWord
        }

        var word = Word()

        realm.executeTransaction { db: Realm ->
            val maxId = db.where<Word>().max("id")
            val nextId = (maxId?.toLong() ?: 0L) + 1L
            word = db.createObject<Word>(nextId)
            word.word = newWord
            word.mainMeaning = mainMeaning
            word.verb = verb
            word.noun = noun
            word.adjective = adjective
            word.adverb = adverb
            word.expression = expression
            word.prefix = prefix
            word.suffix = suffix
            word.others = others
            word.reference = reference
        }
        return word
    }

    private fun getIfSameWordExist(checkWord: String): Word? {
        var word: Word? = null
        realm.executeTransaction { db: Realm ->
            word = db.where<Word>().equalTo("word", checkWord).findFirst()
            word?.let {
                it.tryAddSameWordCount += 1
            }
        }
        return word
    }

    fun closeRealm() {
        realm.close()
    }
}