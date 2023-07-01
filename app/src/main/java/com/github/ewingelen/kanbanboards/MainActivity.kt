package com.github.ewingelen.kanbanboards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val REQ_ONE_TAP = 2
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.oauth_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, options)

        auth = Firebase.auth

        findViewById<SignInButton>(R.id.loginButton).setOnClickListener {
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, REQ_ONE_TAP)
        }

        if (auth.currentUser != null) {
            startActivity(Intent(this, SecondActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_ONE_TAP) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                when {
                    idToken != null -> {
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(firebaseCredential)
                            .addOnSuccessListener {
                                val user = auth.currentUser
                                updateUI(user)
                                val uid = user!!.uid
                                val email = user.email
                                if (it.additionalUserInfo?.isNewUser == true) {

                                } else {

                                }
                                startActivity(Intent(this, SecondActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                updateUI(null)
                            }
                    }

                    else -> {}
                }
            } catch (e: ApiException) {
            }
        }
    }

    private fun updateUI(user: Any?) {}
}