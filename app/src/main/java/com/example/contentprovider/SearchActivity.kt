package com.example.contentprovider

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contentprovider.databinding.ActivitySearchBinding
import com.example.contentprovider.model.ContactModel
import com.example.contentprovider.utils.MyAdapterSearch

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private var contactList = mutableListOf<ContactModel>()
    private var adapter:MyAdapterSearch? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarTB)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        addContact()

        binding.searchContactBTN.setOnClickListener {
            searchContact()
        }
    }

    private fun addContact() {
        contactList = intent
            .extras?.getSerializable("contact") as MutableList<ContactModel>
        adapter = MyAdapterSearch(this,contactList)
        binding.searchRecycleViewRV.layoutManager = LinearLayoutManager(this)
        binding.searchRecycleViewRV.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun searchContact() {
        val searchText = binding.searchContactET.text.toString()
        if (searchText.isNotEmpty()) {
            val iterator = contactList.iterator()
            while (iterator.hasNext()){
                for (i in iterator) {
                    if (!i.name.lowercase().contains(searchText.lowercase())){
                        iterator.remove()
                    }
                }
            }
            adapter?.notifyDataSetChanged()
        }else{
            Toast.makeText(this,
                "Введите символы для поиска", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> startActivity(Intent(this,MainActivity::class.java))
        }
        return super.onOptionsItemSelected(item)

    }
}