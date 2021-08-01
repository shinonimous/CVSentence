/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.model.db

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Word : RealmObject() {

    @PrimaryKey
    var id: Long = 0
    var word: String = ""
    var mainMeaning: String = ""
    var verb: String = "" // Verb
    var noun: String = "" // Noun
    var adjective: String = "" // Adjective
    var adverb: String = "" // Adverb
    var expression: String = "" // Expression
    var prefix: String = "" // Prefix
    var suffix: String = "" // Suffix
    var others: String = "" // Others
    var lookupCount: Int = 0
    var lastLookupDate: Date = Date()
    var durationFromLastLookupTime: Long = 0L
    var recommendedRecurTiming: String = RRT.FIRST.value
    var notRememberedCountSinceUnderLimitReached: Int = 0
    var difficultyScore: Float = DS.FORTH.value
    var notRememberedCount: Int = 0
    var rememberedCount: Int = 0
    var tryAddSameWordCount: Int = 0
    var registeredDate: Date = lastLookupDate
    var green: Boolean = false
    var reference: String = ""

}

/**
 * RRT is RecommendedRecurTiming
 */
enum class RRT {
    FIRST {
        override val value = "1"
        override val longValue = 86400000L //24hour
    },
    SECOND {
        override val value = "4"
        override val longValue = 345600000L
    },
    THIRD {
        override val value = "7"
        override val longValue = 604800000L
    },
    FORTH {
        override val value = "11"
        override val longValue = 950400000L
    },
    FIFTH {
        override val value = "15"
        override val longValue = 1296000000L
    },
    SIXTH {
        override val value = "20"
        override val longValue = 1728000000L
    },
    SEVENTH {
        override val value = "40"
        override val longValue = 3456000000L
    },
    EIGHTH {
        override val value = "80"
        override val longValue = 6912000000L
    },
    NINTH {
        override val value = "160"
        override val longValue = 13824000000L
    },
    TENTH {
        override val value = "360"
        override val longValue = 31104000000L
    };

    abstract val value: String
    abstract val longValue: Long

    companion object {

        fun getRRTValue(dbValue: String):RRT {
            var returnValue:RRT = FIRST
            when(dbValue) {
                FIRST.value -> returnValue = FIRST
                SECOND.value -> returnValue = SECOND
                THIRD.value -> returnValue = THIRD
                FORTH.value -> returnValue = FORTH
                FIFTH.value -> returnValue = FIFTH
                SIXTH.value -> returnValue = SIXTH
                SEVENTH.value -> returnValue = SEVENTH
                EIGHTH.value -> returnValue = EIGHTH
                NINTH.value -> returnValue = NINTH
                TENTH.value -> returnValue = TENTH
            }
            return returnValue
        }

        fun getProperRRTWhenNotRemembered(
                durationFromLastLookupTime: Long,
                presentRRT: RRT
        ): RRT{

            if (presentRRT.longValue < durationFromLastLookupTime) {
                return presentRRT
            }

            var returnRRT: RRT = presentRRT

            when(durationFromLastLookupTime) {
                in 0L..SECOND.longValue -> { returnRRT = FIRST }
                in SECOND.longValue..THIRD.longValue -> { returnRRT = SECOND }
                in THIRD.longValue..FORTH.longValue -> { returnRRT = THIRD }
                in FORTH.longValue..FIFTH.longValue -> { returnRRT = FORTH }
                in FIFTH.longValue..SIXTH.longValue -> { returnRRT = FIFTH }
                in SIXTH.longValue..SEVENTH.longValue -> { returnRRT = SIXTH }
                in SEVENTH.longValue..EIGHTH.longValue -> { returnRRT = SEVENTH }
                in EIGHTH.longValue..NINTH.longValue -> { returnRRT = EIGHTH }
                in NINTH.longValue..TENTH.longValue -> { returnRRT = NINTH }
                in TENTH.longValue..9223372036854775807L -> { returnRRT = TENTH }
            }
            return returnRRT
        }

        fun getProperRRTWhenRemembered(
                durationFromLastLookupTime: Long,
                presentRRT: RRT
        ): RRT{

            if (presentRRT.longValue > durationFromLastLookupTime) {
                return presentRRT
            }

            var returnRRT: RRT = presentRRT

            when(durationFromLastLookupTime) {
                in 0L..SECOND.longValue -> { returnRRT = SECOND }
                in SECOND.longValue..THIRD.longValue -> { returnRRT = THIRD }
                in THIRD.longValue..FORTH.longValue -> { returnRRT = FORTH }
                in FORTH.longValue..FIFTH.longValue -> { returnRRT = FIFTH }
                in FIFTH.longValue..SIXTH.longValue -> { returnRRT = SIXTH }
                in SIXTH.longValue..SEVENTH.longValue -> { returnRRT = SEVENTH }
                in SEVENTH.longValue..EIGHTH.longValue -> { returnRRT = EIGHTH }
                in EIGHTH.longValue..NINTH.longValue -> { returnRRT = NINTH }
                in NINTH.longValue..9223372036854775807L -> { returnRRT = TENTH }
            }
            return returnRRT
        }
    }
}

/**
 * DS is DifficultyScore
 */
enum class DS {
    FIRST {override val value = 10f},
    SECOND {override val value = 9f},
    THIRD {override val value = 8f},
    FORTH {override val value = 7f},
    FIFTH {override val value = 6f},
    SIXTH {override val value = 5f},
    SEVENTH {override val value = 4f},
    EIGHTH {override val value = 3f},
    NINTH {override val value = 2.5f},
    TENTH {override val value = 2f},
    ELEVENTH {override val value = 1.5f},
    TWELVETH {override val value = 1f},
    THIRTEENTH {override val value = 0f};

    abstract val value: Float

    companion object {
        fun getDSValue(dbValue: Float):DS {
            var returnValue:DS = FORTH
            when(dbValue) {
                FIRST.value -> returnValue = FIRST
                SECOND.value -> returnValue = SECOND
                THIRD.value -> returnValue = THIRD
                FORTH.value -> returnValue = FORTH
                FIFTH.value -> returnValue = FIFTH
                SIXTH.value -> returnValue = SIXTH
                SEVENTH.value -> returnValue = SEVENTH
                EIGHTH.value -> returnValue = EIGHTH
                NINTH.value -> returnValue = NINTH
                TENTH.value -> returnValue = TENTH
                ELEVENTH.value -> returnValue = ELEVENTH
                TWELVETH.value -> returnValue = TWELVETH
                THIRTEENTH.value -> returnValue = THIRTEENTH
            }
            return returnValue
        }


        fun getProperDS(
                notRememberedCountSinceUnderLimitReached: Int,
                updatedRRT: RRT
        ): DS{

            var returnDS = FORTH

            when(updatedRRT) {
                RRT.TENTH -> { returnDS = THIRTEENTH }
                RRT.NINTH -> { returnDS = TWELVETH }
                RRT.EIGHTH -> { returnDS = ELEVENTH }
                RRT.SEVENTH -> { returnDS = TENTH }
                RRT.SIXTH -> { returnDS = NINTH }
                RRT.FIFTH -> { returnDS = EIGHTH }
                RRT.FORTH -> { returnDS = SEVENTH }
                RRT.THIRD -> { returnDS = SIXTH }
                RRT.SECOND -> { returnDS = FIFTH }
                RRT.FIRST -> {
                    when(notRememberedCountSinceUnderLimitReached) {
                        in 0..1 -> { returnDS = FORTH }
                        in 2..4 -> { returnDS = THIRD }
                        in 5..8 -> { returnDS = SECOND }
                        in 9..2147483647 -> { returnDS = FIRST }
                    }
                }
            }
            return returnDS
        }
    }
}