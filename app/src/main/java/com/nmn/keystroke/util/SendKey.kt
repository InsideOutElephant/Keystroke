package com.nmn.keystroke.util

import android.os.AsyncTask
import android.util.Log
import com.nmn.keystroke.activity.ApplicationList
import java.net.HttpURLConnection
import java.net.URL

class SendKey(private  var activity: ApplicationList?) : AsyncTask<String, String, String>()  {
//    private URL
    fun SendKey(url:String) {

    }
    override fun doInBackground(vararg params: String?): String {
        val URL = params[0].toString()
        val result = ""
        Log.d("URL: ", URL)
        try {
            val url = URL(URL)
            val httpURLConnection = url.openConnection() as HttpURLConnection

            httpURLConnection.readTimeout = 8000
            httpURLConnection.connectTimeout = 8000
            httpURLConnection.doOutput = true
//            httpURLConnection.
            httpURLConnection.connect()

            val responseCode: Int = httpURLConnection.responseCode
//            Log.d(activity?.tag, "responseCode - " + responseCode)

            if (responseCode == 200) {
//                val inStream: InputStream = httpURLConnection.inputStream
//                val isReader = InputStreamReader(inStream)
//                val bReader = BufferedReader(isReader)
//                var tempStr: String?
//
//                try {
//
//                    while (true) {
//                        tempStr = bReader.readLine()
//                        if (tempStr == null) {
//                            break
//                        }
//                        result += tempStr
//                    }
//                } catch (Ex: Exception) {
//                    Log.e(activity?.tag, "Error in convertToString " + Ex.printStackTrace())
//                }
                Log.d("Response: ",responseCode.toString())
            }
        } catch (ex: Exception) {
            Log.d("", "Error in doInBackground " + ex.message)
        }
        return result
    }
}