package com.kalpesh.women_safety

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NearbyDoctors : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var doctorAdapter: DoctorAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val doctorList = mutableListOf<Doctor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_doctors)

        recyclerView = findViewById(R.id.recyclerViewDoctors)
        recyclerView.layoutManager = LinearLayoutManager(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                100
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                fetchDoctorsFromFirebase(location)
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get location: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchDoctorsFromFirebase(userLocation: Location) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("emergencyusers")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                doctorList.clear()
                for (data in snapshot.children) {
                    val position = data.child("position").getValue(String::class.java)
                    if (position == "Doctor") {
                        val name = data.child("name").getValue(String::class.java) ?: "Unknown"
                        val latitude = data.child("latitude").getValue(Double::class.java) ?: 0.0
                        val longitude = data.child("longitude").getValue(Double::class.java) ?: 0.0

                        val doctorLocation = Location("").apply {
                            this.latitude = latitude
                            this.longitude = longitude
                        }
                        val distance = userLocation.distanceTo(doctorLocation) / 1000.0

                        doctorList.add(Doctor(name, latitude, longitude, distance))
                    }
                }
                doctorList.sortBy { it.distance }
                doctorAdapter = DoctorAdapter(doctorList) { doctor ->
                    openMap(doctor)
                }
                recyclerView.adapter = doctorAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@NearbyDoctors, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openMap(doctor: Doctor) {
        val uri = Uri.parse("geo:${doctor.latitude},${doctor.longitude}?q=${doctor.latitude},${doctor.longitude}(${doctor.name})")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}
