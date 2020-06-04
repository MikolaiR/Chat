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


class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var repeatPasswordEditText:EditText
    private lateinit var nameEditText:EditText
    private lateinit var loginSignUpButton: Button
    private lateinit var toggleLoginSignUpTextView:TextView

    private  var loginModeAction:Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText)
        nameEditText = findViewById(R.id.nameEditText)
        loginSignUpButton = findViewById(R.id.loginSignUpButton)
        toggleLoginSignUpTextView = findViewById(R.id.toggleLoginSignUpTextView)

        loginSignUpButton.setOnClickListener {
            loginSignUpUser(emailEditText.text.toString().trim(),passwordEditText.text.toString().trim())
        }

    }

    private fun loginSignUpUser(email:String,password:String) {
        if (loginModeAction){
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                   // todo    updateUI(user)
                        startActivity(Intent(this@SignInActivity,MainActivity::class.java))
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                  //  todo    updateUI(null)
                        // ...
                    }

                    // ...
                }
        }else{
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        //todo  updateUI(user)
                        startActivity(Intent(this@SignInActivity,MainActivity::class.java))
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_LONG).show()
                        //todo updateUI(null)
                    }

                    // ...
                }
        }

        startActivity(Intent(this@SignInActivity,MainActivity::class.java))
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
    }
    companion object{
        const val TAG = "SignInActivity"
    }

    fun toggleLoginMode(view: View) {
        if (loginModeAction){
            loginModeAction = false
            loginSignUpButton.text = "Sign Up"
            toggleLoginSignUpTextView.text = "Or, log in"
            repeatPasswordEditText.visibility = View.VISIBLE
        }else{
            loginModeAction = true
            loginSignUpButton.text = "Log In"
            toggleLoginSignUpTextView.text = "Or, sign in"
            repeatPasswordEditText.visibility = View.GONE
        }
    }
}