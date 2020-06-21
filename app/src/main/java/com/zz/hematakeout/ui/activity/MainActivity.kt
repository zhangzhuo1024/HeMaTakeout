package com.zz.hematakeout.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.zz.hematakeout.R
import com.zz.hematakeout.ui.fragment.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    val fragmentList =
        listOf<Fragment>(HomeFragment(), OrderFragment(), UserFragment(), MoreFragment())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initBottomBar()
        initPermission()
    }

    private fun initBottomBar() {
        changeChildColor(0)
        for (i in 0 until bottom_bar.childCount) {
            bottom_bar.getChildAt(i).setOnClickListener {
                changeChildColor(i)
            }
        }
    }

    private fun changeChildColor(i: Int) {
        for (j in 0 until bottom_bar.childCount) {
            if (i == j) {
                lightTheChild(false, bottom_bar.getChildAt(j))
            } else {
                lightTheChild(true, bottom_bar.getChildAt(j))
            }
        }
        supportFragmentManager.beginTransaction().replace(R.id.view_container, fragmentList[i])
            .commit()
    }

    private fun lightTheChild(isEnable: Boolean, childAt: View?) {
        if (childAt == null) {
            return
        }
        childAt.isEnabled = isEnable
        if (childAt is ViewGroup) {
            for (k in 0 until childAt.childCount) {
                childAt.getChildAt(k).isEnabled = isEnable
            }
        }
    }


    var mPermissionList: MutableList<String> = ArrayList()

    private fun initPermission() {
        mPermissionList.clear() //清空没有通过的权限
        val permissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        //逐个判断你要的权限是否已经通过
        for (i in permissions.indices) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permissions[i]
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                mPermissionList.add(permissions[i]) //添加还未授予的权限
            }
        }

        //申请权限
        if (mPermissionList.size > 0) {
            //有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this, permissions, 500)
        } else {
            //说明权限都已经通过，可以做你想做的事情去
        }
    }
}