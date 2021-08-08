package com.cvrabbit.cvsentence.model.internet

import com.cvrabbit.cvsentence.model.db.WordEntity

interface WordSearch {
    fun searchWord(requestWord: String): WordEntity?
}