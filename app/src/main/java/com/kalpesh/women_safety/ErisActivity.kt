package com.kalpesh.women_safety

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs

class ErisActivity : AppCompatActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var viewFinder: PreviewView
    private lateinit var gestureStatus: TextView
    private lateinit var calibrateButton: Button
    private lateinit var toggleDetectionButton: Button
    private lateinit var instructionsText: TextView

    // Detection state
    private var isCalibrating = false
    private var isDetecting = false
    private var calibrationBlinkPattern = mutableListOf<Long>()
    private var currentBlinkPattern = mutableListOf<Long>()
    private var lastBlinkTime: Long = 0
    private var blinkCount = 0

    // Emergency contact
    private val emergencyContact = "YOUR_EMERGENCY_CONTACT_NUMBER" // Replace with actual number

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eris)

        initializeViews()
        setupButtons()

        if (!allPermissionsGranted()) {
            requestPermissions()
        } else {
            startCamera()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun initializeViews() {
        viewFinder = findViewById(R.id.viewFinder)
        gestureStatus = findViewById(R.id.gestureStatus)
        calibrateButton = findViewById(R.id.calibrateButton)
        toggleDetectionButton = findViewById(R.id.toggleDetectionButton)
        instructionsText = findViewById(R.id.instructionsText)
    }

    private fun setupButtons() {
        calibrateButton.setOnClickListener {
            startCalibration()
        }

        toggleDetectionButton.setOnClickListener {
            if (calibrationBlinkPattern.isEmpty()) {
                Toast.makeText(this, "Please calibrate first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            toggleDetection()
        }
    }

    private fun startCalibration() {
        isCalibrating = true
        isDetecting = false
        calibrationBlinkPattern.clear()
        blinkCount = 0
        gestureStatus.text = "Calibrating: Blink 3 times..."
        instructionsText.text = "Blink naturally 3 times"
        toggleDetectionButton.text = "Start Detection"
    }

    private fun toggleDetection() {
        isDetecting = !isDetecting
        isCalibrating = false
        toggleDetectionButton.text = if (isDetecting) "Stop Detection" else "Start Detection"
        gestureStatus.text = if (isDetecting) "Detection Active" else "Detection Stopped"
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(viewFinder.surfaceProvider)
                    }

                // Image Analysis
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, setupFaceAnalyzer())
                    }

                // Select front camera
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )

                Log.d(TAG, "Camera started successfully")

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
                Toast.makeText(this, "Failed to open camera: ${exc.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    private fun setupFaceAnalyzer(): ImageAnalysis.Analyzer {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val detector = FaceDetection.getClient(options)

        return ImageAnalysis.Analyzer { imageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                detector.process(image)
                    .addOnSuccessListener { faces ->
                        if (faces.isNotEmpty()) {
                            val face = faces[0]
                            val leftEyeOpenProb = face.leftEyeOpenProbability ?: 0f
                            val rightEyeOpenProb = face.rightEyeOpenProbability ?: 0f

                            // Detect blink when both eyes are mostly closed
                            if (leftEyeOpenProb < 0.1 && rightEyeOpenProb < 0.1) {
                                runOnUiThread {
                                    handleBlink()
                                }
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Face detection failed", e)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }
    }

    private fun handleBlink() {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastBlinkTime > 500) { // Prevent multiple detections for same blink
            if (isCalibrating) {
                handleCalibrationBlink(currentTime)
            } else if (isDetecting) {
                handleDetectionBlink(currentTime)
            }
            lastBlinkTime = currentTime
        }
    }

    private fun handleCalibrationBlink(currentTime: Long) {
        if (blinkCount > 0) {
            calibrationBlinkPattern.add(currentTime - lastBlinkTime)
        }
        blinkCount++

        if (blinkCount >= 3) {
            isCalibrating = false
            gestureStatus.text = "Calibration Complete!"
            instructionsText.text = "Pattern stored. Tap 'Start Detection' to begin monitoring"
        } else {
            gestureStatus.text = "Calibrating: ${3 - blinkCount} blinks remaining..."
        }
    }

    private fun handleDetectionBlink(currentTime: Long) {
        currentBlinkPattern.add(currentTime - lastBlinkTime)

        // Keep only the last 2 intervals (3 blinks)
        if (currentBlinkPattern.size > 2) {
            currentBlinkPattern.removeAt(0)
        }

        // Check if pattern matches calibration
        if (currentBlinkPattern.size == 2 && patternsMatch()) {
            sendSOS()
            currentBlinkPattern.clear()
        }
    }

    private fun patternsMatch(): Boolean {
        if (calibrationBlinkPattern.size < 2) return false

        // Compare intervals between blinks
        for (i in currentBlinkPattern.indices) {
            val calibrationInterval = calibrationBlinkPattern[i]
            val currentInterval = currentBlinkPattern[i]

            // Allow 50% tolerance in timing
            if (abs(calibrationInterval - currentInterval) > calibrationInterval * 0.5) {
                return false
            }
        }
        return true
    }

    private fun sendSOS() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(
                    emergencyContact,
                    null,
                    "SOS ALERT: Emergency assistance needed",
                    null,
                    null
                )
                Toast.makeText(this, "SOS Alert Sent!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to send SOS: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SEND_SMS),
                SMS_PERMISSION_CODE
            )
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "SOSDetectionActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val SMS_PERMISSION_CODE = 11
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.SEND_SMS
        )
    }
}