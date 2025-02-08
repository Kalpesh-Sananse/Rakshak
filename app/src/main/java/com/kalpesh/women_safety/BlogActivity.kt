package com.kalpesh.women_safety


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.util.*

class BlogActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var blogAdapter: BlogAdapter
    private val blogs = mutableListOf<Blog>()

    private val firebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog)

        recyclerView = findViewById(R.id.recyclerViewBlogs)
        recyclerView.layoutManager = LinearLayoutManager(this)

        blogAdapter = BlogAdapter(blogs)
        recyclerView.adapter = blogAdapter

        retrieveBlogs()
        setupBlogNotificationListener()
    }

    private fun retrieveBlogs() {
        val blogRef = firebaseDatabase.reference.child("blogs")
        blogRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                blogs.clear()
                for (blogSnapshot in snapshot.children) {
                    val blog = blogSnapshot.getValue(Blog::class.java)
                    blog?.let { blogs.add(it) }
                }
                blogAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@BlogActivity, "Failed to load blogs: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupBlogNotificationListener() {
        val blogRef = firebaseDatabase.reference.child("blogs")
        blogRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val newBlog = snapshot.getValue(Blog::class.java)
                newBlog?.let { sendNotification(it.title) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun sendNotification(blogTitle: String) {
        val channelId = "blog_notifications"
        val channelName = "Blog Updates"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("New Blog Added")
            .setContentText(blogTitle)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random().nextInt(), notification)
    }
}
