package com.cvrabbit.cvsentence.model.internet.edict

import android.content.Context
import android.util.Log
import com.cvrabbit.cvsentence.model.db.WordEntity
import com.cvrabbit.cvsentence.model.internet.WordSearch
import com.cvrabbit.cvsentence.model.internet.lang.OriginalWordGenerator
import com.cvrabbit.cvsentence.util.constant.Constants.EDICT_ACCESS_URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URL

class EdictSearch(private val context: Context): WordSearch {

    override fun searchWord(requestWord: String): WordEntity? {
        val wordEntity = WordEntity()
        val eir = getAPIResponse(requestWord)
        return if (eir.responseExist) {
            wordEntity.word = eir.requestedWord
            wordEntity.mainMeaning = eir.mainMeaning
            wordEntity.verb = eir.verb
            wordEntity.noun = eir.noun
            wordEntity.adjective = eir.adjective
            wordEntity.adverb = eir.adverb
            wordEntity.expression = eir.expression
            wordEntity.prefix = eir.prefix
            wordEntity.suffix = eir.suffix
            wordEntity.others = eir.others
            wordEntity
        } else {
            null
        }
    }

    private fun getAPIResponse(requestStr: String): EdictInterpretedResponse {
        val requestStr = requestStr.toLowerCase()
        var eir = EdictInterpretedResponse()
        if (!checkIfRequestValid(requestStr)) {
            return eir
        }
        runBlocking {
            val job = CoroutineScope(Dispatchers.IO).launch {
                eir = getProperEirObject(EdictType.EXACT_MATCH, requestStr)
                if(!eir.responseExist) {
                    eir = getProperEirObject(EdictType.COMMON_MATCH, requestStr)
                }
            }
            job.join()
        }
        return eir
    }

    private fun requestToEdict(edictType: EdictType, requestStr: String): String {
        val url = URL(EDICT_ACCESS_URL + "wwwjdic?1ZD${edictType.indicator}$requestStr")
        return url.readText()
    }

    private fun getProperEirObject(edictType: EdictType, requestStr: String): EdictInterpretedResponse {
        val response = requestToEdict(edictType, requestStr)
        var eir = EdictInterpretedResponse()
        val eir1 = EdictResponseInterpreter.interpretResponse(response)
        eir1.requestedWord = requestStr
        if(eir1.responseExist) {eir = eir1}

        else { // When API response doesn't exist -> doubt verb conjugation
            val eir2 = getOriginalVerbEirObject(edictType, requestStr)
            if(eir2.responseExist) {eir = eir2}

            else { // When API response (For original verb) doesn't exist -> doubt plural noun
                val eir3 = getOriginalNounEirObject(edictType, requestStr)
                if(eir3.responseExist) {eir = eir3}
            }
        }
        return eir
    }

    private fun getOriginalVerbEirObject(edictType: EdictType, requestStr: String): EdictInterpretedResponse {
        var eir = EdictInterpretedResponse()
        val mayBeOriginalVerb = OriginalWordGenerator(context).getPresentStr(requestStr)
        if (requestStr != mayBeOriginalVerb) {
            val response = requestToEdict(edictType, mayBeOriginalVerb)
            val originalVerbEir = EdictResponseInterpreter.interpretResponse(response)
            originalVerbEir.requestedWord = mayBeOriginalVerb
            if (originalVerbEir.verb != "") { eir = originalVerbEir }
            else { originalVerbEir.responseExist = false; eir = originalVerbEir }
        }
        return eir
    }

    private fun getOriginalNounEirObject(edictType: EdictType, requestStr: String): EdictInterpretedResponse {
        var eir = EdictInterpretedResponse()
        val mayBeOriginalNoun = OriginalWordGenerator(context).getSingleStr(requestStr)
        if (requestStr != mayBeOriginalNoun) {
            val response = requestToEdict(edictType, mayBeOriginalNoun)
            val originalNounEir = EdictResponseInterpreter.interpretResponse(response)
            originalNounEir.requestedWord = mayBeOriginalNoun
            if (originalNounEir.noun != "") { eir = originalNounEir }
            else { originalNounEir.responseExist = false; eir = originalNounEir }
        }
        return eir
    }

    private fun checkIfRequestValid(requestStr: String): Boolean {
        val returnBool = true

        if (!checkLengthOfWord(requestStr)) {
            return false
        }
        if (!checkIfEnglish(requestStr)) {
            Log.d("checkIfEnglish", "Not English")
            return false
        }

        return returnBool
    }

    // When using WordNet, max 71 length
    private fun checkLengthOfWord(requestStr: String): Boolean {
        return requestStr.length in 1..30
    }

    private fun checkIfEnglish(requestStr: String): Boolean {
        //return requestStr.matches(Regex("""^[a-zA-Z0-9'*./?_-]*${'$'}"""))
        //return requestStr.matches(Regex("""^[a-zA-Z0-9'\u0020\u00a0-]*${'$'}"""))
        return requestStr.matches(Regex("""^[a-zA-Z0-9'-]*${'$'}"""))
    }

}

enum class EdictType {
    EXACT_MATCH {override val indicator = "Q"},
    COMMON_MATCH {override val indicator = "P"};
    abstract val indicator: String
}