package com.example.mysteps

import android.app.Application
import com.example.mysteps.data.SupabaseClient
import io.github.jan.supabase.gotrue.SettingsSessionManager
import io.github.jan.supabase.gotrue.auth
import org.osmdroid.config.Configuration

class MyStepsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("osmdroid", MODE_PRIVATE)
        Configuration.getInstance().load(this, prefs)

        Configuration.getInstance().userAgentValue = "MySteps2/1.0 (contact: toshikycheba@example.com)"

        Configuration.getInstance().osmdroidTileCache = cacheDir
    }
}