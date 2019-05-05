package com.nmn.keystroke.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.nmn.keystroke.R
import com.nmn.keystroke.model.Connection
import com.nmn.keystroke.util.ConnectionUtil

import kotlinx.android.synthetic.main.add_connection.*

class AddConnection : AppCompatActivity() {
    lateinit var connUtil:ConnectionUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_connection)
        var toolbar = findViewById<Toolbar>(com.nmn.keystroke.R.id.toolbar)
        setSupportActionBar(toolbar)
//        connUtil = intent.getSerializableExtra("connUtil") as ConnectionUtil
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(com.nmn.keystroke.R.menu.save_cancel__show_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // action with ID action_refresh was selected
            R.id.save_action_show -> {
                val name = findViewById<EditText>(R.id.nameEditText).text.toString()
                val host = findViewById<EditText>(R.id.hostEditText).text.toString()
                val port = findViewById<EditText>(R.id.portEditText).text.toString()
                val mac = findViewById<EditText>(R.id.macAddressEditText).text.toString()
                var saved = false
                if (validateInput(name, host, port, mac)) {
                    var id = ConnectionUtil(applicationContext).getNextID()
                    val portNumber = Integer.valueOf(port)
                    var newConnection = Connection(id, name, host, portNumber, mac, false)
                    saved = ConnectionUtil(applicationContext).create(newConnection)
                    if (saved) {
                        clearInputFields();
                        Toast.makeText(this, "Connection saved successfully", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                } else {
                    Toast.makeText(this, "Please enter correct values", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            // action with ID action_settings was selected
            R.id.cancel_action_show -> finish()
            else -> {
            }
        }
        return true
    }

    private fun clearInputFields() {
        findViewById<EditText>(R.id.nameEditText).setText("")
        findViewById<EditText>(R.id.hostEditText).setText("")
        findViewById<EditText>(R.id.portEditText).setText("")
        findViewById<EditText>(R.id.macAddressEditText).setText("")
    }

    private fun validateInput(name: String, host: String, port: String, mac: String): Boolean {
        if (name == "") return false
        if (host == "") return false
        if (port == "") return false
        else if (Integer.valueOf(port) !in -1..65500) return false
        if (mac == "") return false
        return true
    }
}
