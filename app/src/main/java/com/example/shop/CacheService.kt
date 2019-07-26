package com.example.shop

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class CacheService(): IntentService("CacheService"), AnkoLogger{
    override fun onHandleIntent(p0: Intent?) {
        info("onHandleIntent")
        Thread.sleep(5000)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        info("onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        info("onDestroy")
    }
}