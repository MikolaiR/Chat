package com.example.chat

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide


class AwesomeMessageAdapter(context: Context, resource: Int, objects: Array<out AwesomeMessage>) :
    ArrayAdapter<AwesomeMessage>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
       /* if (convertView == null) {
          val convertView = LayoutInflater.from(context.applicationContext)
                .inflate(R.layout.message_item, parent, false)
            val photoImageView = convertView!!.findViewById<ImageView>(R.id.photoImageView)
            val textTextView = convertView.findViewById<TextView>(R.id.textTextView)
            val nameTextView = convertView.findViewById<TextView>(R.id.nameTextView)
        }*/// todo
            val photoImageView = convertView!!.findViewById<ImageView>(R.id.photoImageView)
            val textTextView = convertView.findViewById<TextView>(R.id.textTextView)
            val nameTextView = convertView.findViewById<TextView>(R.id.nameTextView)


        val message = getItem(position)!!
        val isText = message.imageUrl == null
        if (isText){
            textTextView.visibility = View.VISIBLE
            photoImageView.visibility = View.GONE
            textTextView.text = message.text
        }else{
            textTextView.visibility =View.GONE
            photoImageView.visibility = View.VISIBLE
            Glide.with(photoImageView.context)
                .load(message.imageUrl)
                .into(photoImageView)
        }

        nameTextView.text = message.name

        return convertView
    }
}