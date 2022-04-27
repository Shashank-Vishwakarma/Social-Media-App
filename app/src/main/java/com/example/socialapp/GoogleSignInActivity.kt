package com.example.socialapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.socialapp.daos.UserDao
import com.example.socialapp.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class GoogleSignInActivity : AppCompatActivity() {

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInButton : SignInButton
    private lateinit var progressBar : ProgressBar
    companion object{
        private const val RC_SIGN_IN = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_sign_in)
        progressBar = findViewById(R.id.progressBar)

        // initialize the firebase authentication
        firebaseAuth = FirebaseAuth.getInstance()

        // configure the google sign in
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        // set the click listener on the sign in button
        googleSignInButton = findViewById(R.id.googleSignInButton)
        googleSignInButton.setOnClickListener {
            signIn()
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = firebaseAuth.currentUser
        updateUI(currentUser)
    }

    // This updates the UI
    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser!=null){

            val user = User(currentUser.uid,currentUser.displayName,currentUser.photoUrl.toString())
            val userDao = UserDao()
            userDao.addUser(user)

            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        } else{
            progressBar.visibility = View.GONE
            googleSignInButton.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode== RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if(task.isSuccessful){
                try{
                    val account = task.getResult(ApiException::class.java)
                    Log.d("TAG","firebaseAuthWithGoogle : "+account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e:Exception){
                    Log.d("TAG","signInResult: failed code"+e.message.toString())
                }
            } else{
                Log.d("TAG","task failed")
            }
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent,RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken,null)

        progressBar.visibility = View.VISIBLE
        googleSignInButton.visibility = View.GONE

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    val currentUser = firebaseAuth.currentUser
                    updateUI(currentUser)
                } else{
                    Toast.makeText(this,"google sign in failed",Toast.LENGTH_SHORT).show()
                }
            }
    }

}