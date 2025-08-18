package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings


class NOaLoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        auth = FirebaseAuth.getInstance()

        // Init Remote Config
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0) // Always fetch fresh in dev
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val btnGoToSignUp = view.findViewById<Button>(R.id.btnGoToSignUp)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                        fetchLessonUrlFromRemoteConfig()
                    } else {
                        Toast.makeText(requireContext(), "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        Log.e("LoginFragment", "Login failed", task.exception)
                    }
                }
        }

        btnGoToSignUp.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NOaSignUpFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun fetchLessonUrlFromRemoteConfig() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val lessonUrl = remoteConfig.getString("lessonUrl")
                    Log.d("LoginFragment", "Lesson URL from Remote Config: $lessonUrl")

                    // Save directly to SharedPreferences
                    val prefs = requireContext().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                    prefs.edit().putString("lesson_url", lessonUrl).apply()

                    // Optional: still pass to MainActivity if needed
                    (activity as? NOaMainActivity)?.lessonUrl = lessonUrl

                    // Navigate to Home
                    (activity as? NOaMainActivity)?.reloadHomeAfterLogin()
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch lesson URL", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
