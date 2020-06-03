package com.example.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.*

class MainActivity : AppCompatActivity() {

    private lateinit var messageListView: ListView
    private lateinit var sendMessageButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var messageEditText: EditText
    private lateinit var sendPhotoButton:ImageButton

    private lateinit var adapter: AwesomeMessageAdapter
    private  var userName = "Default User"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sendMessageButton = findViewById(R.id.sendMessageButton)
        progressBar = findViewById(R.id.progressBar)
        messageEditText = findViewById(R.id.messageEditText)
        sendPhotoButton = findViewById(R.id.sendPhotoButton)
        messageListView = findViewById(R.id.messageListView)
        var awesomeMessage = arrayOf<AwesomeMessage>()
        adapter = AwesomeMessageAdapter(this,R.layout.message_item,awesomeMessage)
        messageListView.adapter = adapter

        progressBar.visibility = ProgressBar.INVISIBLE

        messageEditText.addTextChangedListener(object : TextWatcher{
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
            messageEditText.setText("")
        }
        sendPhotoButton.setOnClickListener {

        }
    }
}