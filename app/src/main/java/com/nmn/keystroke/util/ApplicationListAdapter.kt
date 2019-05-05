package com.nmn.keystroke.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.nmn.keystroke.R
import com.nmn.keystroke.java.Command


class ApplicationListAdapter(var context: Context, var commands: ArrayList<Command>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var grid: View
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (convertView == null) {
            grid = inflater.inflate(R.layout.grid_item, null, true)
            var imageView = grid.findViewById<ImageView>(R.id.grid_item_image)
            var textView = grid.findViewById<TextView>(R.id.grid_item_text)
            imageView.setImageBitmap(ImageUtil().getImage(commands[position].base64Image))
            textView.text = commands[position].name
        } else {
            grid = convertView
        }
        return grid
    }


    override fun getItem(position: Int): Any {
        return commands[position]
    }

    override fun getItemId(position: Int): Long {
        return commands[position].id.toLong()
    }

    override fun getCount(): Int {
        return commands.size
    }
}