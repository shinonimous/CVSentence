package com.cvrabbit.cvsentence.util.constant

object Constants {

    // Overlay Permission Constants
    const val OVERLAY_PERMISSION_REQUEST_CODE = 1

    // Internet WordSearch Constants
    const val WORDNIK_ACCESS_URL = "https://api.wordnik.com/v4/word.json/"
    const val WORDNIK_FIXED_PARAMETERS = "/definitions?limit=200&includeRelated=false&sourceDictionaries=wiktionary&useCanonical=true&includeTags=false&api_key="
    const val WORDNIK_API_KEY = "3jen77gz0mls6ksarkco8rzd9u1rhrxb8pu9f7k5309wrr28n"

    const val EDICT_ACCESS_URL = "https://www.edrdg.org/cgi-bin/wwwjdic/"

    const val ENGLISH_CHECK_REGEX_ETCETERA_ALLOWED = """^[a-zA-Z0-9'*./?_-]*${'$'}"""
    const val ENGLISH_CHECK_REGEX_SPACE_ALLOWED = """^[a-zA-Z0-9'\u0020\u00a0-]*${'$'}"""
    const val ENGLISH_CHECK_REGEX_SPACE_NOT_ALLOWED = """^[a-zA-Z0-9'-]*${'$'}"""

    // Database Constants
    const val RUNNING_DATABASE_NAME = "word_db"

    // Preference
    const val NOT_INITIALIZED_DS = -1f
    const val NOT_INITIALIZED_DATE = -1L

    // Twitter Constants
    const val CONSUMER_KEY = "vFiYVloPBnnwnV2YnhZ1rfxKi"
    const val CONSUMER_SECRET = "Ez8cOrs3iCYK5MIyuprEEZTG7ulFp4mvT6BpbvQD61vVagBSb9"
    const val CALLBACK_URL = "callback://"
    const val GOOGLE_PLAY_LINK = "https://play.google.com/store/apps/details?id=com.cvrabbit.cvsentence"
    const val HOMEPAGE_LINK = "https://cv-rabbit.com/top"

    // Service Constants
    const val ACTION_SHOW = "SHOW"
    const val ACTION_HIDE = "HIDE"
    const val NOTIFICATION_CHANNEL_ID = "channel_id"
    const val NOTIFICATION_CHANEL_NAME = "channel_name"
    const val NOTIFICATION_ID = 1
    const val OVERLAY_MEANING_SHOWING_INTERVAL = 6000L
    const val CLIPBOARD_COROUTINE_DELAY_INTERVAL = 500L

    // AdMob
    const val ADMOB_AD_UNIT_ID = "ca-app-pub-1302766634885116/6683148954"
}