package com.example.contentprovider

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contentprovider.databinding.ActivityMainBinding
import com.example.contentprovider.model.ContactModel
import com.example.contentprovider.utils.MyAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private var contactModelList = mutableListOf<ContactModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarTB)

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED){
            permissionsContact.launch(Manifest.permission.READ_CONTACTS)
        }else{
            getContact()
        }
    }

    @SuppressLint("Range")
    private fun getContact(){
        contactModelList = ArrayList()
        val phones = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,null,null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )
        while (phones!!.moveToNext()){
            val name = phones
                .getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber = phones
                .getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            val contactModel = ContactModel(name,phoneNumber)
            contactModelList.add(contactModel)
        }
        phones.close()
        val adapter = MyAdapter(permissionOfSms,permissionsOfCall,this,contactModelList)
        binding.recycleViewRV.layoutManager = LinearLayoutManager(this)
        binding.recycleViewRV.adapter = adapter
    }

    private val permissionsContact = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Получен доступ к контактам", Toast.LENGTH_SHORT).show()
            getContact()
        } else {
            Toast.makeText(this, "В разрешении отказано...", Toast.LENGTH_SHORT).show()
        }
    }
    private val permissionsOfCall = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Получен доступ к звонкам", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "В разрешении отказано...", Toast.LENGTH_SHORT).show()
        }
    }
    private val permissionOfSms = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){ isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Получен доступ к отправке смс", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "В разрешении отказано...", Toast.LENGTH_SHORT).show()
        }
    }
}