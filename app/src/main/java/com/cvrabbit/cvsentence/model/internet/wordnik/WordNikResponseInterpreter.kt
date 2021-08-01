/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.model.internet.wordnik

import java.lang.Exception

object WordNikResponseInterpreter {
    fun interpretResponse(responseStr: String):WordNikInterpretedResponse {
        val wir = WordNikInterpretedResponse()
        wir.fullResponse = responseStr
        wir.setProperties()
        return wir
    }
}

class WordNikInterpretedResponse {
    var requestedWord = ""
    var fullResponse = ""
    var responseExist = false
    lateinit var chunks: List<WordNikDef>
    var mainMeaning = ""
    var verb = ""
    var noun = ""
    var adverb = ""
    var adjective = ""
    var prefix = ""
    var suffix = ""
    var expression = ""
    var others = ""

    fun setProperties() {
        try {
            chunks = getWordNikDef(fullResponse)
            responseExist = true
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        var verbCount = 0
        var nounCount = 0
        var adjectiveCount = 0
        var adverbCount = 0
        var prefixCount = 0
        var suffixCount = 0
        var expCount = 0
        var othersCount = 0
        for (i in chunks) {
            when(i.partOfSpeech) {
                //Verb
                "verb",
                "verb-intransitive",
                "verb-transitive",
                "auxiliary-verb",
                "past-paticle" -> {
                    verb += "●${removeTags(i.text)}\n"
                    verbCount += 1
                    if(verbCount <= 2) {
                        mainMeaning += "●${removeTags(i.text)}\n"
                    }
                }

                //Noun
                "noun",
                "noun-plural",
                "noun-posessive",
                "proper-noun",
                "proper-noun-plural",
                "proper-noun-posessive",
                "family-name",
                "given-name" -> {
                    noun += "●${removeTags(i.text)}\n"
                    nounCount += 1
                    if(nounCount <= 2) {
                        mainMeaning += "●${removeTags(i.text)}\n"
                    }
                }

                //Adverb
                "adverb" -> {
                    adverb += "●${removeTags(i.text)}\n"
                    adverbCount += 1
                    if(adverbCount <= 2) {
                        mainMeaning += "●${removeTags(i.text)}\n"
                    }
                }

                //Adjective
                "adjective" -> {
                    adjective += "●${removeTags(i.text)}\n"
                    adjectiveCount += 1
                    if(adjectiveCount <= 2) {
                        mainMeaning += "●${removeTags(i.text)}\n"
                    }
                }

                //Prefix
                "affix",
                "phrasal-prefix" -> {
                    prefix += "●${removeTags(i.text)}\n"
                    prefixCount += 1
                    if(prefixCount <= 2) {
                        mainMeaning += "●${removeTags(i.text)}\n"
                    }
                }

                //Suffix
                "suffix" -> {
                    suffix += "●${removeTags(i.text)}\n"
                    suffixCount += 1
                    if(suffixCount <= 2) {
                        mainMeaning += "●${removeTags(i.text)}\n"
                    }
                }

                //Expression
                "idiom" -> {
                    expression += "●${removeTags(i.text)}\n"
                    expCount += 1
                    if (expCount <= 2) {
                        mainMeaning += "●${removeTags(i.text)}\n"
                    }
                }

                //Others
                "article",
                "definite-article",
                "interjection",
                "preposition",
                "abbreviation",
                "conjunction",
                "imperative" -> {
                    others += "●${removeTags(i.text)}\n"
                    othersCount += 1
                    if(othersCount <= 2) {
                        mainMeaning += "●${removeTags(i.text)}\n"
                    }
                }

                else -> {others += "●${removeTags(i.text)}\n"
                    othersCount += 1
                    if(othersCount <= 2) {
                        mainMeaning += "●${removeTags(i.text)}\n"
                    }
                }
            }
        }
    }

    private fun removeTags(str: String):String{
        return str.replace(Regex("""<.+?>"""), "")
    }
}