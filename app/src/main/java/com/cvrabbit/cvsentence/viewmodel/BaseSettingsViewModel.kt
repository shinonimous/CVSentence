/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import com.cvrabbit.cvsentence.model.db.Reference
import com.cvrabbit.cvsentence.model.db.Word
import com.cvrabbit.cvsentence.model.preferences.PreferenceAccess
import com.cvrabbit.cvsentence.model.repository.MainRepository
import com.cvrabbit.cvsentence.service.OverlayView
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.createObject
import io.realm.kotlin.where

class BaseSettingsViewModel @ViewModelInject constructor(
    application: Application,
    val mainRepository: MainRepository
): AndroidViewModel(application) {
    private var realm = Realm.getDefaultInstance()

    // When user create new reference in BaseSettings Page, use this method.
    fun createNewReference(reference: String) {
        // for room (Shold be coroutine)
        // mainRepository.insertReference(ReferenceEntity(reference))

        // for realm
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

    fun getFloatingPosition(): FloatingPosition {
        return if (PreferenceAccess(getApplication()).getFloatingPosition()) {
            FloatingPosition.RIGHT
        } else {
            FloatingPosition.LEFT
        }
    }

    fun saveFloatingPosition(intendedFloatingPosition: FloatingPosition) {
        PreferenceAccess(getApplication()).saveIfFloatingRight(intendedFloatingPosition == FloatingPosition.RIGHT)
    }

    fun changeFloatingVisibility() {
        if (OverlayView.overlayView != null) {
            val overlayView = OverlayView.overlayView!!
            overlayView.updateLayout()
        }
    }
}

enum class FloatingPosition {
    LEFT, RIGHT
}