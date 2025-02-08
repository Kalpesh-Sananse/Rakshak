package com.kalpesh.women_safety

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.location.*

class HealthParamterActivity: AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_paramter)

        database = FirebaseDatabase.getInstance().reference.child("sos_alerts")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val oxygenLevelInput = findViewById<EditText>(R.id.oxygenLevelInput)
        val heartRateInput = findViewById<EditText>(R.id.heartRateInput)
        val bloodPressureInput = findViewById<EditText>(R.id.bloodPressureInput)
        val submitButton = findViewById<Button>(R.id.submitButton)

        submitButton.setOnClickListener {
            val oxygenLevel = oxygenLevelInput.text.toString().toIntOrNull()
            val heartRate = heartRateInput.text.toString().toIntOrNull()
            val bloodPressure = bloodPressureInput.text.toString().toIntOrNull()

            if (oxygenLevel != null && heartRate != null && bloodPressure != null) {
                detectSOSCondition(oxygenLevel, heartRate, bloodPressure)
            } else {
                showToast("Please enter valid numeric values")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun detectSOSCondition(oxygenLevel: Int, heartRate: Int, bloodPressure: Int) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val isSOS = advancedSOSAlgorithm(oxygenLevel, heartRate, bloodPressure)
                if (isSOS) {
                    sendSOS(location.latitude, location.longitude)
                } else {
                    showToast("Health parameters are within normal range")
                }
            } else {
                showToast("Failed to get current location. Try again.")
            }
        }.addOnFailureListener {
            showToast("Location error: ${it.message}")
        }
    }

    private fun advancedSOSAlgorithm(oxygenLevel: Int, heartRate: Int, bloodPressure: Int): Boolean {
        // **Advanced SOS Algorithm**
        return when {
            oxygenLevel < 85 -> true // Dangerously low oxygen
            heartRate > 130 || heartRate < 45 -> true // Abnormal heart rate
            bloodPressure > 160 || bloodPressure < 50 -> true // Critical BP
            (oxygenLevel < 90 && heartRate > 120) -> true // Oxygen & Heart rate combined risk
            (oxygenLevel < 88 && bloodPressure > 150) -> true // Oxygen & BP combined risk
            else -> false
        }
    }

    private fun sendSOS(latitude: Double, longitude: Double) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            showToast("User not authenticated. Please log in.")
            return
        }

        val sosEntry = database.push()
        val sosData = mapOf(
            "latitude" to latitude,
            "longitude" to longitude,
            "userId" to userId
        )

        sosEntry.setValue(sosData)
            .addOnSuccessListener { showToast("🚨 SOS Alert Sent Successfully!") }
            .addOnFailureListener { showToast("❌ Failed to send SOS Alert!") }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
