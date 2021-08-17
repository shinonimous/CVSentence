/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cvrabbit.cvsentence.util.constant.DS
import com.cvrabbit.cvsentence.util.constant.RRT
import java.util.*

@Entity(tableName = "word_table")
data class WordEntity(
    var word: String = "",
    var mainMeaning: String = "",
    var verb: String = "", // Verb
    var noun: String = "", // Noun
    var adjective: String = "", // Adjective
    var adverb: String = "", // Adverb
    var expression: String = "", // Expression
    var prefix: String = "", // Prefix
    var suffix: String = "", // Suffix
    var others: String = "", // Others
    var lookupCount: Int = 0,
    var lastLookupDate: Long = Date().time,
    var durationFromLastLookupTime: Long = 0L,
    var recommendedRecurTiming: String = RRT.FIRST.value,
    var notRememberedCountSinceUnderLimitReached: Int = 0,
    var difficultyScore: Float = DS.FORTH.value,
    var notRememberedCount: Int = 0,
    var rememberedCount: Int = 0,
    var tryAddSameWordCount: Int = 0,
    var registeredDate: Long = Date().time,
    var green: Boolean = false,
    var reference: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}

fun initializeWordEntity(wordEntity: WordEntity) {
    wordEntity.word = ""
    wordEntity.mainMeaning = ""
    wordEntity.verb = "" // Verb
    wordEntity.noun = "" // Noun
    wordEntity.adjective = "" // Adjective
    wordEntity.adverb = "" // Adverb
    wordEntity.expression = "" // Expression
    wordEntity.prefix = "" // Prefix
    wordEntity.suffix = "" // Suffix
    wordEntity.others = "" // Others
    wordEntity.lookupCount = 0
    wordEntity.lastLookupDate = Date().time
    wordEntity.durationFromLastLookupTime = 0L
    wordEntity.recommendedRecurTiming = RRT.FIRST.value
    wordEntity.notRememberedCountSinceUnderLimitReached = 0
    wordEntity.difficultyScore = DS.FORTH.value
    wordEntity.notRememberedCount = 0
    wordEntity.rememberedCount = 0
    wordEntity.tryAddSameWordCount = 0
    wordEntity.registeredDate = Date().time
    wordEntity.green = false
    wordEntity.reference = ""
}




