/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.model.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WordDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(wordEntity: WordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReference(referenceEntity: ReferenceEntity)

    @Delete
    suspend fun deleteWord(wordEntity: WordEntity)

    @Update
    suspend fun updateWord(wordEntity: WordEntity)

    @Query(
        "SELECT * FROM word_table"
    )
    suspend fun getAllWords(): List<WordEntity>

    @Query(
        "SELECT * FROM word_table WHERE word = :word"
    )
    suspend fun getCertainWord(word: String): List<WordEntity>

    @Query(
        "SELECT * FROM word_table WHERE green in (:greens) AND difficultyScore BETWEEN :minDS and :maxDS AND registeredDate BETWEEN :minDate and :maxDate AND reference IN (:references) ORDER BY registeredDate DESC"
    )
    fun getAllWordsSortedByDateDesc(greens: List<Boolean>,
                                    minDS: Float,
                                    maxDS: Float,
                                    minDate: Long,
                                    maxDate: Long,
                                    references: List<String>): LiveData<List<WordEntity>>

    @Query("SELECT * FROM word_table WHERE green in (:greens) AND difficultyScore BETWEEN :minDS and :maxDS AND registeredDate BETWEEN :minDate and :maxDate AND reference IN (:references) ORDER BY registeredDate ASC")
    fun getAllWordsSortedByDateAsc(greens: List<Boolean>,
                                   minDS: Float,
                                   maxDS: Float,
                                   minDate: Long,
                                   maxDate: Long,
                                   references: List<String>): LiveData<List<WordEntity>>

    @Query("SELECT * FROM word_table WHERE green in (:greens) AND difficultyScore BETWEEN :minDS and :maxDS AND registeredDate BETWEEN :minDate and :maxDate AND reference IN (:references) ORDER BY difficultyScore DESC")
    fun getAllWordsSortedByDSDesc(greens: List<Boolean>,
                                  minDS: Float,
                                  maxDS: Float,
                                  minDate: Long,
                                  maxDate: Long,
                                  references: List<String>): LiveData<List<WordEntity>>

    @Query("SELECT * FROM word_table WHERE green in (:greens) AND difficultyScore BETWEEN :minDS and :maxDS AND registeredDate BETWEEN :minDate and :maxDate AND reference IN (:references) ORDER BY difficultyScore ASC")
    fun getAllWordsSortedByDSAsc(greens: List<Boolean>,
                                 minDS: Float,
                                 maxDS: Float,
                                 minDate: Long,
                                 maxDate: Long,
                                 references: List<String>): LiveData<List<WordEntity>>

    @Query("SELECT * FROM word_table WHERE green in (:greens) AND difficultyScore BETWEEN :minDS and :maxDS AND registeredDate BETWEEN :minDate and :maxDate AND reference IN (:references) ORDER BY word DESC")
    fun getAllWordsSortedByWordDesc(greens: List<Boolean>,
                                    minDS: Float,
                                    maxDS: Float,
                                    minDate: Long,
                                    maxDate: Long,
                                    references: List<String>): LiveData<List<WordEntity>>

    @Query("SELECT * FROM word_table WHERE green in (:greens) AND difficultyScore BETWEEN :minDS and :maxDS AND registeredDate BETWEEN :minDate and :maxDate AND reference IN (:references) ORDER BY word ASC")
    fun getAllWordsSortedByWordAsc(greens: List<Boolean>,
                                   minDS: Float,
                                   maxDS: Float,
                                   minDate: Long,
                                   maxDate: Long,
                                   references: List<String>): LiveData<List<WordEntity>>

    @Query("SELECT MIN(difficultyScore) FROM word_table")
    fun getMinDS(): Float

    @Query("SELECT MAX(difficultyScore) FROM word_table")
    fun getMaxDS(): Float

    @Query("SELECT MIN(registeredDate) FROM word_table")
    fun getMinDate(): Long

    @Query("SELECT MAX(registeredDate) FROM word_table")
    fun getMaxDate(): Long

    @Query("SELECT DISTINCT reference FROM reference_table")
    fun getAllReferences(): List<String>

}