package com.zz.hematakeout.model.net

import retrofit2.Call
import retrofit2.http.GET




interface TakeoutService {

    @GET("home")
    fun getHomeInfo(): Call<ResponseInfo>
}