package com.example.socialapp.daos

import com.example.socialapp.models.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {
    val db = FirebaseFirestore.getInstance()
    val postCollection = db.collection("posts")
    val auth = FirebaseAuth.getInstance()

    fun addPost(text:String){
        val currentUserId = auth.currentUser!!.uid
        GlobalScope.launch(Dispatchers.IO) {
            val userDao = UserDao()
            val user = userDao.getUserById(currentUserId).await().toObject(com.example.socialapp.models.User::class.java)!!

            val currentTime = System.currentTimeMillis()
            val post = Post(text,user,currentTime)
            postCollection.document().set(post)
        }
    }

    fun getPostById(postId:String):Task<DocumentSnapshot>{
        return postCollection.document(postId).get()
    }

    fun updateLikes(postId:String){
        val currentUserId = auth.currentUser!!.uid
        GlobalScope.launch(Dispatchers.IO) {
            val post = getPostById(postId).await().toObject(Post::class.java)
            val isLiked = post!!.likedby.contains(currentUserId)

            if(isLiked){
                post.likedby.remove(currentUserId)
            } else{
                post.likedby.add(currentUserId)
            }

            postCollection.document(postId).set(post)
        }
    }
}