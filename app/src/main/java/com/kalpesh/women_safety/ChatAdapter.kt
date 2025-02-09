package com.kalpesh.women_safety
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    private val VIEW_TYPE_USER = 1
    private val VIEW_TYPE_BOT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layout = when (viewType) {
            VIEW_TYPE_USER -> R.layout.item_message_user
            else -> R.layout.item_message_bot
        }

        val view = LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.messageText.text = message.text
    }

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isSentByUser) VIEW_TYPE_USER else VIEW_TYPE_BOT
    }

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.messageText)
    }
}