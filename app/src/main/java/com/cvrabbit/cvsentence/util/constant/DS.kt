package com.cvrabbit.cvsentence.util.constant

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