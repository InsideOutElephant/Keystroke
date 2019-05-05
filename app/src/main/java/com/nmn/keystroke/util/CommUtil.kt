package com.nmn.keystroke.util

import android.util.Log
import com.nmn.keystroke.java.Message
import com.nmn.keystroke.java.MessageType
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

class CommUtil(private val host: String, private val port: Int) {
    private var socket: Socket = Socket()
    private lateinit var objectInputStream: ObjectInputStream
    private lateinit var objectOutputStream: ObjectOutputStream

    fun sendSingleRequest(request: Message, finalize: Boolean): Message {
        getSocket()
        var response: Message
        try {
//            val objectInputStream = socket.getInputStream()
//            val objectOutputStream = socket.getOutputStream()
//            val objectOutputStream = ObjectOutputStream(objectOutputStream)
            //

            Log.i("REQUEST:", request.toString())
            objectOutputStream.writeObject(request)
            objectOutputStream.flush()

            if (finalize) {
                val endRequest = Message(null, MessageType.END)
                objectOutputStream.writeObject(endRequest)
                objectOutputStream.flush()
            }

//            val objectInputStream = ObjectInputStream(objectInputStream)
            response = objectInputStream.readObject() as Message
            Log.d("RESPONSE: ", response.toString())
        } catch (e: Exception) {
            response = Message(null, MessageType.RESPONSE, false)
            if (e != null)
                Log.e("Failed to send message: ", e.message.toString())
        }
        return response
    }

    private fun getSocket() {
        if (socket.isClosed || !socket.isConnected) {
            socket = Socket(host, port)
            objectOutputStream = ObjectOutputStream(socket.getOutputStream())
            objectInputStream = ObjectInputStream(socket.getInputStream())
        }
    }

    fun closeComms() {
        socket.close()
    }
}