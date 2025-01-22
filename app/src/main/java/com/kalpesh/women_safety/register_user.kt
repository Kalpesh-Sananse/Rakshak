package com.kalpesh.women_safety

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class register_user : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_user)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Get references to views

        val tvlogin = findViewById<TextView>(R.id.tvlogin)
        val etName = findViewById<EditText>(R.id.etName)
        val etContact = findViewById<EditText>(R.id.etContact)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        tvlogin.setOnClickListener{
            val intent = Intent(this, login_activity::class.java)
            startActivity(intent)
        }

        // Set click listener for the register button
        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val contact = etContact.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (name.isEmpty() || contact.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(name, contact, email, password)
            }
        }

    }
    private fun registerUser(name: String, contact: String, email: String, password: String) {
        // Create a user in Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid

                    // Create a user object
                    val user = User(name, contact, email)

                    // Save user details in Realtime Database
                    userId?.let {
                        database.getReference("Users").child(it).setValue(user)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                                 //   finish() // Close activity after successful registration
                                    val intent = Intent(this, MainActivityy::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this, "Database Error: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(this, "Auth Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


}
data class User(val name: String, val contact: String, val email: String){
}