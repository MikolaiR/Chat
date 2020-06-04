package com.example.chat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class MainActivity : AppCompatActivity() {

    private lateinit var messageListView: ListView
    private lateinit var sendMessageButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var messageEditText: EditText
    private lateinit var sendImageButton: ImageButton

    private lateinit var adapter: AwesomeMessageAdapter
    private var userName: String? = null
    private lateinit var awesomeMessage: MutableList<AwesomeMessage>

    private lateinit var database: FirebaseDatabase
    private lateinit var messageDatabaseReference: DatabaseReference
    private lateinit var messageChildEventListener: ChildEventListener
    private lateinit var usersDatabaseReference: DatabaseReference
    private lateinit var usersChildEventListener: ChildEventListener
    private lateinit var storage: FirebaseStorage
    private lateinit var chatImageStorageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = Firebase.database
        storage = Firebase.storage
        messageDatabaseReference = database.getReference("message")
        usersDatabaseReference = database.getReference("users")
        chatImageStorageReference = storage.getReference("chat_images")

        sendMessageButton = findViewById(R.id.sendMessageButton)
        progressBar = findViewById(R.id.progressBar)
        messageEditText = findViewById(R.id.messageEditText)
        sendImageButton = findViewById(R.id.sendImageButton)
        messageListView = findViewById(R.id.messageListView)

        val intent = intent
        if (intent.getStringExtra(USER_NAME) == null) {
            usersChildEventListener = object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val user = p0.getValue(User::class.java)!!
                    if (user.id == FirebaseAuth.getInstance().currentUser!!.uid) {
                        userName = user.name
                    }
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                }
            }
            usersDatabaseReference.addChildEventListener(usersChildEventListener)
        } else {
            userName = intent.getStringExtra(USER_NAME)
        }



        awesomeMessage = mutableListOf()
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
            val message = AwesomeMessage(messageEditText.text.toString(), userName!!, null)
            messageDatabaseReference.push().setValue(message)

            messageEditText.setText("")
        }
        sendImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = IMAGE_TYPE
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(
                Intent.createChooser(intent, "Choose an song"),
                REQUEST_CODE_IMAGE
            )
        }

        messageChildEventListener = object : ChildEventListener {
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
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data!!.data
            val imageReference =
                chatImageStorageReference.child(selectedImageUri!!.lastPathSegment!!)
            val uploadTask = imageReference.putFile(selectedImageUri)
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val message = AwesomeMessage()
                    message.imageUrl = downloadUri.toString()
                    message.name = userName!!
                    messageDatabaseReference.push().setValue(message)
                } else {
                    // Handle failures
                    // ...
                }
            }
        }
    }

    companion object {
        const val USER_NAME: String = "userName"
        const val IMAGE_TYPE = "image/jpeg"
        const val REQUEST_CODE_IMAGE = 100
    }
}