/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.util.lang

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.cvrabbit.cvsentence.model.db.Word
import com.cvrabbit.cvsentence.model.repository.MainRepository
import java.util.*
import javax.inject.Inject

private const val TAG = "TextToSpeech"

class GoogleTextToSpeech @Inject constructor(
    app: Context,
    private val mainRepository: MainRepository
    ): TextToSpeech.OnInitListener {

    private val tts = TextToSpeech(app, this)

    fun shutDown() {
        tts.shutdown()
    }

    fun textToSpeechOnSelection(word: Word) {
        if (mainRepository.getOnSelectWordSoundSetting()) {
            setLocale(Locale.US)
            textToSpeech(word.word, "on_select_word")
            setTtsListener(word.mainMeaning)
        } else if(mainRepository.getOnSelectMeaningSoundSetting()) {
            setLocale(Locale.JAPAN)
            textToSpeech(word.mainMeaning, "on_select_meaning")
        }
    }
    fun textToSpeechOnDemand(word:Word) {
        if (mainRepository.getOnDemandWordSoundSetting()) {
            setLocale(Locale.US)
            textToSpeech(word.word, "on_demand_word")
            setTtsListener(word.mainMeaning)
        } else if(mainRepository.getOnDemandMeaningSoundSetting()) {
            setLocale(Locale.JAPAN)
            textToSpeech(word.mainMeaning, "on_demand_meaning")
        }
    }

    override fun onInit(status: Int) {
        if (TextToSpeech.SUCCESS == status) {
            Log.d(TAG,"success to initialize$status")
        } else {
            Log.e(TAG, "failed to initialize$status")
        }
    }

    private fun setLocale(locale: Locale) {
        tts.let { tts ->
            if (tts.isLanguageAvailable(locale) > TextToSpeech.LANG_AVAILABLE) {
                tts.language = locale
            } else {
                tts.language = Locale.ENGLISH
            }
        }
    }

    private fun textToSpeech(text: String, utteranceId: String) {
        if (text.isNotEmpty()) {
            tts.let { tts ->
                tts.setSpeechRate(1.0.toFloat())
                tts.setPitch(1.0.toFloat())
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            }
        }
    }
    private fun setTtsListener(meaning: String) {
        val listenerResult = tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onDone(utteranceId: String) {
                if (mainRepository.getOnSelectMeaningSoundSetting() && utteranceId == "on_select_word") {
                    setLocale(Locale.JAPAN)
                    textToSpeech(meaning, "on_select_meaning")
                }
                if (mainRepository.getOnDemandMeaningSoundSetting() && utteranceId == "on_demand_word") {
                    setLocale(Locale.JAPAN)
                    textToSpeech(meaning, "on_demand_meaning")
                }
            }

            override fun onError(utteranceId: String) {
                Log.d(TAG, "progress on Error $utteranceId")
            }

            override fun onStart(utteranceId: String) {
                Log.d(TAG, "progress on Start $utteranceId")
            }
        })
        if (listenerResult != TextToSpeech.SUCCESS) {
            Log.e(TAG, "failed to add utterance progress listener")
        }
    }
}