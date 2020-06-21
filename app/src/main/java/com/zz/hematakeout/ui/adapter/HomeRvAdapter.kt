package com.zz.hematakeout.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.TextSliderView
import com.squareup.picasso.Picasso
import com.zz.hematakeout.R
import com.zz.hematakeout.model.bean.Promotion
import com.zz.hematakeout.model.bean.Seller

class HomeRvAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        val TYTE_TITLE = 0
        val TYTE_SELLER = 1
    }

    var mNearbySellerList: ArrayList<Seller> = ArrayList()
    var mPromotionList: ArrayList<Promotion> = ArrayList()

    fun initData() {
    }

    fun updateData(nearbySellerList: java.util.ArrayList<Seller>, promotionList: ArrayList<Promotion>) {
        mNearbySellerList.clear()
        mPromotionList.clear()
        mNearbySellerList = nearbySellerList
        mPromotionList = promotionList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        when (position) {
            0 -> return TYTE_TITLE
            else -> return TYTE_SELLER
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var homeItem = LayoutInflater.from(parent.context).inflate(R.layout.item_seller, null)
        var titleItem = LayoutInflater.from(parent.context).inflate(R.layout.item_title, null)
        when (viewType) {
            TYTE_TITLE-> return TitleRvHolder(titleItem)
            else ->  return HomeRvHolder(homeItem)
        }
    }

    override fun getItemCount(): Int {
        return mNearbySellerList.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder
        when (holder.itemViewType) {
            TYTE_TITLE -> {
                itemHolder as TitleRvHolder
                itemHolder.bindData(mPromotionList)
            }
            else -> {
                itemHolder as HomeRvHolder
                itemHolder.bindData(mNearbySellerList[position - 1])
            }
        }
    }


    inner class HomeRvHolder(val item: View) : RecyclerView.ViewHolder(item) {
        val tvTitle: TextView
        val ivLogo: ImageView
        val rbScore: RatingBar
        val tvSale: TextView
        val tvSendPrice: TextView
        val tvDistance: TextView

        init {
            tvTitle = item.findViewById(R.id.tv_title)
            ivLogo = item.findViewById(R.id.seller_logo)
            rbScore = item.findViewById(R.id.ratingBar)

            tvSale = item.findViewById(R.id.tv_home_sale)
            tvSendPrice = item.findViewById(R.id.tv_home_send_price)
            tvDistance = item.findViewById(R.id.tv_home_distance)
        }

        fun bindData(seller: Seller) {
            tvTitle.text = seller.name
            Picasso.with(item.context).load(seller.icon).into(ivLogo)
            rbScore.rating = seller.score.toFloat()
            tvSale.text = "月售${seller.sale}单"
            tvSendPrice.text = "￥${seller.sendPrice}起送/配送费￥${seller.deliveryFee}"
            tvDistance.text = seller.distance
        }
    }

    inner class TitleRvHolder(var titleItem: View) : RecyclerView.ViewHolder(titleItem) {
        private var mSlider: SliderLayout

        init {
            mSlider = titleItem.findViewById(R.id.slider)
        }

        fun bindData(promotions: ArrayList<Promotion>) {
            for (promotion in promotions) {
                val textSliderView = TextSliderView(titleItem.context)
                textSliderView.description(promotion.info).image(promotion.pic)
                mSlider.addSlider(textSliderView)
            }
        }
    }

}
