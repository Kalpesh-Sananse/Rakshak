package com.kalpesh.women_safety
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class profileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etNumber: EditText
    private lateinit var etAddress: EditText
    private lateinit var etTaluka: EditText
    private lateinit var etDistrict: EditText
    private lateinit var etState: EditText
    private lateinit var etEmergency1: EditText
    private lateinit var etEmergency2: EditText
    private lateinit var btnSaveUpdate: Button
    private lateinit var database: DatabaseReference
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize views
        etName = findViewById(R.id.et_name)
        etNumber = findViewById(R.id.et_number)
        etAddress = findViewById(R.id.et_address)
        etTaluka = findViewById(R.id.et_taluka)
        etDistrict = findViewById(R.id.et_district)
        etState = findViewById(R.id.et_state)
        etEmergency1 = findViewById(R.id.et_emergency1)
        etEmergency2 = findViewById(R.id.et_emergency2)
        btnSaveUpdate = findViewById(R.id.btn_save_update)

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("Users")

        // Check if user exists
        checkUserExists()

        // Save/Update information
        btnSaveUpdate.setOnClickListener {
            saveOrUpdateInfo()
        }
    }

    private fun checkUserExists() {
        database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Populate fields with existing data
                    val userInfo = snapshot.getValue(UserInfo::class.java)
                    userInfo?.let {
                        etName.setText(it.name)
                        etNumber.setText(it.number)
                        etAddress.setText(it.address)
                        etTaluka.setText(it.taluka)
                        etDistrict.setText(it.district)
                        etState.setText(it.state)
                        etEmergency1.setText(it.emergencyContact1)
                        etEmergency2.setText(it.emergencyContact2)
                    }
                    btnSaveUpdate.text = "Update Information"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@profileActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveOrUpdateInfo() {
        val userInfo = UserInfo(
            name = etName.text.toString(),
            number = etNumber.text.toString(),
            address = etAddress.text.toString(),
            taluka = etTaluka.text.toString(),
            district = etDistrict.text.toString(),
            state = etState.text.toString(),
            emergencyContact1 = etEmergency1.text.toString(),
            emergencyContact2 = etEmergency2.text.toString()
        )

        database.child(userId).setValue(userInfo).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Information saved/updated successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to save information.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// Data class for user information
data class UserInfo(
    val name: String = "",
    val number: String = "",
    val address: String = "",
    val taluka: String = "",
    val district: String = "",
    val state: String = "",
    val emergencyContact1: String = "",
    val emergencyContact2: String = ""
)
