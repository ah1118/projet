package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import org.json.JSONObject
import java.io.File

class NObHomeActivity : AppCompatActivity() {

    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Load HomeFragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NObHomeFragment())
                .commit()
        }

        // Setup BottomNavigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        NOeBottomMenuHelper(this).setupBottomNavigation(bottomNav, R.id.nav_home)

        // Initialize Remote Config
        remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(1)
                .build()
        )

        // Fetch lesson URL and store locally, then check version
        fetchLessonUrlAndUpdateLessons()
    }

    /** Fetch lesson_url from Remote Config and store in SharedPreferences */
    private fun fetchLessonUrlAndUpdateLessons() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // Optional default to avoid empty URL
        remoteConfig.setDefaultsAsync(mapOf("lesson_url" to ""))

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val lessonUrl = remoteConfig.getString("lessonUrl")
                prefs.edit().putString("lesson_url", lessonUrl).apply()
                Log.d("DEBUG", "Fetched lesson URL: $lessonUrl")

                // Now check version and fetch lessons if needed
                checkAndUpdateVersionIfNeeded()
            } else {
                Log.e("DEBUG", "Remote Config fetch failed")
            }
        }
    }

    /** Check local lesson version vs remote version */
    private fun checkAndUpdateVersionIfNeeded() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val localVersion = prefs.getInt("lesson_version", -1)

        val remoteVersion = remoteConfig.getLong("version").toInt() // match your key exactly

        if (localVersion == -1 || localVersion != remoteVersion) {
            // Version missing or different → fetch new lessons
            fetchNewLessons(
                onSuccess = { json ->
                    File(filesDir, "lessons.json")
                        .writeText(json.toString(), Charsets.UTF_8)
                    prefs.edit().putInt("lesson_version", remoteVersion).apply()
                    Log.d("DEBUG", "Fetched lessons and updated version to $remoteVersion")
                },
                onError = { e -> Log.e("DEBUG", "Fetch failed: ${e.message}") }
            )
        } else {
            Log.d("DEBUG", "Local version is up-to-date: $localVersion")
        }
    }

    /** Returns lesson URL stored in SharedPreferences */
    fun getBaseUrl(): String {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return prefs.getString("lesson_url", "") ?: ""
    }

    /** Fetch lessons from the server */
    fun fetchNewLessons(
        onSuccess: (JSONObject) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val baseUrl = getBaseUrl()
        if (baseUrl.isEmpty()) {
            onError(Exception("Base URL is empty"))
            return
        }

        val url = "$baseUrl/mainlesson"
        val queue = Volley.newRequestQueue(this)

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val json = JSONObject(response)
                    onSuccess(json)
                } catch (e: Exception) {
                    onError(e)
                }
            },
            { error -> onError(Exception(error)) }
        )

        queue.add(request)
    }
}
