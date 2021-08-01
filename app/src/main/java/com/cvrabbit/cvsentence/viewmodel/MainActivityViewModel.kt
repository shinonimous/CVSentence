/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.viewmodel

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cvrabbit.cvsentence.model.db.Reference
import com.cvrabbit.cvsentence.model.db.Word
import com.cvrabbit.cvsentence.model.repository.MainRepository
import com.cvrabbit.cvsentence.util.transition.Event
import com.cvrabbit.cvsentence.view.*
import io.realm.Realm
import io.realm.kotlin.where

private const val TAG = "MainActivityViewModel"

class MainActivityViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    var focusWord: Word? = null
    var focusReference: String? = null

    val navigateToFragment: LiveData<Event<FragmentNavigationRequest>>
        get() = _navigateToFragment
    private val _navigateToFragment = MutableLiveData<Event<FragmentNavigationRequest>>()

    val navigateToDialogFragment: LiveData<Event<DialogFragmentNavigationRequest>>
        get() = _navigateToDialogFragment
    private val _navigateToDialogFragment = MutableLiveData<Event<DialogFragmentNavigationRequest>>()

    private var realm = Realm.getDefaultInstance()

    /*******
     * Fragment Transition Functions
     */
    // When word card is clicked
    fun wordCardClicked(word: Word) {
        focusWord = word
        showFragment(WordDetail.newInstance())
    }

    // When back button is clicked
    fun backToList() {
        showFragment(WordsList.newInstance())
    }

    // When Sort Settings button is clicked
    fun openSortSetting() {
        if(!ifAllWordsDeleted()) {
            showFragment(SortSettings.newInstance())
        }
    }

    private fun ifAllWordsDeleted(): Boolean {
        val words = realm.where<Word>().findAll()
        return words.isEmpty()
    }

    // When base Settings button is clicked
    fun openBaseSetting() {
        showFragment(BaseSettings.newInstance())
    }

    private fun showFragment(fragment: Fragment, backStack: Boolean = true, tag: String? = null) {
        _navigateToFragment.value = Event(FragmentNavigationRequest(fragment, backStack, tag))
    }

    /*******
     * Fragment Dialog Functions
     */
    // When first time of showing main
    fun openFirstTimeGuidance() {
        if(mainRepository.getIfShowMainFirstTime()) {
            backToList()
            return } // When testing FirstTimeGuidance, comment out this.
        showDialogFragment(FirstTimeGuidance.newInstance())
    }

    fun setIfShowMainFirstTime() = mainRepository.setIfShowMainFirstTime()

    // When Twitter Button clicked
    fun openTwitterDialog() {
        showDialogFragment(Twitter.newInstance())
    }

    fun getTwitterAccessToken() = mainRepository.getTwitterAccessToken()
    fun saveTwitterAccessToken(token: String, tokenSecret: String) = mainRepository.saveTwitterAccessToken(token, tokenSecret)

    private fun showDialogFragment(fragment: DialogFragment, tag: String? = null) {
        _navigateToDialogFragment.value = Event(DialogFragmentNavigationRequest(fragment, tag))
    }

    /*******
     * Showing Reference Spinner Functions
     */
    // check before showing reference spinner
    fun ifReferenceEmpty(): Boolean {
        val references = realm.where<Reference>().findAll()
        return references.isEmpty()
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
}

data class FragmentNavigationRequest(
    val fragment: Fragment,
    val backStack: Boolean = false,
    val tag: String? = null
)

data class DialogFragmentNavigationRequest(
    val fragment: DialogFragment,
    val tag: String? = null
)