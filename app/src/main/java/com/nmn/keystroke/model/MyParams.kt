package com.nmn.keystroke.model

import android.widget.ListView
import com.nmn.keystroke.java.Command
import com.nmn.keystroke.java.MessageType
import com.nmn.keystroke.util.CommUtil

data class MyParams(
    var host: String,
    var port: Int,
    var commandList: ListView,
    var command: Command?,
    var type: MessageType,
    var keys: String?,
    var closeConnection: Boolean,
    val commUtil: CommUtil
)