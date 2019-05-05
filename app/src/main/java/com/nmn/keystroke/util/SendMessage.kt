package com.nmn.keystroke.util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Base64
import android.util.Log
import com.nmn.keystroke.java.Command
import com.nmn.keystroke.java.Message
import com.nmn.keystroke.java.MessageType
import com.nmn.keystroke.model.MyParams
import java.util.*

class SendMessage :
    AsyncTask<MyParams, Void, HashMap<Int, Command>>() {

    override fun doInBackground(vararg params: MyParams): HashMap<Int, Command> {
        val host = params[0].host
        val port = params[0].port
        val command = params[0].command
        val type = params[0].type
        val keys = params[0].keys
        val closeConnection = params[0].closeConnection
        val commUtil = params[0].commUtil

//        val commUtil = CommUtil(host, port)
        var message = Message(command, type)
        message.keys = keys
        val response = commUtil.sendSingleRequest(message, closeConnection)

        return fillMap(response, type)
    }

    private fun fillMap(response: Message, type: MessageType): HashMap<Int, Command> {
        val commandMap = HashMap<Int, Command>()
        if(response!=null){
        if (type == MessageType.READ) {
            if(!response.commandList.isEmpty()){
            for (command in response.commandList) {
                commandMap[command.id] = command
            }}
        }}
        return commandMap
    }

    override fun onPostExecute(result: HashMap<Int, Command>) {

    }
}