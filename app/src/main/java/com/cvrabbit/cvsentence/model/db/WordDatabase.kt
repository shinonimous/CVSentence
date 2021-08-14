/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.model.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [WordEntity::class, ReferenceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WordDatabase: RoomDatabase() {
    abstract fun getWordDAO(): WordDAO
}