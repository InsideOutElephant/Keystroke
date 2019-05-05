package com.nmn.keystroke.model

class CommandKt(private var name: String, private var command: String) {
//    nameET = nameET
//    private var commandET:String? = null
    private var args = ""



    fun getArgs(): String {
        return args
    }

    fun setArgs(args: String) {
        this.args = args
    }
}