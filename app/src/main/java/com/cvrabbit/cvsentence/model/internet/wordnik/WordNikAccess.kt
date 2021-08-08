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
import com.cvrabbit.cvsentence.R
import com.cvrabbit.cvsentence.model.db.WordEntity
import com.cvrabbit.cvsentence.model.internet.WordSearch
import com.cvrabbit.cvsentence.model.internet.lang.OriginalWordGenerator
import com.cvrabbit.cvsentence.service.OverlayService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.net.URL
import java.util.*

class WordNikSearch(context: Context): WordSearch {

    private val appContext = context.applicationContext
    private val accessURL = "https://api.wordnik.com/v4/word.json/"
    private val parameters = "/definitions?limit=200&includeRelated=false&sourceDictionaries=wiktionary&useCanonical=true&includeTags=false&api_key="
    private val apiKey = "3jen77gz0mls6ksarkco8rzd9u1rhrxb8pu9f7k5309wrr28n"

    override fun searchWord(requestWord: String): WordEntity? {
        val wordEntity = WordEntity()
        val wir = getAPIResponse(requestWord)
        if (wir.responseExist) {
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
            return wordEntity
        } else {
            return null
        }
    }

    private fun getAPIResponse(requestStr: String): WordNikInterpretedResponse {
        val lowerRequestStr = requestStr.toLowerCase(Locale.ENGLISH)
        var eir = WordNikInterpretedResponse()
        if (!checkIfRequestValid(lowerRequestStr)) {
            return eir
        }
        eir = getProperWirObject(lowerRequestStr)
        return eir
    }

    private fun requestToWordNik(requestStr: String): String {
        val url = URL(accessURL + requestStr + parameters + apiKey)
        Log.d("MyURL", accessURL + requestStr + parameters + apiKey)
        var response = ""
        runBlocking {
            val job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    response = url.readText()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            job.join()
        }
        Log.d("MyResponse",response)
        return response
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
            return false
        }
        if (!checkIfEnglish(requestStr)) {
            Log.d("checkIfEnglish", "Not English")
            return false
        }

        return returnBool
    }

    // When using WordNet, max 71 length
    private fun checkLengthOfWord(requestStr: String): Boolean {
        return requestStr.length in 1..30
    }

    private fun checkIfEnglish(requestStr: String): Boolean {
        //return requestStr.matches(Regex("""^[a-zA-Z0-9'*./?_-]*${'$'}"""))
        return requestStr.matches(Regex("""^[a-zA-Z0-9'\u0020\u00a0-]*${'$'}""")) //When allow space
        //return requestStr.matches(Regex("""^[a-zA-Z0-9'-]*${'$'}""")) //When don't allow space
    }

}