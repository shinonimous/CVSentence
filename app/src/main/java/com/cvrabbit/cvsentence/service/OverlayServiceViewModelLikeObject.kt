/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.service

import com.cvrabbit.cvsentence.model.db.WordEntity
import com.cvrabbit.cvsentence.model.repository.MainRepository
import com.cvrabbit.cvsentence.util.lang.GoogleTextToSpeech
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * I searched for a way to make a view model for a service,
 * but there are some descriptions that it's not a good practice to make a view model for a service.
 * https://github.com/android/architecture-components-samples/issues/137
 *
 * Therefore, although this class holds methods that are usually implemented in a view model class,
 * it's not an actual view model class.
 */

private const val TAG = "OverlayServiceViewModel"

class OverlayViewModelLikeObject @Inject constructor(
    private val mainRepository: MainRepository,
    val textToSpeech: GoogleTextToSpeech
) {

    fun searchWord(requestWord: String) = mainRepository.searchWord(requestWord)

    fun createNewWordEntity(wordEntity: WordEntity) =
        CoroutineScope(IO).launch {
            mainRepository.insertWord(wordEntity)
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

}