/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.util.calendar

import java.text.SimpleDateFormat
import java.util.*

object CalendarOperation {

    fun minusDate(dateA: Date, dateB: Date):Long {
        return (dateA.time - dateB.time)
    }

    fun durationMillisToDurationDateString(durationMillis: Long): String {
        val oneDayMillis = 1000 * 60 * 60 * 24
        val intVal = (durationMillis / oneDayMillis).toInt()
        return intVal.toString()
    }

    fun dateToStringFormat(registeredDate: Date): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN)
        return sdf.format(registeredDate)
    }

    fun plusMillisToDate(targetDate: Date, addMillis: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = targetDate
        calendar.add(Calendar.MILLISECOND, addMillis)
        return calendar.time
    }

    fun getFirstMillisOfMonth(startDateStr: String):Date {
        val sdf = SimpleDateFormat("yyyy/MM", Locale.JAPAN)
        return sdf.parse(startDateStr)!!
    }

    fun getLastMillisOfMonth(endDateStr: String):Date {
        val firstMillisOfMonth = getFirstMillisOfMonth(endDateStr)
        val calendar = Calendar.getInstance()
        calendar.time = firstMillisOfMonth
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        return calendar.time
    }
}