package com.example.socialapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialapp.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth

class PostAdapter(options: FirestoreRecyclerOptions<Post>,val listener:InterfacePostAdapter):FirestoreRecyclerAdapter<Post, PostViewHolder>(options){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent , false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.postText.text = model.text
        holder.userNameText.text = model.createdBy.userName
        Glide.with(holder.itemView.context).load(model.createdBy.imageUrl).circleCrop().into(holder.userImage)
        holder.likeCount.text = model.likedby.size.toString()
        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)

        holder.likeButton.setOnClickListener {
            listener.onLikeClicked(snapshots.getSnapshot(holder.adapterPosition).id)
        }

        val auth = FirebaseAuth.getInstance()
        val currentUserId = auth.currentUser!!.uid

        val isLiked = model.likedby.contains(currentUserId)
        if(isLiked){
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context , R.drawable.ic_liked))
        } else{
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context , R.drawable.ic_unliked))
        }
    }

}

class PostViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
    val postText : TextView = itemView.findViewById(R.id.postTitle)
    val userNameText : TextView = itemView.findViewById(R.id.userName)
    val createdAt : TextView = itemView.findViewById(R.id.createdAtTime)
    val likeCount : TextView = itemView.findViewById(R.id.likeCount)
    val userImage : ImageView = itemView.findViewById(R.id.userProfileImage)
    val likeButton : ImageView = itemView.findViewById(R.id.likeImageButton)
}

interface InterfacePostAdapter{
    fun onLikeClicked(postId:String)
}