/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.util.calendar

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "CalendarOperation"

object CalendarOperation {

    fun minusDateLong(dateA: Long, dateB: Long):Long {
        return (dateA - dateB)
    }

    fun durationMillisToDurationDateString(durationMillis: Long): String {
        val oneDayMillis = 1000 * 60 * 60 * 24
        val intVal = (durationMillis / oneDayMillis).toInt()
        Log.d(TAG, "durationMillisToDateString is Running: durationMills: $durationMillis, dateString: $intVal")
        return intVal.toString()
    }

    fun longDateToStringyyyyMMddFormat(regDateLong: Long): String {
        val regDate = Date(regDateLong)
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN)
        return sdf.format(regDate)
    }

    fun longDateToStringyyyyMMFormat(regDateLong: Long): String {
        val regDate = Date(regDateLong)
        val sdf = SimpleDateFormat("yyyy/MM", Locale.JAPAN)
        return sdf.format(regDate)
    }

    fun addOneMonthToLongDate(longDate: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.time = Date(longDate)
        calendar.add(Calendar.MONTH, 1)
        return calendar.time.time
    }

    fun getFirstMillisOfMonth(startDateStr: String):Long {
        val sdf = SimpleDateFormat("yyyy/MM", Locale.JAPAN)
        return sdf.parse(startDateStr)!!.time
    }

    fun getLastMillisOfMonth(endDateStr: String):Long {
        val firstMillisOfMonth = getFirstMillisOfMonth(endDateStr)
        val calendar = Calendar.getInstance()
        calendar.time = Date(firstMillisOfMonth)
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        return calendar.time.time
    }
}