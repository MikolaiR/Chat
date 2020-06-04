package com.example.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var messageListView: ListView
    private lateinit var sendMessageButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var messageEditText: EditText
    private lateinit var sendPhotoButton: ImageButton

    private lateinit var adapter: AwesomeMessageAdapter
    private var userName = "Default User"
    private lateinit var awesomeMessage:MutableList<AwesomeMessage>

    private lateinit var database: FirebaseDatabase
    private lateinit var messageDatabaseReference: DatabaseReference
    private lateinit var usersDatabaseReference: DatabaseReference
    private lateinit var messageChildEventListener:ChildEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = Firebase.database
        messageDatabaseReference = database.getReference("message")

        sendMessageButton = findViewById(R.id.sendMessageButton)
        progressBar = findViewById(R.id.progressBar)
        messageEditText = findViewById(R.id.messageEditText)
        sendPhotoButton = findViewById(R.id.sendPhotoButton)
        messageListView = findViewById(R.id.messageListView)
        awesomeMessage = mutableListOf<AwesomeMessage>()
        adapter = AwesomeMessageAdapter(this, R.layout.message_item, awesomeMessage)
        messageListView.adapter = adapter

        progressBar.visibility = ProgressBar.INVISIBLE

        messageEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                sendMessageButton.isEnabled = s.toString().trim().isNotEmpty()
            }

        })

        messageEditText.filters = arrayOf(InputFilter.LengthFilter(500))
        sendMessageButton.setOnClickListener {
            val message = AwesomeMessage(messageEditText.text.toString(),userName,null)
            messageDatabaseReference.push().setValue(message)

            messageEditText.setText("")
        }
        sendPhotoButton.setOnClickListener {

        }

        messageChildEventListener = object:ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
               val message = p0.getValue(AwesomeMessage::class.java)
                adapter.add(message)
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        }
        messageDatabaseReference.addChildEventListener(messageChildEventListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.sign_out ->{
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainActivity,SignInActivity::class.java))
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
}