package com.example.contentprovider

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.RawContacts
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contentprovider.databinding.ActivityMainBinding
import com.example.contentprovider.model.ContactModel
import com.example.contentprovider.utils.MyAdapterMain

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private var adaptor:MyAdapterMain? = null
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding.newAddBTN.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
                permissionWriteContact.launch(Manifest.permission.WRITE_CONTACTS)
            }else{
                addContact()
                adaptor!!.notifyDataSetChanged()
                getContact()
            }
        }
    }

    private fun addContact() {
        val newContactName = binding.newNameAddET.text.toString()
        val newContactPhone = binding.newPhoneAddET.text.toString()
        val listCPO = ArrayList<ContentProviderOperation>()

        listCPO.add(
        ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
            .withValue(RawContacts.ACCOUNT_TYPE,null)
            .withValue(RawContacts.ACCOUNT_NAME,null)
            .build()
        )
        listCPO.add(
        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,0)
            .withValue(ContactsContract.Data.MIMETYPE,StructuredName.CONTENT_ITEM_TYPE)
            .withValue(StructuredName.DISPLAY_NAME,newContactName)
            .build()
        )
        listCPO.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,0)
                .withValue(ContactsContract.Data.MIMETYPE,Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER,newContactPhone)
                .withValue(Phone.TYPE,Phone.TYPE_MOBILE)
                .build()
        )
        Toast.makeText(this,
            "$newContactName добавлен в список контактов", Toast.LENGTH_SHORT).show()
        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY,listCPO)
        }catch (e:Exception){
            Log.e("Exception",e.message!!)
        }
    }

    @SuppressLint("Range")
    private fun getContact(){
        contactModelList = ArrayList()
        val phones = contentResolver.query(
            Phone.CONTENT_URI,
            null,null,null,
            Phone.DISPLAY_NAME
        )
        while (phones!!.moveToNext()){
            val name = phones
                .getString(phones.getColumnIndex(Phone.DISPLAY_NAME))
            val phoneNumber = phones
                .getString(phones.getColumnIndex(Phone.NUMBER))
            val contactModel = ContactModel(name,phoneNumber)
            contactModelList.add(contactModel)
        }
        phones.close()
        adaptor = MyAdapterMain(permissionOfSms,permissionsOfCall,this,contactModelList)
        binding.recycleViewRV.layoutManager = LinearLayoutManager(this)
        binding.recycleViewRV.adapter = adaptor
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
    private val permissionWriteContact = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){ isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Получен доступ к записи контактов", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "В разрешении отказано...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.exitItem -> finish()
            R.id.searchItem -> {
                val intent = Intent(this,SearchActivity::class.java)
                intent.putExtra("contact",ArrayList(contactModelList))
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}