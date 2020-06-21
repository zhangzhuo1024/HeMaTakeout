package com.zz.hematakeout.presenter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zz.hematakeout.model.bean.Promotion
import com.zz.hematakeout.model.bean.Seller
import com.zz.hematakeout.model.net.ResponseInfo
import com.zz.hematakeout.model.net.TakeoutService
import com.zz.hematakeout.ui.fragment.HomeFragment
import com.zz.hematakeout.util.LogUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HomeFragmentPresenter(val homeFragment: HomeFragment) {
    fun getHomeInfo() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.43.74:8080/TakeoutService/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val takeoutService: TakeoutService = retrofit.create<TakeoutService>(TakeoutService::class.java)
        val homeInfo = takeoutService.getHomeInfo()
        homeInfo.enqueue(object : Callback<ResponseInfo> {
            override fun onFailure(call: Call<ResponseInfo>, t: Throwable) {
                LogUtils.e("homeInfo  onFailure Throwable = " + t)
            }

            override fun onResponse(call: Call<ResponseInfo>, response: Response<ResponseInfo>) {
                LogUtils.e("homeInfo  response = " + response)
                if (response == null) {
                    LogUtils.e("response == null")
                } else {
                    LogUtils.e("response.code() = " + response.code())
                    LogUtils.e("response.body() = " + response.body())
                    if (response.isSuccessful) {
                        val responseInfo = response.body()
                        if (responseInfo == null) {
                            LogUtils.e("responseInfo == null")
                            return
                        }
                        LogUtils.e("responseInfo.code = " + responseInfo.code)
                        LogUtils.e("responseInfo.data = " + responseInfo.data)
                        if (responseInfo.code.equals("0")) {
                            parseJson(responseInfo.data);
                        }
                    }
                }
            }

        })
    }

    private fun parseJson(data: String) {
        val jsonObject = JSONObject(data)
        val nearBy = jsonObject.getString("nearbySellerList")
        val promotion = jsonObject.getString("promotionList")

        val gson = Gson()
        var nearbySellerList : ArrayList<Seller> = gson.fromJson(nearBy, object : TypeToken<ArrayList<Seller>>() {}.type)
        var promotionList : ArrayList<Promotion> = gson.fromJson(promotion, object : TypeToken<ArrayList<Promotion>>() {}.type)

        homeFragment.onHomeSucess(nearbySellerList, promotionList)
    }

}