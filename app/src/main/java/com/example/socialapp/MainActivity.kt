package com.example.socialapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialapp.daos.PostDao
import com.example.socialapp.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , InterfacePostAdapter{
    private lateinit var fab:FloatingActionButton
    private lateinit var adapter : PostAdapter
    private lateinit var recyclerView :RecyclerView
    private lateinit var postDao : PostDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)

        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity,CreatePostActivity::class.java)
            startActivity(intent)
        }

       setUpRecyclerView()
    }

        private fun setUpRecyclerView() {
            postDao = PostDao()
            val postsCollections = postDao.postCollection
            val query = postsCollections.orderBy("createdAt", Query.Direction.DESCENDING)
            val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

            recyclerView.layoutManager = LinearLayoutManager(this)
            adapter = PostAdapter(recyclerViewOptions , this)
            recyclerView.adapter = adapter
        }

        override fun onStart() {
            super.onStart()
            adapter.startListening()
        }

        override fun onStop() {
            super.onStop()
            adapter.stopListening()
        }

    override fun onLikeClicked(postId: String) {
        postDao.updateLikes(postId)

    }
}