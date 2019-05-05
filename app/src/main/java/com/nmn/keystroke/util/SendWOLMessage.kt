package com.nmn.keystroke.util

import android.os.AsyncTask
import android.widget.Toast
import com.nmn.keystroke.model.Connection
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class SendWOLMessage(val connection: Connection?) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg params: Void?): Void? {
//        val ipStr = "192.168.178.255"
//        val macStr = "D0:50:99:2B:69:3C"
        val ipStr = connection!!.host
        val macStr = connection!!.mac
        val port = 9

        try {
            val macBytes = getMacBytes(macStr)
            val bytes = ByteArray(6 + 16 * macBytes.size)
            for (i in 0..5) {
                bytes[i] = 0xff.toByte()
            }
            var i = 6
            while (i < bytes.size) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.size)
                i += macBytes.size
            }

            val address = InetAddress.getByName(ipStr)
            val packet = DatagramPacket(bytes, bytes.size, address, port)
            val socket = DatagramSocket()
            socket.send(packet)
            socket.close()

            Toast.makeText(null, "Wake-on-LAN packet sent.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(null, "Failed to send Wake-on-LAN packet: " + e.message, Toast.LENGTH_SHORT).show()
        }
        return null
    }

    @Throws(IllegalArgumentException::class)
    private fun getMacBytes(macStr: String): ByteArray {
        val bytes = ByteArray(6)
        val hex = macStr.split("(\\:|\\-)".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (hex.size != 6) {
            throw IllegalArgumentException("Invalid MAC address.")
        }
        try {
            for (i in 0..5) {
                bytes[i] = Integer.parseInt(hex[i], 16).toByte()
            }
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid hex digit in MAC address.")
        }

        return bytes
    }

}
