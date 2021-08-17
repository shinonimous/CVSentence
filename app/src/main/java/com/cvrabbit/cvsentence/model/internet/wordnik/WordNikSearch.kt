/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.model.internet.wordnik

import android.content.Context
import android.util.Log
import com.cvrabbit.cvsentence.model.db.WordEntity
import com.cvrabbit.cvsentence.model.internet.WordSearch
import com.cvrabbit.cvsentence.model.internet.lang.OriginalWordGenerator
import com.cvrabbit.cvsentence.util.constant.Constants.ENGLISH_CHECK_REGEX_SPACE_ALLOWED
import com.cvrabbit.cvsentence.util.constant.Constants.WORDNIK_ACCESS_URL
import com.cvrabbit.cvsentence.util.constant.Constants.WORDNIK_API_KEY
import com.cvrabbit.cvsentence.util.constant.Constants.WORDNIK_FIXED_PARAMETERS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.net.URL
import java.util.*

private const val TAG = "WordNikSearch"

class WordNikSearch(context: Context): WordSearch {

    private val appContext = context.applicationContext

    override fun searchWord(requestWord: String): WordEntity? {
        val wordEntity = WordEntity()
        val wir = getAPIResponse(requestWord)
        return if (wir.responseExist) {
            wordEntity.word = wir.requestedWord
            wordEntity.mainMeaning = wir.mainMeaning
            wordEntity.verb = wir.verb
            wordEntity.noun = wir.noun
            wordEntity.adjective = wir.adjective
            wordEntity.adverb = wir.adverb
            wordEntity.expression = wir.expression
            wordEntity.prefix = wir.prefix
            wordEntity.suffix = wir.suffix
            wordEntity.others = wir.others
            wordEntity
        } else {
            null
        }
    }

    private fun getAPIResponse(requestStr: String): WordNikInterpretedResponse {
        val lowerRequestStr = requestStr.toLowerCase(Locale.ENGLISH)
        var eir = WordNikInterpretedResponse()
        if (!checkIfRequestValid(lowerRequestStr)) {
            return eir
        }
        runBlocking {
            val job = CoroutineScope(Dispatchers.IO).launch {
                eir = getProperWirObject(lowerRequestStr)
            }
            job.join()
        }
        return eir
    }

    private fun getProperWirObject(requestStr: String): WordNikInterpretedResponse {
        val response = requestToWordNik(requestStr)
        var wir = WordNikInterpretedResponse()
        val wir1 = WordNikResponseInterpreter.interpretResponse(response)
        wir1.requestedWord = requestStr
        if(wir1.responseExist) {wir = wir1}

        else { // When API response doesn't exist -> doubt verb conjugation
            val wir2 = getOriginalVerbWirObject(requestStr)
            if(wir2.responseExist) {wir = wir2}

            else { // When API response (For original verb) doesn't exist -> doubt plural noun
                val wir3 = getOriginalNounWirObject(requestStr)
                if(wir3.responseExist) {wir = wir3}
            }
        }
        return wir
    }

    private fun requestToWordNik(requestStr: String): String {
        val url = URL(WORDNIK_ACCESS_URL + requestStr + WORDNIK_FIXED_PARAMETERS + WORDNIK_API_KEY)
        var response = ""
        try {
            response = url.readText()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d(TAG, "row response:${response}")
        return response
    }

    private fun getOriginalVerbWirObject(requestStr: String): WordNikInterpretedResponse {
        var wir = WordNikInterpretedResponse()
        val mayBeOriginalVerb = OriginalWordGenerator(appContext).getPresentStr(requestStr)
        if (requestStr != mayBeOriginalVerb) {
            val response = requestToWordNik(mayBeOriginalVerb)
            val originalVerbWir = WordNikResponseInterpreter.interpretResponse(response)
            originalVerbWir.requestedWord = mayBeOriginalVerb
            if (originalVerbWir.verb != "") { wir = originalVerbWir }
            else { originalVerbWir.responseExist = false; wir = originalVerbWir }
        }
        return wir
    }

    private fun getOriginalNounWirObject(requestStr: String): WordNikInterpretedResponse {
        var wir = WordNikInterpretedResponse()
        val mayBeOriginalNoun = OriginalWordGenerator(appContext).getSingleStr(requestStr)
        if (requestStr != mayBeOriginalNoun) {
            val response = requestToWordNik(mayBeOriginalNoun)
            val originalNounWir = WordNikResponseInterpreter.interpretResponse(response)
            originalNounWir.requestedWord = mayBeOriginalNoun
            if (originalNounWir.noun != "") { wir = originalNounWir }
            else { originalNounWir.responseExist = false; wir = originalNounWir }
        }
        return wir
    }

    private fun checkIfRequestValid(requestStr: String): Boolean {
        val returnBool = true

        if (!checkLengthOfWord(requestStr)) {
            Log.d(TAG, "checkIfRequestValid is Running: Too Long")
            return false
        }
        if (!checkIfEnglish(requestStr)) {
            Log.d(TAG, "checkIfRequestValid is Running: Not English")
            return false
        }

        return returnBool
    }

    // When using WordNet, max 71 length
    private fun checkLengthOfWord(requestStr: String): Boolean {
        return requestStr.length in 1..30
    }

    private fun checkIfEnglish(requestStr: String): Boolean {
        return requestStr.matches(Regex(ENGLISH_CHECK_REGEX_SPACE_ALLOWED))
    }

}