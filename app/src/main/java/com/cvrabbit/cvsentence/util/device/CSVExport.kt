/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.util.device

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.Toast
import com.cvrabbit.cvsentence.R
import com.cvrabbit.cvsentence.model.db.Word
import java.io.File
import java.io.FileWriter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class CSVExport(context: Context) {
    private val appContext = context.applicationContext
    private val fileNameConst = "_cvRabbit.csv"
    @SuppressLint("SimpleDateFormat")
    private fun generateFileName(): String {
        return (SimpleDateFormat("yyyyMMddHHmmss").format(Date()) + fileNameConst)
    }

    private val csvHeader = context.getString(R.string.csv_header)

    private fun getAppendWord(i: Word): String {
        return i.id.toString() + "," + i.word + "," + i.mainMeaning + "," +
                i.verb + "," + i.noun + "," + i.adjective + "," + i.adverb + "," +
                i.expression + "," + i.prefix + "," + i.suffix + "," + i.others + "," +
                i.lookupCount + "," + i.lastLookupDate + "," + i.recommendedRecurTiming + "," +
                i.difficultyScore + "," + i.notRememberedCount + "," + i.rememberedCount + "," +
                i.tryAddSameWordCount + "," + i.registeredDate + "," + i.reference + "\n"
    }

    fun saveWordsAsCSV(words: List<Word>): String {
        var savedDirText: String = ""
        if (words.isEmpty()) {
            Toast.makeText(appContext, appContext.getText(R.string.csv_non_word_registered), Toast.LENGTH_SHORT).show()
        } else {
            var fileWriter: FileWriter? = null
            val path = appContext.getExternalFilesDir(null)
            try {
                val file = File(path, generateFileName())
                fileWriter = FileWriter(file)
                fileWriter.append(csvHeader)
                for (i in words) {
                    fileWriter.append(getAppendWord(i))
                    fileWriter.flush()
                }
                Toast.makeText(appContext, appContext.getText(R.string.csv_export_succeeded), Toast.LENGTH_SHORT).show()
                savedDirText = appContext.getText(R.string.bsc_csv_export_save_dir).toString() + '\n' +
                        path.toString()
            } catch (e: Exception) {
                Toast.makeText(appContext, appContext.getText(R.string.csv_export_failed), Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } finally {
                try {
                    fileWriter!!.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return savedDirText
    }
}