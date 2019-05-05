package com.nmn.keystroke.util

import android.content.Context
import android.support.annotation.ColorInt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.nmn.keystroke.R
import com.nmn.keystroke.model.Connection
import org.jetbrains.anko.doAsync

class ConnectionListAdapter(
    var context: Context,
    var connections: ArrayList<Connection>,
    var connUtil: ConnectionUtil
) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var myView: View
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (convertView == null) {
            myView = inflater.inflate(R.layout.connection_list_item, null, true)
            val textView = myView.findViewById<TextView>(R.id.connection_textview)
            textView.text = connections[position].name
            var result = PingConnection(context, connections[position]).execute().get()
            if (result) {
                textView.setBackgroundColor(context.getColor(R.color.colorConnectionGood))
            }else{
                textView.setBackgroundColor(context.getColor(R.color.colorConnectionBad))
            }
        } else {
            myView = convertView
        }
        return myView
    }

    override fun getItem(position: Int): Any {
        return connections[position]}

    override fun getItemId(position: Int): Long {
        return connections[position].id.toLong()}

    override fun getCount(): Int {
        return connections.size}
}