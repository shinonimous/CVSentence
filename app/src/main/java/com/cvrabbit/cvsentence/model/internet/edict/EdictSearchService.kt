/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.model.internet.edict

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface EdictSearchService {

    @GET("wwwjdic?1ZD{edictType}{searchStr}")
    fun getWordMeaning(@Path("edictType") edictType: String, @Path("searchStr") searchStr: String): Call<ResponseBody>

}