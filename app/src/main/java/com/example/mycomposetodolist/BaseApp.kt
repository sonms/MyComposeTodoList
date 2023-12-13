package com.example.mycomposetodolist

import java.io.File
import android.app.Application

class BaseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val dexOutputDir: File = codeCacheDir
        dexOutputDir.setReadOnly()
    }
}