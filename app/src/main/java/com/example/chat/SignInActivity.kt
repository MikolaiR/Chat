package com.example.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var repeatPasswordEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var loginSignUpButton: Button
    private lateinit var toggleLoginSignUpTextView: TextView

    private lateinit var database: FirebaseDatabase
    private lateinit var usersDatabaseReference: DatabaseReference
    private var loginModeAction: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        database = Firebase.database
        usersDatabaseReference = database.getReference("users")

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        }

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText)
        nameEditText = findViewById(R.id.nameEditText)
        loginSignUpButton = findViewById(R.id.loginSignUpButton)
        toggleLoginSignUpTextView = findViewById(R.id.toggleLoginSignUpTextView)

        loginSignUpButton.setOnClickListener {
            loginSignUpUser(
                emailEditText.text.toString().trim(),
                passwordEditText.text.toString().trim()
            )
        }

    }

    private fun loginSignUpUser(email: String, password: String) {
        if (loginModeAction) {
            when {
                passwordEditText.text.toString().trim().length < 7 -> {
                    Toast.makeText(
                        this,
                        "Password must be at least 7 character",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                emailEditText.text.toString().trim() == "" -> {
                    Toast.makeText(this, "Please input your email", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success")
                                val user = auth.currentUser
                                // todo    updateUI(user)
                                startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.exception)
                                Toast.makeText(
                                    baseContext, "Authentication failed.",
                                    Toast.LENGTH_LONG
                                ).show()
                                //  todo    updateUI(null)
                                // ...
                            }
                            // ...
                        }
                }
            }
        } else {
            when {
                emailEditText.text.toString().trim() == "" -> {
                    Toast.makeText(this, "Please input your email", Toast.LENGTH_SHORT).show()
                }
                passwordEditText.text.toString().trim() != repeatPasswordEditText.text.toString()
                    .trim() -> {
                    Toast.makeText(this, "Password don't mathch", Toast.LENGTH_SHORT).show()
                }
                passwordEditText.text.toString().trim().length < 7 -> {
                    Toast.makeText(
                        this,
                        "Password must be at least 7 character",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                nameEditText.text.toString().trim() == "" -> {
                    Toast.makeText(this, "Please input your name", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success")
                                val user = auth.currentUser
                                createUser(user!!)
                                //todo  updateUI(user)
                                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                                intent.putExtra(MainActivity.USER_NAME, nameEditText.text.toString().trim())
                                startActivity(intent)
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(
                                    baseContext, "Authentication failed.",
                                    Toast.LENGTH_LONG
                                ).show()
                                //todo updateUI(null)
                            }

                            // ...
                        }
                }
            }

        }
    }

    private fun createUser(firebaseUser: FirebaseUser) {
        val user = User(nameEditText.text.toString().trim(), firebaseUser.email!!, firebaseUser.uid)
        usersDatabaseReference.push().setValue(user)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
    }

    companion object {
        const val TAG = "SignInActivity"
    }

    fun toggleLoginMode(view: View) {
        if (loginModeAction) {
            loginModeAction = false
            loginSignUpButton.text = "Registration"
            toggleLoginSignUpTextView.text = "Or, log in"
            repeatPasswordEditText.visibility = View.VISIBLE
            nameEditText.visibility = View.VISIBLE
        } else {
            loginModeAction = true
            loginSignUpButton.text = "Log In"
            toggleLoginSignUpTextView.text = "Registration"
            repeatPasswordEditText.visibility = View.GONE
            nameEditText.visibility = View.GONE
        }
    }
}