package com.nmn.keystroke.util

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import com.nmn.keystroke.java.Message
import com.nmn.keystroke.java.MessageType
import com.nmn.keystroke.model.Connection
import org.jetbrains.anko.getStackTraceString
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException

class ConnectionUtil(var context: Context) : Serializable {
    private val filename = "connections"
    private var connectionMap: HashMap<Int, Connection> = load()

    public fun save() {
        val file = File(context.filesDir, filename)
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            ObjectOutputStream(it).writeObject(connectionMap)
        }
    }

    private fun load(): HashMap<Int, Connection> {
        return if (File(context.filesDir, filename).exists())
            ObjectInputStream(context.openFileInput(filename)).readObject() as HashMap<Int, Connection>
        else return HashMap()
    }

    fun create(connection: Connection): Boolean {
        return if (connectionMap.containsKey(connection.id))
            false
        else {
            connectionMap[connection.id] = connection
            save()
            connectionMap = load()
            true
        }
    }

    fun read(id: Int): Connection? {
        return if (connectionMap.containsKey(id)) connectionMap[id] else return null
    }

    fun update(connection: Connection): Boolean {
        return if (connectionMap.containsKey(connection.id)) {
            connectionMap[connection.id] = connection
            save()
            true
        } else return false

    }

    fun delete(id: Int): Boolean {
        return if (connectionMap.containsKey(id)) {
            connectionMap.remove(id)
            save()
            true
        } else {
            return false
        }
    }

    fun getConnectionList(): ArrayList<Connection> {
        var list = ArrayList<Connection>()
        for (c in connectionMap.values)
            list.add(c)
        return list
    }

    fun getNextID(): Int {
        var ids = arrayListOf<Int>()
        for (conn in connectionMap.values) {
            ids.add(conn.id)
        }
        return if (ids.size == 0) 1
        else {
            ids.sort()
            return ids[ids.size - 1] + 1
        }
    }

    fun getAdapter(): ListAdapter? {
        return ArrayAdapter(context, android.R.layout.simple_list_item_1, getConnectionList())
    }

    // connection.host, connection.port, 3000
    fun ping(connection: Connection): Boolean {
        var socket = Socket()
        var inet = InetSocketAddress(connection.host, connection.port)
        try {
            socket.connect(inet, 3000)
            if (socket.isConnected) {
                var request = Message(MessageType.PING, false)

                var response: Message
                try {
                    val objectOutputStream = ObjectOutputStream(socket.getOutputStream())
                    val objectInputStream = ObjectInputStream(socket.getInputStream())
                    Log.i("REQUEST:", request.toString())
                    objectOutputStream.writeObject(request)
                    objectOutputStream.flush()

                    val endRequest = Message(null, MessageType.END)
                    objectOutputStream.writeObject(endRequest)
                    objectOutputStream.flush()

                    response = objectInputStream.readObject() as Message
                    Log.d("RESPONSE: ", response.toString())
                } catch (e: Exception) {
                    response = Message(null, MessageType.RESPONSE, false)
                    if (e != null)
                        Log.e("Failed to send message: ", e.message.toString())
                }

                return response.isSuccess
            }
        } catch (e: SocketTimeoutException) {
            Log.e("Connection timout", "Couldn't connect to " + connection.host + ":" + connection.port)
        } catch (e: Exception) {
            Log.e("Connection Exception", e.getStackTraceString())
            Log.e("Connection Error", "Couldn't connect to " + connection.host + ":" + connection.port)
        }
        return false
    }

    fun close(conn: Connection) {

    }
}

