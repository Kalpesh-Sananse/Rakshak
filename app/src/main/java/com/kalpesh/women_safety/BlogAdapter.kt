package com.kalpesh.women_safety



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class BlogAdapter(private val blogs: List<Blog>) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    class BlogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivBlogImage: ImageView = view.findViewById(R.id.ivBlogImage)
        val tvBlogTitle: TextView = view.findViewById(R.id.tvBlogTitle)
        val tvBlogBody: TextView = view.findViewById(R.id.tvBlogBody)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.blog_item, parent, false)
        return BlogViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blog = blogs[position]
        holder.tvBlogTitle.text = blog.title
        holder.tvBlogBody.text = blog.body
        Picasso.get().load(blog.imageUrl).into(holder.ivBlogImage)
    }

    override fun getItemCount(): Int = blogs.size
}
