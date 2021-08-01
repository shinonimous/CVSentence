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

@Entity(tableName = "reference_table")
data class ReferenceEntity(
    var reference: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}