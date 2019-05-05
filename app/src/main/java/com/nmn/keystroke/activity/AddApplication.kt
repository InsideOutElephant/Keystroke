package com.nmn.keystroke.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar;
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import com.nmn.keystroke.java.Command
import com.nmn.keystroke.util.ActionType
import com.nmn.keystroke.util.ImageUtil
import kotlinx.android.synthetic.main.add_application.*
import kotlinx.android.synthetic.main.content_add_application.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException


class AddApplication : AppCompatActivity() {
    private lateinit var nameET: EditText
    private lateinit var commandET: EditText
    private lateinit var argsET: EditText
    private lateinit var command: Command

    private lateinit var bitmap: Bitmap
    private lateinit var encodedImage: String
    private var edit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.nmn.keystroke.R.layout.add_application)
        var toolbar = findViewById<Toolbar>(com.nmn.keystroke.R.id.toolbar)
        setSupportActionBar(toolbar)
        nameET = findViewById(com.nmn.keystroke.R.id.nameEditText)
        commandET = findViewById(com.nmn.keystroke.R.id.commandEditText)
        argsET = findViewById(com.nmn.keystroke.R.id.argsEditText)

        val type = intent.getIntExtra("action", 0)
        if (type == ActionType.UPDATE.ordinal)
            fillExistingData()
    }

    private fun fillExistingData() {
        command = intent.getSerializableExtra("command") as Command
        nameET.setText(command.name)
        commandET.setText(command.command)
        argsET.setText(command.args)
        appIconView.setImageBitmap(ImageUtil().getImage(command.base64Image))
        encodedImage = command.base64Image
        edit = true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(com.nmn.keystroke.R.menu.save_cancel__show_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val name = nameET.text.toString()
        val commandText = commandET.text.toString()
        val args = argsET.text.toString()
        when (item.itemId) {
            com.nmn.keystroke.R.id.save_action_show -> {
                if (validateInput(name, commandText)) {
                    if (edit) {
                        this.command.name = name
                        this.command.command = commandText
                        this.command.base64Image = encodedImage
                    } else
                        this.command = Command(name, commandText)
                    if (args != "") this.command.args = args
                    setData()
                    finish()
                }
            }
            com.nmn.keystroke.R.id.cancel_action_show -> {
                clearFields()
                finish()
            }
        }
        return true
    }

    private fun clearFields() {
        nameET.setText("")
        commandET.setText("")
        argsET.setText("")
    }

    private fun setData() {
        val data = Intent()
        data.putExtra("command", this.command)
        setResult(Activity.RESULT_OK, data)
    }

    private fun validateInput(name: String, command: String): Boolean {
        if (name == "") return false
        if (command == "") return false
        return true
    }

    fun chooseNewIcon(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), ActionType.PICK_IMAGE.ordinal)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ActionType.PICK_IMAGE.ordinal -> {
                    if (data != null) {
                        if (data.data != null) {
                            val imagePath: String = data.data.path
                            Log.e("imagePath", imagePath)
                            try {

//                                bitmap.recycle()
                                val stream = contentResolver.openInputStream(data.data)
                                if (stream != null) {
                                    bitmap = BitmapFactory.decodeStream(stream)
                                    bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false)
                                    val baos = ByteArrayOutputStream()
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                                    val bytes = baos.toByteArray()
                                    encodedImage = Base64.encodeToString(bytes, 0)
                                    stream.close()
                                    appIconView.setImageBitmap(bitmap)
                                }
                            } catch (e: FileNotFoundException) {
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }
}
