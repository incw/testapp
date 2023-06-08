package com.example.testapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.databinding.ActivityMainBinding
import com.google.firebase.ktx.BuildConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        preferences = getSharedPreferences(LINK, Context.MODE_PRIVATE)
        initApp()
    }

    private fun checkPreferencesLink(): String? {
        return preferences.getString(URL, "")
    }

    private fun internet(context: Context): Boolean {
        val connect = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val internet = connect.activeNetwork ?: return false
            val activeNetwork = connect.getNetworkCapabilities(internet) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            val network = connect.activeNetworkInfo ?: return false
            return network.isConnected
        }
    }

    private fun startWebViewActivity() {
        val intent = Intent(this, WebViewActivity::class.java)
        startActivity(intent)
    }

    private fun startGame() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }


    private fun needInternetForWorkApp() {
        val intent = Intent(this, NeedInternetActivity::class.java)
        startActivity(intent)
    }

    private fun getRemoteConfig() {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()

            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val result = remoteConfig.getString("link")
                    preferences.edit().putString(URL, result).apply()
                } else if (task.isCanceled) {
                    needInternetForWorkApp()
                }
            }
    }


//    private fun isEmulator(): Boolean {
//        if (BuildConfig.DEBUG) {
//            return false
//        }
//        val phoneModel = Build.MODEL
//        val buildProduct = Build.PRODUCT
//        val buildHardware = Build.HARDWARE
//        var result = (Build.FINGERPRINT.startsWith("generic")
//                || phoneModel.contains("google_sdk")
//                || phoneModel.lowercase(Locale.getDefault()).contains("droid4x")
//                || phoneModel.contains("Emulator")
//                || phoneModel.contains("Android SDK built for x86")
//                || Build.MANUFACTURER.contains("Genymotion")
//                || buildHardware == "goldfish"
//                || Build.BRAND.contains("google")
//                || buildHardware == "vbox86"
//                || buildProduct == "sdk"
//                || buildProduct == "google_sdk"
//                || buildProduct == "sdk_x86"
//                || buildProduct == "vbox86p"
//                || Build.BOARD.lowercase(Locale.getDefault()).contains("nox")
//                || Build.BOOTLOADER.lowercase(Locale.getDefault()).contains("nox")
//                || buildHardware.lowercase(Locale.getDefault()).contains("nox")
//                || buildProduct.lowercase(Locale.getDefault())
//            .contains("nox"))
//        if (result) {
//            return true
//        }
//        result = result or (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
//        if (result) return true
//        result = result or ("google_sdk" == buildProduct)
//        return result
//    }

    private fun initApp() {
        if (checkPreferencesLink() != "") {
            if (internet(this)) {
                startWebViewActivity()
            } else {
                needInternetForWorkApp()
            }
        } else {
            getRemoteConfig()
            if (preferences.getString(URL, "") == "" ) {
                startGame()
            } else {
                startWebViewActivity()
            }
        }
    }

    companion object {
        const val LINK = "link_from_firebase"
        const val URL = "url"
    }
}