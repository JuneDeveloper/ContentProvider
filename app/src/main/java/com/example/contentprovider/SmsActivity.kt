package com.example.contentprovider

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.contentprovider.databinding.ActivitySmsBinding

class SmsActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySmsBinding
    private var number:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarTB)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        number = intent.extras?.getString("sms")
        binding.smsNumberTV.text = number

        binding.sendBTN.setOnClickListener {
            if (binding.smsNumberTV.text.isEmpty() ||
                binding.smsEditTextET.text.isEmpty()) {
                Toast.makeText(this, "Введите сообщение", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else
            sendMessage()
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun sendMessage() {
        val message = binding.smsEditTextET.text.toString()
        try {
            val smsManager: SmsManager
            if (Build.VERSION.SDK_INT>=23) {
                smsManager = this.getSystemService(SmsManager::class.java)
            } else{
                smsManager = SmsManager.getDefault()
            }
            Log.d("NUMBER","$number")
            Log.d("TEXT", message)
            smsManager.sendTextMessage(number, null, message, null, null)
            Toast.makeText(this, "Сообщение отправлено", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(
                this,"Please enter all the data.."+e.message.toString(),
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> startActivity(Intent(this,MainActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}