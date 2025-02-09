package com.kalpesh.women_safety

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ChatbotActivity : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        initializeViews()
        setupRecyclerView()
        setupClickListeners()

        // Add initial context message
        addMessage(ChatMessage(
            "Hi! I'm your AI assistant based in Pune Katraj. How can I help you today?",
            isSentByUser = false
        ))
    }

    private fun initializeViews() {
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messages)
        chatRecyclerView.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@ChatbotActivity)
        }
    }

    private fun setupClickListeners() {
        sendButton.setOnClickListener {
            sendMessage()
        }

        messageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }
    }

    private fun sendMessage() {
        val messageText = messageInput.text.toString().trim()
        if (messageText.isEmpty()) return

        // Add user message to chat
        addMessage(ChatMessage(messageText, true))
        messageInput.text.clear()

        // Send to API
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = sendToGeminiAPI(messageText)
                withContext(Dispatchers.Main) {
                    addMessage(ChatMessage(response, false))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ChatbotActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun addMessage(message: ChatMessage) {
        messages.add(message)
        chatAdapter.notifyItemInserted(messages.size - 1)
        chatRecyclerView.scrollToPosition(messages.size - 1)
    }

    private fun sendToGeminiAPI(prompt: String): String {
        val puneContext = "Based on the location being Pune Katraj, "
        val fullPrompt = "$puneContext$prompt"

        val json = JSONObject().apply {
            put("prompt", fullPrompt)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://gemini-api-t0o9.onrender.com/api/generate") // Using 10.0.2.2 for Android emulator to access localhost
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("API call failed: ${response.code}")
            }

            val responseJson = JSONObject(response.body?.string() ?: "")
            return responseJson.getString("response")
        }
    }
}

// Data class for chat messages
data class ChatMessage(
    val text: String,
    val isSentByUser: Boolean
)