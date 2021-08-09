package com.cvrabbit.cvsentence.util.constant

enum class SortPattern {

    DATE_DESC { override var strValue = ""},
    DATE_ASC {override var strValue = ""},
    DS_DESC {override var strValue = ""},
    DS_ASC {override var strValue = ""},
    WORD_DESC {override var strValue = ""},
    WORD_ASC {override var strValue = ""};

    abstract var strValue: String

    companion object {
        fun setSortTypeStrArray(sortTypeStrArray: Array<String>) {
            DATE_DESC.strValue = sortTypeStrArray[0]
            DATE_ASC.strValue = sortTypeStrArray[1]
            DS_DESC.strValue = sortTypeStrArray[2]
            DS_ASC.strValue = sortTypeStrArray[3]
            WORD_ASC.strValue = sortTypeStrArray[4]
            WORD_DESC.strValue = sortTypeStrArray[5]
        }

        fun getSortPatternByStrValue(strValue: String): SortPattern {
            var returnPattern = DATE_DESC
            when (strValue) {
                DATE_DESC.strValue -> {returnPattern = DATE_DESC}
                DATE_ASC.strValue -> {returnPattern = DATE_ASC}
                DS_DESC.strValue -> {returnPattern = DS_DESC}
                DS_ASC.strValue -> {returnPattern = DS_ASC}
                WORD_ASC.strValue -> {returnPattern = WORD_ASC}
                WORD_DESC.strValue -> {returnPattern = WORD_DESC}
            }
            return returnPattern
        }
    }
}