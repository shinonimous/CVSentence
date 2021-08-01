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

open class Reference: RealmObject() {
    @PrimaryKey
    var id: Long = 0
    var reference: String = ""
}