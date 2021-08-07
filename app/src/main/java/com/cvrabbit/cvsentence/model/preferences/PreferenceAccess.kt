/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.model.preferences

import android.content.SharedPreferences
import com.cvrabbit.cvsentence.model.repository.SortPattern
import com.cvrabbit.cvsentence.model.repository.WordFilter

class PreferenceAccess(private val pref: SharedPreferences) {

    fun saveOnSelectWordSoundSetting(onSelectWordSound:Boolean){
        val editor = pref.edit()
        editor.putBoolean("ON_SELECT_WORD_SOUND", onSelectWordSound).apply()
    }
    fun saveOnSelectMeaningSoundSetting(onSelectMeaningSound:Boolean){
        val editor = pref.edit()
        editor.putBoolean("ON_SELECT_MEANING_SOUND", onSelectMeaningSound).apply()
    }
    fun saveOnDemandWordSoundSetting(onDemandWordSound:Boolean){
        val editor = pref.edit()
        editor.putBoolean("ON_DEMAND_WORD_SOUND", onDemandWordSound).apply()
    }
    fun saveOnDemandMeaningSoundSetting(onDemandMeaningSound:Boolean) {
        val editor = pref.edit()
        editor.putBoolean("ON_DEMAND_MEANING_SOUND", onDemandMeaningSound).apply()
    }
    fun getOnSelectWordSoundSetting():Boolean{
        return pref.getBoolean("ON_SELECT_WORD_SOUND",false)
    }
    fun getOnSelectMeaningSoundSetting():Boolean{
        return pref.getBoolean("ON_SELECT_MEANING_SOUND",false)
    }
    fun getOnDemandWordSoundSetting():Boolean{
        return pref.getBoolean("ON_DEMAND_WORD_SOUND",false)
    }
    fun getOnDemandMeaningSoundSetting(): Boolean {
        return pref.getBoolean("ON_DEMAND_MEANING_SOUND",false)
    }
    fun saveIfFloatingRight(right: Boolean) {
        val editor = pref.edit()
        editor.putBoolean("IF_FLOATING_RIGHT", right).apply()
    }
    fun getFloatingPosition():Boolean {
        return pref.getBoolean("IF_FLOATING_RIGHT",true)
    }
    fun saveTwitterAccessToken(token: String, tokenSecret: String) {
        val editor = pref.edit()
        editor.putString("TWITTER_TOKEN", token)
            .putString("TWITTER_TOKEN_SECRET", tokenSecret)
            .apply()
    }
    fun getTwitterAccessToken():Pair<String?, String?> {
        return Pair(pref.getString("TWITTER_TOKEN", ""), pref.getString("TWITTER_TOKEN_SECRET", ""))
    }
    /**
     * Methods below will be used to handle guidance that run
     * only for the first time user taking certain action after installation.
     * Every time user take target action, run getIfFirstTime method,
     * and show guidance only when return of the method is true.
     * (Default value of this method is true)
     * Then after showing the guidance, run setIfFirstTime method.
     * After that, getIfFirstTime method will return only false.
     */
    fun getIfShowMainFirstTime():Boolean {
        return pref.getBoolean("SHOW_OVERLAY_FIRST_TIME",true)
    }
    fun setIfShowMainFirstTime() {
        pref.edit().putBoolean("SHOW_OVERLAY_FIRST_TIME", false).apply()
    }
    fun saveSortSetting(sortPattern: SortPattern) {

    }
    fun getSortPattern(): SortPattern {

    }
    fun saveFilter(filter: WordFilter) {

    }
    fun getFilter(): WordFilter {

    }
}