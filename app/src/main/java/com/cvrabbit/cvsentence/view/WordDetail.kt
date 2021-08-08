/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.cvrabbit.cvsentence.R
import com.cvrabbit.cvsentence.databinding.FragmentWordDetailBinding
import com.cvrabbit.cvsentence.model.db.DS
import com.cvrabbit.cvsentence.model.db.RRT
import com.cvrabbit.cvsentence.model.db.WordEntity
import com.cvrabbit.cvsentence.util.calendar.CalendarOperation.durationMillisToDurationDateString
import com.cvrabbit.cvsentence.util.calendar.CalendarOperation.longDateToStringFormat
import com.cvrabbit.cvsentence.util.lang.GoogleTextToSpeech
import com.cvrabbit.cvsentence.viewmodel.MainActivityViewModel
import com.cvrabbit.cvsentence.viewmodel.WordDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "WordDetail"

@AndroidEntryPoint
class WordDetail(private val focusWord: MutableLiveData<WordEntity>) : Fragment(R.layout.fragment_word_detail) {
    private lateinit var binding: FragmentWordDetailBinding
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val wordDetailViewModel: WordDetailViewModel by viewModels()
    private var notUpdatedAfterOpenPage: Boolean = false

    @Inject
    lateinit var textToSpeech: GoogleTextToSpeech

    companion object {
        fun newInstance(focusWord: MutableLiveData<WordEntity>) = WordDetail(focusWord)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_word_detail, container, false)
        binding = FragmentWordDetailBinding.bind(view).apply {
            viewmodel = wordDetailViewModel
        }
        binding.lifecycleOwner = this.viewLifecycleOwner
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVisibility()
        wordDetailViewModel.updateLookup(focusWord)
        updateUI(focusWord.value!!)
        setListeners(focusWord.value!!)
        wordDetailViewModel.focusWord.observe(viewLifecycleOwner, {
            Log.d(TAG, "MutableLiveData<WordEntity> observer is Running")
            updateUI(it)
        })
    }

    // Define visibility when WordDetailFragment is Firstly opened
    private fun initVisibility() {
        binding.wordDetail.isVisible = true
        binding.focusedWord.isVisible = true
        binding.wordMeaningCard.isVisible = true
        binding.mainMeaning.isVisible = true
        binding.micView.isVisible = wordDetailViewModel.ifSomeOfOnDemandSettingsOn()
        binding.openDetail.isVisible = true
        binding.openDetail.text = context?.getString(R.string.wda_opendetail)
        binding.verbLabel.isVisible = false
        binding.verbDetail.isVisible = false
        binding.nounLabel.isVisible = false
        binding.nounDetail.isVisible = false
        binding.adjectiveLabel.isVisible = false
        binding.adjectiveDetail.isVisible = false
        binding.adverbLabel.isVisible = false
        binding.adverbDetail.isVisible = false
        binding.prefixLabel.isVisible = false
        binding.prefixDetail.isVisible = false
        binding.suffixLabel.isVisible = false
        binding.suffixDetail.isVisible = false
        binding.expLabel.isVisible = false
        binding.expDetail.isVisible = false
        binding.otherLabel.isVisible = false
        binding.otherDetail.isVisible = false
        binding.wordProgressCard.isVisible = true
    }

    // Set data for Word Detail Page
    private fun updateUI(word: WordEntity) {
        changeTextSizeDueToTextLength(binding.focusedWord, word.word)
        binding.mainMeaning.text = word.mainMeaning
        binding.notRememberedCountNum.text = word.notRememberedCount.toString()
        binding.rememberedCountNum.text = word.rememberedCount.toString()
        binding.rrtFromDate.text = if (notUpdatedAfterOpenPage) {
            longDateToStringFormat(word.lastLookupDate + word.durationFromLastLookupTime)
        } else {
            longDateToStringFormat(word.lastLookupDate)
        }
        binding.rrtDate.text = RRT.getRRTValue(word.recommendedRecurTiming).value
        val dsValue = DS.getDSValue(word.difficultyScore).value
        binding.dsNum.text = dsValue.toInt().toString()
        binding.dsDec.text = ((dsValue - dsValue.toInt()) * 10).toInt().toString()
        binding.lookupCountNum.text = word.lookupCount.toString()
        binding.durationFromLastLookupDateNum.text = durationMillisToDurationDateString(word.durationFromLastLookupTime)
        binding.tryAddSameWordCountNum.text = word.tryAddSameWordCount.toString()
        binding.registeredDateStr.text = longDateToStringFormat(word.registeredDate)
        binding.referenceText.text = if (word.reference == "") {
            context?.getString(R.string.wda_no_reference_title)} else {word.reference}

        // RatingBar
        binding.ratingLinearLayout.removeAllViews()
        val fullStarNum = DS.getDSValue(word.difficultyScore).value.toInt()
        for (i in 1..fullStarNum) {
            val imageView = ImageView(context)
            imageView.setImageResource(R.drawable.vector_fullstar)
            binding.ratingLinearLayout.addView(imageView)
        }
        if (DS.getDSValue(word.difficultyScore).value - fullStarNum.toDouble() == 0.5) {
            val imageView = ImageView(context)
            imageView.setImageResource(R.drawable.vector_halfstar)
            binding.ratingLinearLayout.addView(imageView)
        }

        // WordMeanings
        binding.verbDetail.text = word.verb
        binding.nounDetail.text = word.noun
        binding.adjectiveDetail.text = word.adjective
        binding.adverbDetail.text = word.adverb
        binding.prefixDetail.text = word.prefix
        binding.suffixDetail.text = word.suffix
        binding.expDetail.text = word.expression
        binding.otherDetail.text = word.others
    }

    /**
     * This method set listeners relating to visibility change.
     */
    private fun setListeners(word: WordEntity) {
        // Back Button Listener
        binding.titleLayout.setOnClickListener {
            mainActivityViewModel.backToList()
        }

        // Set openDetail Listener
        binding.openDetail.setOnClickListener {
            when (binding.openDetail.text) {
                context?.getString(R.string.wda_opendetail) -> {
                    binding.openDetail.text = context?.getString(R.string.wda_closedetail)

                    if (binding.verbDetail.text != "") {
                        binding.verbLabel.isVisible = true
                        binding.verbDetail.isVisible = true
                    } else {
                        binding.verbLabel.isVisible = false
                        binding.verbDetail.isVisible = false
                    }

                    if (binding.nounDetail.text != "") {
                        binding.nounLabel.isVisible = true
                        binding.nounDetail.isVisible = true
                    } else {
                        binding.nounLabel.isVisible = false
                        binding.nounDetail.isVisible = false
                    }

                    if (binding.adjectiveDetail.text != "") {
                        binding.adjectiveLabel.isVisible = true
                        binding.adjectiveDetail.isVisible = true
                    } else {
                        binding.adjectiveLabel.isVisible = false
                        binding.adjectiveDetail.isVisible = false
                    }

                    if (binding.adverbDetail.text != "") {
                        binding.adverbLabel.isVisible = true
                        binding.adverbDetail.isVisible = true
                    } else {
                        binding.adverbLabel.isVisible = false
                        binding.adverbDetail.isVisible = false
                    }

                    if (binding.prefixDetail.text != "") {
                        binding.prefixLabel.isVisible = true
                        binding.prefixDetail.isVisible = true
                    } else {
                        binding.prefixLabel.isVisible = false
                        binding.prefixDetail.isVisible = false
                    }

                    if (binding.suffixDetail.text != "") {
                        binding.suffixLabel.isVisible = true
                        binding.suffixDetail.isVisible = true
                    } else {
                        binding.suffixLabel.isVisible = false
                        binding.suffixDetail.isVisible = false
                    }

                    if (binding.expDetail.text != "") {
                        binding.expLabel.isVisible = true
                        binding.expDetail.isVisible = true
                    } else {
                        binding.expLabel.isVisible = false
                        binding.expDetail.isVisible = false
                    }

                    if (binding.otherDetail.text != "") {
                        binding.otherLabel.isVisible = true
                        binding.otherDetail.isVisible = true
                    } else {
                        binding.otherLabel.isVisible = false
                        binding.otherDetail.isVisible = false
                    }
                }
                context?.getString(R.string.wda_closedetail) -> {
                    binding.openDetail.text = context?.getString(R.string.wda_opendetail)
                    binding.verbLabel.isVisible = false
                    binding.verbDetail.isVisible = false
                    binding.nounLabel.isVisible = false
                    binding.nounDetail.isVisible = false
                    binding.adjectiveLabel.isVisible = false
                    binding.adjectiveDetail.isVisible = false
                    binding.adverbLabel.isVisible = false
                    binding.adverbDetail.isVisible = false
                    binding.prefixLabel.isVisible = false
                    binding.prefixDetail.isVisible = false
                    binding.suffixLabel.isVisible = false
                    binding.suffixDetail.isVisible = false
                    binding.expLabel.isVisible = false
                    binding.expDetail.isVisible = false
                    binding.otherLabel.isVisible = false
                    binding.otherDetail.isVisible = false
                }
            }
        }

        //  Set RadioButton (Reset & Restart) Listeners
        binding.radioGroup.setOnCheckedChangeListener {_,_ -> }
        binding.notRemembered.setOnCheckedChangeListener {_,_ -> }
        binding.remembered.setOnCheckedChangeListener{_,_ -> }
        binding.radioGroup.clearCheck()
        binding.notRemembered.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                wordDetailViewModel.updateWhenNotRememberedChecked()
            } else {
                wordDetailViewModel.updateWhenNotRememberedUnChecked()
            }
        }
        binding.remembered.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                wordDetailViewModel.updateWhenRememberedChecked()
            } else {
                wordDetailViewModel.updateWhenRememberedUnChecked()
            }
        }

        // Set SoundSettings Listener
        binding.micView.setOnClickListener {
            textToSpeech.textToSpeechOnDemand(word)
        }

        // Set reference button Listener
        binding.changeReference.setOnClickListener {
            if(mainActivityViewModel.ifReferenceEntityEmpty()) {
                Toast.makeText(activity,R.string.wda_no_reference, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val title = context?.getString(R.string.wda_select_reference_title)
            val message = context?.getString(R.string.wda_select_reference_message)
            val refArray = mainActivityViewModel.getAllReferencesAsArray()
            val refAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, refArray)
            val spinner = Spinner(context)
            spinner.adapter = refAdapter
            val dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle(title)
                .setMessage(message)
                .setView(spinner)
                .setPositiveButton(R.string.wda_reference_ok)
                { _,_ ->
                    wordDetailViewModel.setReference(spinner.selectedItem.toString())
                }
                .setNegativeButton(R.string.wda_reference_cancel,null)
                .show()
        }
        // Set Twitter Button Listener
        binding.twitter.setOnClickListener {
            mainActivityViewModel.openTwitterDialog()
        }

    }

    private fun changeTextSizeDueToTextLength(textView: TextView, text: String) {
        when (text.length) {
            in 1..17 -> {
                textView.textSize = 34f
                textView.isSingleLine = true
                textView.text = text
            }
            in 18..24 -> {
                textView.textSize = 30f
                textView.isSingleLine = false
                textView.text = text
            }
            in 25..35 -> {
                textView.textSize = 20f
                textView.isSingleLine = false
                textView.text = text
            }
            in 36..72 -> {
                textView.textSize = 18f
                textView.isSingleLine = false
                textView.text = text
            }
        }
    }

}