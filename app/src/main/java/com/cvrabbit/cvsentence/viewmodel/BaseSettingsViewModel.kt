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
import com.cvrabbit.cvsentence.model.db.ReferenceEntity
import com.cvrabbit.cvsentence.model.db.WordEntity
import com.cvrabbit.cvsentence.model.repository.MainRepository
import com.cvrabbit.cvsentence.service.OverlayView
import com.cvrabbit.cvsentence.util.constant.FloatingPosition
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

private const val TAG = "BaseSettingsViewModel"

class BaseSettingsViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    // When user creates new reference in BaseSettings Page, use this method.
    fun createNewReference(reference: ReferenceEntity) {
        viewModelScope.launch {
            if (reference.reference != "") {
                mainRepository.insertReference(reference)
            }
        }
    }

    // When user clicks on "CSV Export", use this method.
    fun getAllWords(): List<WordEntity> = runBlocking{
        withContext(viewModelScope.coroutineContext) {
            mainRepository.getAllWords()
        }
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