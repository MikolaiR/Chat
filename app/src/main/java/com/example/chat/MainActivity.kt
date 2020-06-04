package com.example.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.widget.*
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
        Log.i("messageEditText","${messageEditText}")
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
                Log.i("messageAwesome","${message!!.name}--${message.text}")
                adapter.add(message)
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }
            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("Not yet implemented")
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        messageDatabaseReference.addChildEventListener(messageChildEventListener)
    }
}