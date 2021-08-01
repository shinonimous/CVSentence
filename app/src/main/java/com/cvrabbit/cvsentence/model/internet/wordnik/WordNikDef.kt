/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.model.internet.wordnik

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Serializable
class WordNikDef (
    val partOfSpeech: String,
    val text: String,
    val word: String
)

fun getWordNikDef (apiResponse: String): List<WordNikDef> {
    return Json {ignoreUnknownKeys = true }.decodeFromString(
        ListSerializer(WordNikDef.serializer()),
        apiResponse
    )
}