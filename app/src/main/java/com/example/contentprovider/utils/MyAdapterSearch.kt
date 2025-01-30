package com.example.contentprovider.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.contentprovider.R
import com.example.contentprovider.model.ContactModel

class MyAdapterSearch(
    private val context: Context,
    private val contactList: MutableList<ContactModel>)
    :RecyclerView.Adapter<MyAdapterSearch.UserViewHolder>() {

    class UserViewHolder(itemView:View):ViewHolder(itemView){
        val imageCall:ImageView = itemView.findViewById(R.id.imageCallIV)
        val name:TextView = itemView.findViewById(R.id.nameTV)
        val phone:TextView = itemView.findViewById(R.id.phoneTV)
        val imageSms:ImageView = itemView.findViewById(R.id.imageSmsIV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val item = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list,parent,false)
        return UserViewHolder(item)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val contact = contactList[position]
        holder.name.text = contact.name
        holder.phone.text = contact.phone
        holder.imageSms.visibility = View.GONE
        holder.imageCall.setOnClickListener {
            callTheNumber(contact.phone)
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