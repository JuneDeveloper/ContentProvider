package com.example.contentprovider.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.contentprovider.R
import com.example.contentprovider.SmsActivity
import com.example.contentprovider.model.ContactModel

class MyAdapter(
    private val sms:ActivityResultLauncher<String>,
    private val call:ActivityResultLauncher<String>,
    private val context: Context,
    private val contactList:MutableList<ContactModel>)
    :RecyclerView.Adapter<MyAdapter.UserViewHolder>() {


    class UserViewHolder(itemView: View) : ViewHolder(itemView) {
        val callImage: ImageView = itemView.findViewById(R.id.imageCallIV)
        val smsImage: ImageView = itemView.findViewById(R.id.imageSmsIV)
        val name: TextView = itemView.findViewById(R.id.nameTV)
        val phone: TextView = itemView.findViewById(R.id.phoneTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val item = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list, parent, false)
        return UserViewHolder(item)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val contact = contactList[position]
        holder.name.text = contact.name
        holder.phone.text = contact.phone
        holder.callImage.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                call.launch(Manifest.permission.CALL_PHONE)
            } else {
                callTheNumber(contact.phone)
            }
        }
        holder.smsImage.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                sms.launch(Manifest.permission.SEND_SMS)
            } else {
                val intent = Intent(context, SmsActivity::class.java)
                intent.putExtra("sms", contact.phone)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    private fun callTheNumber(number: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$number")
        context.startActivity(intent)
    }
}
