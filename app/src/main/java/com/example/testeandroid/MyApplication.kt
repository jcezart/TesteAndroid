package com.example.testeandroid

import android.app.Application

class MyApplication : Application() {
    init {
        appContext = this
    }

    companion object {
        lateinit var appContext: Application
            private set
    }
}
