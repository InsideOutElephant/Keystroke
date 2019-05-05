package com.nmn.keystroke.util

import android.content.Context
import android.os.AsyncTask
import com.nmn.keystroke.model.Connection

class PingConnection(private val context: Context, private val connection: Connection): AsyncTask<Void, Void, Boolean>() {
    override fun doInBackground(vararg params: Void?): Boolean {
        return ConnectionUtil(context).ping(connection)
    }
}
