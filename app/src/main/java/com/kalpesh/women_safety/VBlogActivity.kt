package com.kalpesh.women_safety

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class VBlogActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
   // private lateinit var videoAdapter: VideoAdapter
    private val videoList = mutableListOf<Videos>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vblog)
//<<<<<<< HEAD
//=======
//
//>>>>>>> a0e03c72e5d506f03f37cd0b2f8c43f5e294d143
    }

    private fun fetchVideosFromFirebase() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("videos")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                videoList.clear()
                for (data in snapshot.children) {
                    val video = data.getValue(Videos::class.java)
                    if (video != null) {
                        videoList.add(video)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VBlogActivity, "Failed to load videos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openVideo(videoUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(videoUrl)
        startActivity(intent)
    }
}
