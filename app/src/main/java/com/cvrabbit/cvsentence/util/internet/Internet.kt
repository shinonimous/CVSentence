/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.util.internet

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class Internet(context: Context) {
    private val appContext = context.applicationContext
    fun checkInternetConnection(): Boolean {
        val connectivityManager =
            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkCapabilities? =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return activeNetwork != null
    }
}