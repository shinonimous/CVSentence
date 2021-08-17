/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.model.internet.lang

import android.content.Context
import org.json.JSONArray
import simplenlg.features.Feature
import simplenlg.features.Tense
import simplenlg.framework.NLGFactory
import simplenlg.lexicon.Lexicon
import simplenlg.realiser.english.Realiser
import java.io.BufferedReader
import java.io.InputStreamReader

private const val TAG = "OriginalWordGenerator"

class OriginalWordGenerator(context: Context) {

    // Get Base form of Verb
    fun getPresentStr(unknownWord: String): String {
        return if (unknownWord.contains(" ")) {
            val unknownWordArray = unknownWord.split(" ")
            val changedWord = getBaseVerb(unknownWordArray[0])
            var outputTail = ""
            for(i in 1 until unknownWordArray.size) {
                outputTail += " " + unknownWordArray[i]
            }
            changedWord + outputTail
        } else if (unknownWord.contains("-")) {
            val unknownWordArray = unknownWord.split("-")
            val changedWord = getBaseVerb(unknownWordArray[0])
            var outputTail = ""
            for(i in 1 until unknownWordArray.size) {
                outputTail += "-" + unknownWordArray[i]
            }
            changedWord + outputTail
        } else {
            getBaseVerb(unknownWord)
        }
    }

    // Get Single format of noun
    fun getSingleStr(unknownWord: String): String {
        return Singularize.singularize(unknownWord)
    }

    // When using verb.json file (in asset folder) to get base verb, use this.
    //private fun getBaseVerb(unknownWord: String) = getBaseVerbUsingDictionary(unknownWord)

    // When using SimpleNLG to get base verb, use this.
    private fun getBaseVerb(unknownWord: String) = getBaseVerbUsingSimpleNLG(unknownWord)

    private fun getBaseVerbUsingDictionary(unknownWord: String): String {
        val verbs = getVerbDictionary()
        for (i in verbs) {
            println(i[0] + ";" + i[1] + ";")
            if (unknownWord == i[1] || unknownWord == i[2] || unknownWord == i[3] || unknownWord == i[4]) {
                return i[0]
            }
        }
        return unknownWord
    }

    private fun getBaseVerbUsingSimpleNLG(unknownWord: String): String {
        val lexicon = Lexicon.getDefaultLexicon()
        val nlgFactory = NLGFactory(lexicon)
        val realiser = Realiser(lexicon)
        val p = nlgFactory.createClause()
        p.setSubject("I")
        p.setVerb(unknownWord)
        p.setFeature(Feature.TENSE, Tense.PRESENT)
        var output = realiser.realiseSentence(p).substring(2)
        output = output.substring(0, output.length - 1)
        return output
    }

    private val appContext = context.applicationContext
    private var verbDictionary: Array<Array<String>> = arrayOf()

    private fun getVerbDictionary(): Array<Array<String>> {
        if (verbDictionary.isEmpty()) {
            val assetManager = appContext.resources.assets
            val inputStream = assetManager.open("verb.json")
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val str = bufferedReader.readText()
            val jsonArray = JSONArray(str)
            for(i in 0 until jsonArray.length()){
                val conjugation = JSONArray(jsonArray.get(i).toString())
                var mylist: Array<String> = arrayOf()
                for (j in 0 until conjugation.length()){
                    val addStr = conjugation.get(j).toString()
                    mylist += addStr
                }
                verbDictionary += mylist
            }
        }
        return verbDictionary
    }
}