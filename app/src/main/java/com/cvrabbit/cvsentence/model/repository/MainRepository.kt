package com.cvrabbit.cvsentence.model.repository

import com.cvrabbit.cvsentence.model.db.ReferenceEntity
import com.cvrabbit.cvsentence.model.db.WordDAO
import com.cvrabbit.cvsentence.model.db.WordEntity
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val wordDAO: WordDAO
) {

    suspend fun insertWord(wordEntity: WordEntity) = wordDAO.insertWord(wordEntity)

    suspend fun insertReference(referenceEntity: ReferenceEntity) = wordDAO.insertReference(referenceEntity)

    suspend fun deleteWord(wordEntity: WordEntity) = wordDAO.deleteWord(wordEntity)

    fun getAllWordsSortedBySortedByDateDesc(
        greens: List<Boolean>,
        minDS: Float,
        maxDS: Float,
        minDate: Long,
        maxDate: Long,
        references: List<String>
    ) = wordDAO.getAllWordsSortedByDateDesc(
        greens, minDS, maxDS, minDate, maxDate, references
    )

    fun getAllWordsSortedBySortedByDateAsc(
        greens: List<Boolean>,
        minDS: Float,
        maxDS: Float,
        minDate: Long,
        maxDate: Long,
        references: List<String>
    ) = wordDAO.getAllWordsSortedByDateAsc(
        greens, minDS, maxDS, minDate, maxDate, references
    )

    fun getAllWordsSortedBySortedByDSDesc(
        greens: List<Boolean>,
        minDS: Float,
        maxDS: Float,
        minDate: Long,
        maxDate: Long,
        references: List<String>
    ) = wordDAO.getAllWordsSortedByDSDesc(
        greens, minDS, maxDS, minDate, maxDate, references
    )

    fun getAllWordsSortedBySortedByDSAsc(
        greens: List<Boolean>,
        minDS: Float,
        maxDS: Float,
        minDate: Long,
        maxDate: Long,
        references: List<String>
    ) = wordDAO.getAllWordsSortedByDSAsc(
        greens, minDS, maxDS, minDate, maxDate, references
    )

    fun getAllWordsSortedBySortedByWordDesc(
        greens: List<Boolean>,
        minDS: Float,
        maxDS: Float,
        minDate: Long,
        maxDate: Long,
        references: List<String>
    ) = wordDAO.getAllWordsSortedByWordDesc(
        greens, minDS, maxDS, minDate, maxDate, references
    )

    fun getAllWordsSortedBySortedByWordAsc(
        greens: List<Boolean>,
        minDS: Float,
        maxDS: Float,
        minDate: Long,
        maxDate: Long,
        references: List<String>
    ) = wordDAO.getAllWordsSortedByWordAsc(
        greens, minDS, maxDS, minDate, maxDate, references
    )
}