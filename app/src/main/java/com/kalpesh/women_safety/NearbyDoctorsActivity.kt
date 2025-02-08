package com.kalpesh.women_safety

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NearbyDoctorsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_doctors)

        val userLatitude = intent.getDoubleExtra("userLatitude", 0.0)
        val userLongitude = intent.getDoubleExtra("userLongitude", 0.0)
        val doctorsList = intent.getSerializableExtra("doctorsList") as ArrayList<Doctor>

        // Show doctors on a custom map-like interface (using RecyclerView or canvas)
    }
}