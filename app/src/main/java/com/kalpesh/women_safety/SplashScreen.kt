package com.kalpesh.women_safety

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        // Delay for 3 seconds before navigating to the HomePage activity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashScreen, register_user::class.java)
            startActivity(intent)
            finish() // Close the splash screen activity
        }, 3000) // 3000 milliseconds (3 seconds)
    }
}