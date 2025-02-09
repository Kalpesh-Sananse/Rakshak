package com.kalpesh.women_safety

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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

class ErisActivity : AppCompatActivity() {
    private lateinit var viewFinder: PreviewView
    private lateinit var gestureStatus: TextView
    private lateinit var calibrateButton: Button
    private lateinit var toggleDetectionButton: Button
    private lateinit var instructionsText: TextView
    private lateinit var cameraExecutor: ExecutorService

    private var isCalibrating = false
    private var isDetecting = false
    private var calibrationBlinkPattern = mutableListOf<Long>()
    private var currentBlinkPattern = mutableListOf<Long>()
    private var lastBlinkTime: Long = 0
    private var blinkCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eris)

        initializeViews()
        setupButtons()
        checkPermissions()

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
            toggleDetection()
        }
    }

    private fun checkPermissions() {
        val requiredPermissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.SEND_SMS,
            Manifest.permission.VIBRATE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requiredPermissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest, PERMISSION_REQUEST_CODE)
        } else {
            startCamera()
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

    private fun startBackgroundDetection() {
        val serviceIntent = Intent(this, SOSDetectionService::class.java).apply {
            action = "START_DETECTION"
            putExtra("calibrationPattern", calibrationBlinkPattern.toLongArray())
        }

        requestBatteryOptimizationExemption()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        Toast.makeText(this, "Background detection started", Toast.LENGTH_SHORT).show()
    }

    private fun stopBackgroundDetection() {
        val serviceIntent = Intent(this, SOSDetectionService::class.java).apply {
            action = "STOP_DETECTION"
        }
        stopService(serviceIntent)
        Toast.makeText(this, "Detection stopped", Toast.LENGTH_SHORT).show()
    }

    private fun toggleDetection() {
        isDetecting = !isDetecting
        if (isDetecting) {
            if (calibrationBlinkPattern.isEmpty()) {
                Toast.makeText(this, "Please calibrate first", Toast.LENGTH_SHORT).show()
                isDetecting = false
                return
            }
            startBackgroundDetection()
        } else {
            stopBackgroundDetection()
        }
        toggleDetectionButton.text = if (isDetecting) "Stop Detection" else "Start Detection"
        gestureStatus.text = if (isDetecting) "Detection Active" else "Detection Stopped"
    }

    private fun requestBatteryOptimizationExemption() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val packageName = packageName
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent().apply {
                    action = android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    data = android.net.Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }
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

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )
                } catch (exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                    Toast.makeText(this, "Camera initialization failed", Toast.LENGTH_SHORT).show()
                }

            } catch (exc: Exception) {
                Log.e(TAG, "Camera initialization failed", exc)
                Toast.makeText(this, "Camera initialization failed", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Calibration Complete!", Toast.LENGTH_SHORT).show()
            gestureStatus.text = "Calibration Complete!"
            instructionsText.text = "Pattern stored. Tap 'Start Detection' to begin monitoring"
        } else {
            gestureStatus.text = "Calibrating: ${3 - blinkCount} blinks remaining..."
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions required for app functionality", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "ErisActivity"
        private const val PERMISSION_REQUEST_CODE = 123
    }
}