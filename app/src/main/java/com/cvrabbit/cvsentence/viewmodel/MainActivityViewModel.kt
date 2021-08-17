/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.viewmodel

import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cvrabbit.cvsentence.model.db.WordEntity
import com.cvrabbit.cvsentence.model.repository.MainRepository
import com.cvrabbit.cvsentence.util.transition.Event
import com.cvrabbit.cvsentence.view.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "MainActivityViewModel"

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    var focusReference: String? = null

    val observableReferences = mainRepository.getAllReferences()

    val navigateToFragment: LiveData<Event<FragmentNavigationRequest>>
        get() = _navigateToFragment
    private val _navigateToFragment = MutableLiveData<Event<FragmentNavigationRequest>>()

    val navigateToDialogFragment: LiveData<Event<DialogFragmentNavigationRequest>>
        get() = _navigateToDialogFragment
    private val _navigateToDialogFragment = MutableLiveData<Event<DialogFragmentNavigationRequest>>()

    /*******
     * Fragment Transition Functions
     */
    // When word card is clicked
    fun wordCardClicked(word: WordEntity) {
        Log.d(TAG, "wordCardClicked is Running: WordEntity's id: ${word.id}")
        val wordDetail = WordDetail.newInstance(word)
        showFragment(wordDetail)
    }

    // When back button is clicked
    fun backToList() {
        Log.d(TAG, "backToList is Running")
        showFragment(WordsList.newInstance())
    }

    // When Sort Settings button is clicked
    fun openSortSetting(ifAllWordsDeleted: Boolean, allReferences: List<String>) {
        Log.d(TAG, "openSortSetting is Running: ifAllWordsDeleted: $ifAllWordsDeleted")
        if(!ifAllWordsDeleted) {
            showFragment(SortSettings.newInstance(allReferences))
        }
    }

    // When base Settings button is clicked
    fun openBaseSetting() {
        Log.d(TAG, "openBaseSetting is Running")
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
        // When testing FirstTimeGuidance, remove "!".
        if(!mainRepository.getIfShowMainFirstTime()) {
            backToList()
            return
        }
        showDialogFragment(FirstTimeGuidance.newInstance())
    }

    fun setIfShowMainFirstTime() = mainRepository.setIfShowMainFirstTime()

    // When Twitter Button clicked
    fun openTwitterDialog(word: WordEntity) {
        showDialogFragment(Twitter.newInstance(word))
    }

    fun getTwitterAccessToken() = mainRepository.getTwitterAccessToken()
    fun saveTwitterAccessToken(token: String, tokenSecret: String) = mainRepository.saveTwitterAccessToken(token, tokenSecret)

    private fun showDialogFragment(fragment: DialogFragment, tag: String? = null) {
        _navigateToDialogFragment.value = Event(DialogFragmentNavigationRequest(fragment, tag))
    }

    /*******
     * Showing Reference Spinner Functions
     */

    // use when showing reference spinner
    fun getAllReferencesAsArray(referencesFromDB: List<String>): Array<String> {
        val reference = mutableListOf<String>()
        reference.add("")
        reference.addAll(referencesFromDB)
        return reference.toTypedArray()
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