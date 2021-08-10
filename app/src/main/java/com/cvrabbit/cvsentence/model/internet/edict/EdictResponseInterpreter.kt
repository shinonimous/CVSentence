/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.model.internet.edict

import android.util.Log
import java.util.regex.Pattern

private val preTag = "<pre>\n(.+?)\n</pre>"
private val preTagPattern = Pattern.compile(preTag, Pattern.DOTALL)
private val kanaTag = "\\[(.+?)]"
private val kanaTagPattern = Pattern.compile(kanaTag, Pattern.DOTALL)
private val bracket = "\\((.+?)\\)"
private val bracketPattern = Pattern.compile(bracket, Pattern.DOTALL)

object EdictResponseInterpreter {
    fun interpretResponse(responseStr: String): EdictInterpretedResponse {
        val eir = EdictInterpretedResponse()
        eir.fullResponse = responseStr
        eir.setProperties()
        return eir
    }
}

class EdictInterpretedResponse {

    private val TAG = "EdictInterpretedResponse"

    var requestedWord = ""
    var fullResponse = ""
    var responseExist = false
    lateinit var chunks: List<EdictChunk>
    var mainMeaning = ""
    var mainMeaningKanji = ""
    var verb = ""
    var noun = ""
    var adverb = ""
    var adjective = ""
    var prefix = ""
    var suffix = ""
    var expression = ""
    var others = ""
    var allMeanings = ""
    var allMeaningsKanji = ""

    fun setProperties() {
        if (!fullResponse.contains(preTagPattern.toRegex())) {
            Log.d(TAG,"Doesn't contain pre tag")
            responseExist = false
        } else {
            responseExist = true
            extractChunks(extractInsidePreTag()!!)
            val sortedChunk = chunks.sortedWith(compareBy { it.pos })
            for (i in sortedChunk) {
                for (j in i.commonMeanings) { mainMeaning += "$j;" }
                for (j in i.commonMeaningsKanji) { mainMeaningKanji += "$j;" }
                for (j in i.meaningsKanji) { allMeaningsKanji += "$j;" }
                when(i.pos) {
                    BracketMarker.VERB -> {
                        for (j in i.meanings) { verb += "$j;" ; allMeanings += "$j;" }
                    }
                    BracketMarker.NOUN -> {
                        for (j in i.meanings) { noun += "$j;" ; allMeanings += "$j;" }
                    }
                    BracketMarker.ADJECTIVE -> {
                        for (j in i.meanings) { adjective += "$j;" ; allMeanings += "$j;" }
                    }
                    BracketMarker.ADVERB -> {
                        for (j in i.meanings) { adverb += "$j;" ; allMeanings += "$j;" }
                    }
                    BracketMarker.PREFIX -> {
                        for (j in i.meanings) { prefix += "$j;" ; allMeanings += "$j;" }
                    }
                    BracketMarker.SUFFIX -> {
                        for (j in i.meanings) { suffix += "$j;" ; allMeanings += "$j;" }
                    }
                    BracketMarker.EXPRESSION -> {
                        for (j in i.meanings) { expression += "$j;" ; allMeanings += "$j;" }
                    }
                    BracketMarker.ELSE -> {
                        for (j in i.meanings) { others += "$j;" ; allMeanings += "$j;" }
                    }
                }
            }
            mainMeaning = mainMeaning.dropLast(1)
            mainMeaningKanji = mainMeaningKanji.dropLast(1)
            allMeanings = allMeanings.dropLast(1)
            allMeaningsKanji = allMeaningsKanji.dropLast(1)
            verb = verb.dropLast(1)
            noun = noun.dropLast(1)
            adjective = adjective.dropLast(1)
            adverb = adverb.dropLast(1)
            prefix = prefix.dropLast(1)
            suffix = suffix.dropLast(1)
            expression = expression.dropLast(1)
            others = others.dropLast(1)

            if (mainMeaning == "") {mainMeaning = allMeanings}

            if (mainMeaning.filter{it == ';'}.count() > 15) {
                if( mainMeaningKanji != "") { mainMeaning = mainMeaningKanji }
                else { mainMeaning = allMeaningsKanji }
            }

            Log.d(TAG, "mainMeaning: $mainMeaning")
            Log.d(TAG, "verb: $verb")
            Log.d(TAG,"noun: $noun")
            Log.d(TAG, "adjective: $adjective")
            Log.d(TAG, "adverb: $adverb")
            Log.d(TAG, "prefix: $prefix")
            Log.d(TAG, "suffix: $suffix")
            Log.d(TAG, "expression: $expression")
            Log.d(TAG, "others: $others")
        }
    }

    /**API Response's main part is included in <pre></pre> tag
     * https://stackoverflow.com/questions/6560672/java-regex-to-extract-text-between-tags
     * */
    private fun extractInsidePreTag(): String? {
        val matcher = preTagPattern.matcher(fullResponse)
        matcher.find()
        return matcher.group(1)
    }

    /**
     * insidePreTagStr is consisted of multiple "Meaning;Meaning;…/(POS)\n" format.
     * For example, "kiss" returns following result.
     * 　キス(P);キッス /(n,vs) kiss/(P)/
     * 　吸う(P);喫う /(v5u,vt) (1) to smoke/to breathe in/to inhale/(v5u,vt) (2) …
     * This method returns list of chunks delimited by "\n" (Separate by each line)
     */
    private fun extractChunks(insidePreTagStr: String) {
        val chunkList = mutableListOf<EdictChunk>()
        val targetStrList = insidePreTagStr.split(Regex("\n"), 0)
        for (i in targetStrList) {
            val ec = EdictChunk()
            ec.fullChunk = i
            ec.setProperties()
            chunkList.add(ec)
        }
        chunks = chunkList
    }
}

class EdictChunk {

    private val TAG = "EdictChunk"

    var fullChunk = ""
    lateinit var pos: BracketMarker
    var commonMeanings = listOf<String>()
    var commonMeaningsKanji = listOf<String>()
    var kanjiMeanings = listOf<String>() // This is close to row data
    var kanaMeanings = listOf<String>() // This is close to row data
    var meanings = listOf<String>() // Fully processed data
    var meaningsKanji = listOf<String>() // Fully processed data

    fun setProperties() {
        setPos()
        setKanjiKanaMeanings()
        setCommonAndEveryMeanings()
    }

    /**
     * Example of chunk is
     *  翔る(P);駆ける;翔ける(io) [かける] /(v5r,vi) (1) (usu. 翔る……
     * In this example, pos (Part of Speech) is verb (v5r).
     */
    private fun setPos() {
        val startWithPosStr = (fullChunk.substring(fullChunk.indexOf("/"), fullChunk.length)).drop(2)
        Log.d(TAG, startWithPosStr)
        pos = when {
            startWithPosStr.startsWith(BracketMarker.NOUN.bracket, 0) -> {
                BracketMarker.NOUN
            }
            startWithPosStr.startsWith(BracketMarker.VERB.bracket, 0) -> {
                BracketMarker.VERB
            }
            startWithPosStr.startsWith(BracketMarker.ADJECTIVE.bracket,0) -> {
                BracketMarker.ADJECTIVE
            }
            startWithPosStr.startsWith(BracketMarker.ADVERB.bracket,0) -> {
                BracketMarker.ADVERB
            }
            startWithPosStr.startsWith(BracketMarker.PREFIX.bracket,0) -> {
                BracketMarker.PREFIX
            }
            startWithPosStr.startsWith(BracketMarker.SUFFIX.bracket,0) -> {
                BracketMarker.SUFFIX
            }
            startWithPosStr.startsWith(BracketMarker.EXPRESSION.bracket,0) -> {
                BracketMarker.EXPRESSION
            }
            else -> {
                BracketMarker.ELSE
            }
        }
    }
    private fun setKanjiKanaMeanings() {
        val wholeMeanings = fullChunk.substring(0, fullChunk.indexOf("/"))
        if (wholeMeanings.contains(kanaTagPattern.toRegex())) {
            val matcher = kanaTagPattern.matcher(wholeMeanings)
            matcher.find()
            val kanaStrings = matcher.group(1)
            kanaMeanings =  kanaStrings!!.split(Regex(";"), 0)
            kanjiMeanings =
                wholeMeanings
                    .replace("[$kanaStrings]", "", ignoreCase = false)
                    .split(Regex(";"),0)
        } else {
            kanaMeanings = wholeMeanings.split(Regex(";"),0)
        }
    }
    private fun setCommonAndEveryMeanings() {
        val commonMeaningMutable = mutableListOf<String>()
        val commonMeaningKanjiMutable = mutableListOf<String>()
        val meaningMutable = mutableListOf<String>()
        val meaningKanjiMutable = mutableListOf<String>()
        if(kanjiMeanings.isNotEmpty()) {
            for (i in kanjiMeanings) {
                val addWord = i.replace(bracketPattern.toRegex(), "")
                    .replace(" ", "")
                if (i.contains("(P)")) {
                    commonMeaningMutable.add(addWord)
                    commonMeaningKanjiMutable.add(addWord)
                    meaningMutable.add(addWord)
                } else {
                    meaningMutable.add(addWord)
                    meaningKanjiMutable.add(addWord)
                }
            }
        }
        if(kanaMeanings.isNotEmpty()) {
            for (i in kanaMeanings) {
                val addWord = i.replace(bracketPattern.toRegex(), "")
                    .replace(" ","")
                if (i.contains("(P)")) {
                    commonMeaningMutable.add(addWord)
                    meaningMutable.add(addWord)
                } else {
                    meaningMutable.add(addWord)
                }
            }
        }
        commonMeanings = commonMeaningMutable
        commonMeaningsKanji = commonMeaningKanjiMutable
        meanings = meaningMutable
        meaningsKanji = meaningKanjiMutable
    }
}

enum class BracketMarker() {
    NOUN{override val bracket = "n"},
    VERB{override val bracket = "v"},
    ADVERB{override val bracket = "adv"},
    ADJECTIVE{override val bracket = "adj"},
    PREFIX{override val bracket = "pref"},
    SUFFIX{override val bracket = "suf"},
    EXPRESSION{override val bracket = "exp"},
    ELSE{override val bracket = ""};

    abstract val bracket: String
}