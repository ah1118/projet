package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class NOaMainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    var lessonUrl: String? = null  // <-- add this property here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (auth.currentUser == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NOaLoginFragment())
                .commit()
        } else {
            reloadHomeAfterLogin()
        }
    }

    fun reloadHomeAfterLogin() {
        val intent = Intent(this, NObHomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
