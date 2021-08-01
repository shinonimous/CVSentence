/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm
import io.realm.RealmConfiguration

@HiltAndroidApp
class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmUtility.getDefaultConfig()
        Realm.setDefaultConfiguration(config)
    }
}

object RealmUtility {
    private val SCHEMA_V_1 = 100000L

    fun getDefaultConfig():RealmConfiguration {
        return RealmConfiguration.Builder()
                .schemaVersion(SCHEMA_V_1)
                .deleteRealmIfMigrationNeeded()
                .allowWritesOnUiThread(true)
                .build()
    }
}