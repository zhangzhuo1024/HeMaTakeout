package com.zz.hematakeout.ui.activity

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.smssdk.EventHandler
import cn.smssdk.OnDialogListener
import cn.smssdk.SMSSDK
import com.mob.MobSDK
import com.mob.OperationCallback
import com.zz.hematakeout.R
import com.zz.hematakeout.smsSdk.privacy.PrivacyDialog
import com.zz.hematakeout.smsSdk.util.DemoSpHelper

import com.zz.hematakeout.util.LogUtils
import com.zz.hematakeout.util.SMSUtil
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    var eh: EventHandler = object : EventHandler() {
        override
        fun afterEvent(event: Int, result: Int, data: Any) {
            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                }
            } else {
                (data as Throwable).printStackTrace()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initListener()
        initPermission()
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }

    private fun initListener() {

        var tvCode: TextView = findViewById(R.id.tvCode)
        var tvVerify: TextView = findViewById(R.id.tvVerify)
        tvCode.setOnClickListener {
            checkPrivacyGranted()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SMSSDK.unregisterEventHandler(eh) //注册短信回调
    }

    private fun initPermission() {
        val readPhone = checkSelfPermission("android.permission.READ_PHONE_STATE")
        val receiveSms = checkSelfPermission("android.permission.RECEIVE_SMS")
        val readContacts = checkSelfPermission("android.permission.READ_CONTACTS")
        val readSdcard = checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE")
        var requestCode = 0
        val permissions = ArrayList<String>()
        if (readPhone != PackageManager.PERMISSION_GRANTED) {
            requestCode = requestCode or (1 shl 0)
            permissions.add("android.permission.READ_PHONE_STATE")
        }
        if (receiveSms != PackageManager.PERMISSION_GRANTED) {
            requestCode = requestCode or (1 shl 1)
            permissions.add("android.permission.RECEIVE_SMS")
        }
        if (readContacts != PackageManager.PERMISSION_GRANTED) {
            requestCode = requestCode or (1 shl 2)
            permissions.add("android.permission.READ_CONTACTS")
        }
        if (readSdcard != PackageManager.PERMISSION_GRANTED) {
            requestCode = requestCode or (1 shl 3)
            permissions.add("android.permission.READ_EXTERNAL_STORAGE")
        }
        if (requestCode > 0) {
            val permission = arrayOfNulls<String>(permissions.size)
            requestPermissions(permissions.toArray(permission), requestCode)
            return
        }
    }

    private fun checkPrivacyGranted() {
        LogUtils.e(
            " LoginActivity " + " isPrivacyGranted  " + DemoSpHelper.getInstance()
                .isPrivacyGranted()
        )
        if (!DemoSpHelper.getInstance().isPrivacyGranted()) {
            val privacyDialog = PrivacyDialog(this, object : OnDialogListener {
                override fun onAgree() {
                    Log.e("Takeout", " LoginActivity " + "PrivacyDialog onAgree")
                    uploadResult(true)
                    DemoSpHelper.getInstance().setPrivacyGranted(true)
                }

                override fun onDisagree() {
                    Log.e("Takeout", " LoginActivity " + "PrivacyDialog onDisagree")
                    uploadResult(false)
                    DemoSpHelper.getInstance().setPrivacyGranted(false)
                    val handler = Handler(Handler.Callback {
                        false
                    })
                    handler.sendEmptyMessageDelayed(0, 500)
                }
            })
            privacyDialog.show()
        } else {
            uploadResult(true)
        }
    }

    private fun uploadResult(granted: Boolean) {
        MobSDK.submitPolicyGrantResult(granted, object : OperationCallback<Void?>() {
            override fun onComplete(aVoid: Void?) {
                Log.e("Takeout", " LoginActivity " + "submitPolicyGrantResult onComplete")
                getTheTvCode()
            }

            override fun onFailure(throwable: Throwable) { // Nothing to do
                Log.e("Takeout", " LoginActivity " + "submitPolicyGrantResult onFailure")
            }
        })
    }

    private fun getTheTvCode() {
        if (!isNetworkConnected()) {
            Toast.makeText(this, getString(R.string.smssdk_network_error), Toast.LENGTH_SHORT)
                .show();
        }
        val phone = etPhone.getText().toString().trim()
        Log.e("Takeout", " LoginActivity " + "getTheTvCode" + phone)
        if (SMSUtil.judgePhoneNums(this, phone)) {
            SMSSDK.getVerificationCode("86", phone)
            tvCode.isEnabled = false
            Thread(CutDownTask()).start()
        }
    }

    private fun isNetworkConnected(): Boolean {
        val manager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun submitPrivacyGrantResult(granted: Boolean) {
        MobSDK.submitPolicyGrantResult(granted, object : OperationCallback<Void?>() {
            override fun onComplete(data: Void?) {
                LogUtils.d("隐私协议授权结果提交：成功")
            }

            override fun onFailure(t: Throwable) {
                LogUtils.d("隐私协议授权结果提交：失败")
            }
        })
    }

    companion object{
        val TIME_MINUS = 1
        val TIME_OUT = 0
    }

    var mHandler: Handler = Handler {
        when (it.what) {
            TIME_MINUS -> {
                if (mTime > 0) {
                    tvCode.text = "剩余时间${mTime}秒"
                    it.target.sendEmptyMessageDelayed(TIME_MINUS, 999)
                    mTime--
                } else {
                    it.target.sendEmptyMessage(TIME_OUT)
                }
                false
            }
            else -> {
                tvCode.isEnabled = true
                tvCode.text = "点击重发"
                mTime = 60
                false
            }
        }
    }

    var mTime: Int = 60

    inner class CutDownTask : Runnable {
        override fun run() {
            mHandler.sendEmptyMessageDelayed(TIME_MINUS, 999)
        }
    }
}
