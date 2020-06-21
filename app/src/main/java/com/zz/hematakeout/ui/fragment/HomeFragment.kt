package com.zz.hematakeout.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zz.hematakeout.R
import com.zz.hematakeout.model.bean.Promotion
import com.zz.hematakeout.model.bean.Seller
import com.zz.hematakeout.presenter.HomeFragmentPresenter
import com.zz.hematakeout.ui.adapter.HomeRvAdapter
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    lateinit var mRvHome: RecyclerView
    lateinit var homeRvAdapter: HomeRvAdapter
    lateinit var homeFragmentPresenter:HomeFragmentPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initView()
    }

    private fun initView(): View? {
        val homeView = LayoutInflater.from(activity).inflate(R.layout.fragment_home, null)


        mRvHome = homeView.findViewById(R.id.rv_home)
        mRvHome.layoutManager = LinearLayoutManager(activity)
        homeRvAdapter = HomeRvAdapter()
        mRvHome.adapter = homeRvAdapter
        homeFragmentPresenter = HomeFragmentPresenter(this)

        return homeView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()

    }

    private fun initData() {
        homeFragmentPresenter.getHomeInfo()
        homeRvAdapter.initData()
    }

    fun onHomeSucess(
        nearbySellerList: ArrayList<Seller>,
        promotionList: ArrayList<Promotion>
    ) {

        homeRvAdapter.updateData(nearbySellerList, promotionList)
    }


}
