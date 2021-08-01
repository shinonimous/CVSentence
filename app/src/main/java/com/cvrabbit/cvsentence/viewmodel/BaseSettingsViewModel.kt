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
import com.cvrabbit.cvsentence.model.db.Reference
import com.cvrabbit.cvsentence.model.db.ReferenceEntity
import com.cvrabbit.cvsentence.model.db.Word
import com.cvrabbit.cvsentence.model.db.WordEntity
import com.cvrabbit.cvsentence.model.repository.FloatingPosition
import com.cvrabbit.cvsentence.model.repository.MainRepository
import com.cvrabbit.cvsentence.service.OverlayView
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class BaseSettingsViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    /**
     * for room
     */

    // When user creates new reference in BaseSettings Page, use this method.
    fun createNewReference(reference: ReferenceEntity) {
        viewModelScope.launch {
            mainRepository.insertReference(reference)
        }
    }

    // When user clicks on "CSV Export", use this method.
    fun getAllWords(): List<WordEntity> = runBlocking{
        withContext(viewModelScope.coroutineContext) {
            mainRepository.getAllWords()
        }
    }

    /**
     * for realm
     */

    private var realm = Realm.getDefaultInstance()
    // When user create new reference in BaseSettings Page, use this method.
    fun createNewReference(reference: String) {
        if(ifSameReferenceExist(reference)){ return }
        realm.executeTransaction { db: Realm ->
            val maxId = db.where<Reference>().max("id")
            val nextId = (maxId?.toLong() ?: 0L) + 1L
            val ref = db.createObject<Reference>(nextId)
            ref.reference = reference
        }
    }

    private fun ifSameReferenceExist(reference: String): Boolean {
        var ref: Reference? = null
        if (reference == "") { return true }
        realm.executeTransaction { db: Realm ->
            ref = db.where<Reference>().equalTo("reference", reference).findFirst()
        }
        return (ref != null)
    }

    fun getAllWordsSync(): RealmResults<Word> {
        return realm.where<Word>().findAll()
    }

    // release the realm
    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    fun getFloatingPosition(): FloatingPosition = mainRepository.getFloatingPosition()
    fun saveFloatingPosition(intendedFloatingPosition: FloatingPosition) = mainRepository.saveFloatingPosition(intendedFloatingPosition)
    fun getOnSelectWordSoundSetting() = mainRepository.getOnSelectWordSoundSetting()
    fun getOnSelectMeaningSoundSetting() = mainRepository.getOnSelectMeaningSoundSetting()
    fun getOnDemandWordSoundSetting() = mainRepository.getOnDemandWordSoundSetting()
    fun getOnDemandMeaningSoundSetting() = mainRepository.getOnDemandMeaningSoundSetting()
    fun saveOnSelectWordSoundSetting(isChecked: Boolean) = mainRepository.saveOnSelectWordSoundSetting(isChecked)
    fun saveOnSelectMeaningSoundSetting(isChecked: Boolean) = mainRepository.saveOnSelectMeaningSoundSetting(isChecked)
    fun saveOnDemandWordSoundSetting(isChecked: Boolean) = mainRepository.saveOnDemandWordSoundSetting(isChecked)
    fun saveOnDemandMeaningSoundSetting(isChecked: Boolean) = mainRepository.saveOnDemandMeaningSoundSetting(isChecked)

    fun changeFloatingVisibility() {
        if (OverlayView.overlayView != null) {
            val overlayView = OverlayView.overlayView!!
            overlayView.updateLayout()
        }
    }
}