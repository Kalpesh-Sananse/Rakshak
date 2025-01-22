package com.kalpesh.women_safety

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivityy : AppCompatActivity() {

    private lateinit var btnManualSOS: Button
    private lateinit var btnToggleVoiceSOS: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var speechRecognizer: SpeechRecognizer
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var isVoiceSOSActive = false
    private val predefinedWords = listOf("help", "help me", "danger", "bacchao muze")

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnManualSOS = findViewById(R.id.btnManualSOS)
        btnToggleVoiceSOS = findViewById(R.id.toggleVoiceSOS)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!hasPermissions()) {
            requestPermissions()
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        btnManualSOS.setOnClickListener {
            if (hasPermissions()) {
                triggerManualSOS()
            } else {
                Toast.makeText(this, "Please grant all permissions to use this feature.", Toast.LENGTH_SHORT).show()
            }
        }

        btnToggleVoiceSOS.setOnClickListener {
            if (hasPermissions()) {
                toggleVoiceSOS()
            } else {
                Toast.makeText(this, "Please grant all permissions to use this feature.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hasPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            REQUEST_CODE_PERMISSIONS
        )
    }

    @SuppressLint("MissingPermission")
    private fun triggerManualSOS() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                sendSOSAlert(location)
            } else {
                Toast.makeText(this, "Unable to fetch location. Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleVoiceSOS() {
        isVoiceSOSActive = !isVoiceSOSActive
        if (isVoiceSOSActive) {
            btnToggleVoiceSOS.text = "Disable Voice SOS"
            startVoiceRecognition()
        } else {
            btnToggleVoiceSOS.text = "Enable Voice SOS"
            speechRecognizer.stopListening()
            Toast.makeText(this, "Voice SOS Disabled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Toast.makeText(this@MainActivityy, "Listening for SOS commands...", Toast.LENGTH_SHORT).show()
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                Toast.makeText(this@MainActivityy, "Error occurred: $error", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null) {
                    for (match in matches) {
                        if (predefinedWords.contains(match.lowercase())) {
                            triggerVoiceSOS(match)
                            return
                        }
                    }
                }
                Toast.makeText(this@MainActivityy, "No SOS word detected", Toast.LENGTH_SHORT).show()
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer.startListening(intent)
    }

    private fun triggerVoiceSOS(triggeredWord: String) {
        Toast.makeText(this, "Voice SOS Triggered by word: $triggeredWord", Toast.LENGTH_LONG).show()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                sendSOSAlert(location)
            } else {
                Toast.makeText(this, "Unable to fetch location for Voice SOS.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendSOSAlert(location: Location) {
        val userId = auth.currentUser?.uid ?: return
        val sosData = mapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "timestamp" to System.currentTimeMillis(),
            "userId" to userId
        )

        database.reference.child("sos_alerts").push().setValue(sosData)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "🚨 SOS Alert Sent!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send SOS alert.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
}
