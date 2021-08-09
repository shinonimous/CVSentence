package com.cvrabbit.cvsentence.util.constant

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