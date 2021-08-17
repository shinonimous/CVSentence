/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvrabbit.cvsentence.model.db.ReferenceEntity
import com.cvrabbit.cvsentence.model.repository.MainRepository
import com.cvrabbit.cvsentence.service.OverlayView
import com.cvrabbit.cvsentence.util.constant.FloatingPosition
import com.cvrabbit.cvsentence.util.device.CSVExport
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "BaseSettingsViewModel"

@HiltViewModel
class BaseSettingsViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    private val csvSavedDir: MutableLiveData<String> = MutableLiveData("")
    val observableCsvSavedDir: LiveData<String> = csvSavedDir

    // When user creates new reference in BaseSettings Page, use this method.
    fun createNewReference(reference: ReferenceEntity) {
        Log.d(TAG, "createNewReference is Running")
        viewModelScope.launch {
            if (reference.reference != "") {
                if(mainRepository.getCertainReference(reference.reference).isEmpty()) {
                    mainRepository.insertReference(reference)
                }
            }
        }
    }

    // When user clicks on "CSV Export", use this method.
    fun csvExport(context: Context) = viewModelScope.launch {
        val savedDir: String = CSVExport(context).saveWordsAsCSV(mainRepository.getAllWords())
        csvSavedDir.postValue(savedDir)
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