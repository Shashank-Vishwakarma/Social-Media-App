package com.example.socialapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.socialapp.daos.PostDao

class CreatePostActivity : AppCompatActivity() {
    private lateinit var postButton: Button
    private lateinit var postEditText: EditText
    private val postDao: PostDao = PostDao()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        postEditText = findViewById(R.id.postBox)
        postButton = findViewById(R.id.postButton)
        postButton.setOnClickListener {
            val postInput = postEditText.text.toString().trim()
            if(postInput.isNotEmpty()){
                postDao.addPost(postInput)
                finish()
            }
        }
    }
}