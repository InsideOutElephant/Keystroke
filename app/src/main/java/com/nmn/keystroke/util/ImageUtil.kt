package com.nmn.keystroke.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log

class ImageUtil {
    fun getImage(base64Image: String?): Bitmap? {
        var t: Bitmap? = null
        if (base64Image != null) {
            var img = Base64.decode(base64Image, 0)
            if (img.isNotEmpty()) {
                t = BitmapFactory.decodeByteArray(img, 0, img.size, null)
                Log.i("Image: ", t.toString())
            }
        }
        return t
    }
}
