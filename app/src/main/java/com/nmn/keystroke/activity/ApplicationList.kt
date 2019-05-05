package com.nmn.keystroke.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.EditText
import android.widget.ListView
import android.widget.Switch
import com.nmn.keystroke.R
import com.nmn.keystroke.java.Command
import com.nmn.keystroke.java.Message
import com.nmn.keystroke.java.MessageType
import com.nmn.keystroke.model.Connection
import com.nmn.keystroke.model.MyParams
import com.nmn.keystroke.util.ActionType
import com.nmn.keystroke.util.ApplicationListAdapter
import com.nmn.keystroke.util.CommUtil
import com.nmn.keystroke.util.SendMessage
import kotlinx.android.synthetic.main.application_list.*


class ApplicationList : AppCompatActivity() {
    private lateinit var commandListView: ListView
    private lateinit var commandList: ArrayList<Command>
    private lateinit var keyStrokeEditText: EditText
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var keystrokeSwitch: Switch
    private lateinit var host: String
    private var port: Int = 0
    private var commandMap = HashMap<Int, Command>()
    private lateinit var commUtil: CommUtil
    private lateinit var connection: Connection

    private val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (connection.keystroke) {
                keyStrokeEditText.removeTextChangedListener(this)
                val message = Message(MessageType.KEY, false)
                message.keys = s.toString()
                sendMessage(null, MessageType.KEY, s.toString())
                keyStrokeEditText.setText("")
                keystrokeTextview.append(s)
                keyStrokeEditText.addTextChangedListener(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.application_list)
        val toolbar = findViewById<Toolbar>(com.nmn.keystroke.R.id.toolbar)
        setSupportActionBar(toolbar)


        connection = intent.getSerializableExtra("connection") as Connection
        commUtil = CommUtil(connection.host, connection.port)
        host = connection.host
        port = connection.port
        commandListView = findViewById(com.nmn.keystroke.R.id.standard_list)
        pullToRefresh = findViewById(com.nmn.keystroke.R.id.pullToRefresh)
        keyStrokeEditText = findViewById(com.nmn.keystroke.R.id.keyStrokeEditText)
        if (connection.keystroke)
            keyStrokeEditText.addTextChangedListener(watcher)
        addListListener()
    }

    override fun onStart() {
        getData()
        fillList()
        super.onStart()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        keystrokeSwitch = menu?.findItem(R.id.app_bar_switch)!!.actionView.findViewById(R.id.menu_switch) as Switch
        keystrokeSwitch.isChecked = connection.keystroke
        keystrokeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            connection.keystroke = isChecked
            if (isChecked) keyStrokeEditText.addTextChangedListener(watcher)
            else keyStrokeEditText.removeTextChangedListener(watcher)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val inflater = menuInflater
        inflater.inflate(com.nmn.keystroke.R.menu.add_app_full_option, menu)



        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // action with ID action_refresh was selected
            R.id.add_button -> {
                val intent = Intent(this, AddApplication::class.java)
                startActivityForResult(intent, ActionType.CREATE.ordinal)
            }
            else -> {
            }
        }

        return true
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.context_menu, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val info = item?.menuInfo as AdapterContextMenuInfo
        val id = commandMap[info.id.toInt()]!!.id
        val command = commandMap[id]
        when (item.itemId) {
            R.id.edit_context_menu_item -> {
                val intent = Intent(this, AddApplication::class.java)
                intent.putExtra("action", ActionType.UPDATE.ordinal)
                intent.putExtra("command", command)
                startActivityForResult(intent, ActionType.UPDATE.ordinal)
            }
            R.id.delete_context_menu_item -> sendMessage(command, MessageType.DELETE, null)
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ActionType.CREATE.ordinal -> {
                    if (data != null) {
                        val command = data.getSerializableExtra("command") as Command
                        sendMessage(command, MessageType.CREATE, null)
                    }
                }
                ActionType.UPDATE.ordinal -> {
                    if (data != null) {
                        val command = data.getSerializableExtra("command") as Command
                        sendMessage(command, MessageType.UPDATE, null)
                    }
                }
            }
        }
    }

    private fun sendMessage(command: Command?, type: MessageType, keys: String?) {
        val params = MyParams(host, port, commandListView, command, type, keys, false, commUtil)
        when {
            type == MessageType.READ -> {
                commandMap = SendMessage().execute(params).get()
                commandList = getCommandList(commandMap)
                fillList()
            }
            type == MessageType.KEY -> {
                params.closeConnection = false
                SendMessage().execute(params)
            }
            type != MessageType.EXECUTE && type != MessageType.END -> {
                SendMessage().execute(params)
                sendMessage(null, MessageType.READ, null)
            }
            else -> SendMessage().execute(params)
        }
    }

    private fun getCommandList(commandMap: HashMap<Int, Command>?): ArrayList<Command> {
        val resultList = ArrayList<Command>()
        if (commandMap != null) {
            val ids = commandMap.keys.sorted()
            for (id in ids)
                resultList.add(commandMap[id]!!)
        }
        return resultList
    }

    private fun refreshData() {
        sendMessage(null, MessageType.READ, null)
    }

    private fun addListListener() {
        registerForContextMenu(commandListView)
        commandListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            if (commandMap.containsKey(position)) {
                val command = commandMap[position]
                if (command != null) {
                    sendMessage(command, MessageType.EXECUTE, "")
                }
            }
        }
        pullToRefresh.setOnRefreshListener {
            refreshData()
            pullToRefresh.isRefreshing = false
        }
    }

    private fun getData() {
        sendMessage(null, MessageType.READ, "")
    }

    private fun fillList() {
        val adapter = ApplicationListAdapter(this, ArrayList(commandList))
        commandListView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    fun sendKeystrokeString(v: View) {
        val keys = keyStrokeEditText.text.toString()
        sendMessage(null, MessageType.KEY, keys)
        keystrokeTextview.append(keys)
        keyStrokeEditText.setText("")
    }

    override fun finish() {
        sendMessage(null, MessageType.END, null)
        commUtil.closeComms()
        val data = Intent()
        data.putExtra("connection", this.connection)
        setResult(Activity.RESULT_OK, data)
        super.finish()
    }
}