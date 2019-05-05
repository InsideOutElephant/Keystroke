package com.nmn.keystroke.model

import java.io.Serializable

data class Connection(
    var id: Int,
    var name: String,
    var host: String,
    var port: Int,
    var mac: String = "",
    var keystroke: Boolean, var description: String = "", var lastConnected: String = ""
) : Serializable