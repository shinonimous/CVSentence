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
    var lastLookupDate: Long = 0L,
    var durationFromLastLookupTime: Long = 0L,
    var recommendedRecurTiming: String = RRT.FIRST.value,
    var notRememberedCountSinceUnderLimitReached: Int = 0,
    var difficultyScore: Float = DS.FORTH.value,
    var notRememberedCount: Int = 0,
    var rememberedCount: Int = 0,
    var tryAddSameWordCount: Int = 0,
    var registeredDate: Long = 0L,
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
    wordEntity.lastLookupDate = 0L
    wordEntity.durationFromLastLookupTime = 0L
    wordEntity.recommendedRecurTiming = RRT.FIRST.value
    wordEntity.notRememberedCountSinceUnderLimitReached = 0
    wordEntity.difficultyScore = DS.FORTH.value
    wordEntity.notRememberedCount = 0
    wordEntity.rememberedCount = 0
    wordEntity.tryAddSameWordCount = 0
    wordEntity.registeredDate = 0L
    wordEntity.green = false
    wordEntity.reference = ""
}

fun overwriteWordEntity(writeEntity: WordEntity, writtenEntity: WordEntity) {
    writtenEntity.word = writeEntity.word
    writtenEntity.mainMeaning = writeEntity.mainMeaning
    writtenEntity.verb = writeEntity.verb
    writtenEntity.noun = writeEntity.noun
    writtenEntity.adjective = writeEntity.adjective
    writtenEntity.adverb = writeEntity.adverb
    writtenEntity.expression = writeEntity.expression
    writtenEntity.prefix = writeEntity.prefix
    writtenEntity.suffix = writeEntity.suffix
    writtenEntity.others = writeEntity.others
    writtenEntity.lookupCount = writeEntity.lookupCount
    writtenEntity.lastLookupDate = writeEntity.lastLookupDate
    writtenEntity.durationFromLastLookupTime = writeEntity.durationFromLastLookupTime
    writtenEntity.recommendedRecurTiming = writeEntity.recommendedRecurTiming
    writtenEntity.notRememberedCountSinceUnderLimitReached = writeEntity.notRememberedCountSinceUnderLimitReached
    writtenEntity.difficultyScore = writeEntity.difficultyScore
    writtenEntity.notRememberedCount = writeEntity.notRememberedCount
    writtenEntity.rememberedCount = writeEntity.rememberedCount
    writtenEntity.tryAddSameWordCount = writeEntity.tryAddSameWordCount
    writtenEntity.registeredDate = writeEntity.registeredDate
    writtenEntity.green = writeEntity.green
    writtenEntity.reference = writeEntity.reference
}