package com.zz.hematakeout.model.bean

import android.content.pm.ActivityInfo
import java.io.Serializable


data class Seller(var id: Long, var pic: String, var name: String, var score: String, var sale: String,
                  var ensure: String, var invoice: String, var sendPrice: String, var deliveryFee: String,
                  var recentVisit: String, var distance: String, var time: String, var icon: String,
                  var activityList: java.util.ArrayList<ActivityInfo>
) : Serializable {


    /**
     * "id":1,
     * "pic":http://xxxxxxxxxx.jpg,
     * "name":"二十五块半（上地店）",
     *
     * "score":"4.4",
     * "sale":4132,//销量
     * "ensure":1,//是否有转送保证
     *
     * "invoice":1,//是否提供发票
     * "sendPrice":20,//起送价格
     * "deliveryFee":4,//配送费
     *
     * "recentVisit":1,//是否最近光顾
     * "distance":"773m",
     * "time":"34分钟",
     *
     * "activityList":,[{//活动列表
     * "id":1,
     * "type":1,// 活动类型，详见附表
     * "info":"在线支付，满30减8"
     * },{
     * "id":2,
     * "type":2,
     * " info ":"16.9元特价超值套餐！"
     * }]
     *
     */

}