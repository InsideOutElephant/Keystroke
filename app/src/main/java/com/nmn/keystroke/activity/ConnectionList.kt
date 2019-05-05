package com.nmn.keystroke.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.nmn.keystroke.R
import com.nmn.keystroke.java.MessageType
import com.nmn.keystroke.model.Connection
import com.nmn.keystroke.model.MyParams
import com.nmn.keystroke.util.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivityForResult


class ConnectionList : AppCompatActivity() {
    lateinit var connListView: ListView
    lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var connList: ArrayList<Connection>
    private lateinit var connUtil: ConnectionUtil
    private lateinit var myContext: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.nmn.keystroke.R.layout.connection_list)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        connUtil = ConnectionUtil(applicationContext)
//        connList = connUtil.getConnectionList()
        connListView = findViewById(R.id.standard_list)
        pullToRefresh = findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener { refreshList(); }
        connListView.setOnItemClickListener { _, _, position, _ ->
            val connection = connList[position]

            val intent = Intent(this, ApplicationList::class.java)
            intent.putExtra("connection", connection)
//                intent.putExtra("commUtil", )
            startActivityForResult(intent, ActionType.UPDATE.ordinal)
        }
        myContext = this
        registerForContextMenu(connListView)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.connection_context_menu, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val info = item?.menuInfo as AdapterView.AdapterContextMenuInfo
        val id = connList[info.id.toInt()]!!.id
        val connection = connUtil.read(id)
        when (item.itemId) {
            R.id.edit_context_menu_item -> {
                val intent = Intent(this, AddConnection::class.java)
                intent.putExtra("action", ActionType.UPDATE.ordinal)
                intent.putExtra("connection", connection)
                startActivityForResult(intent, ActionType.UPDATE.ordinal)
            }
            R.id.delete_context_menu_item -> connUtil.delete(id)
            R.id.wol_context_item -> SendWOLMessage(connection).execute()
        }
        return true
    }

    override fun onStart() {
        refreshList()
        super.onStart()
    }

    override fun onStop() {
        connUtil
        super.onStop()
    }

    private fun refreshList() {
        connList = connUtil.getConnectionList()
        val adapter = ConnectionListAdapter(myContext, connList, connUtil)
        connListView.adapter = adapter
        adapter.notifyDataSetChanged()
        pullToRefresh.isRefreshing = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(com.nmn.keystroke.R.menu.add_menu_show, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_button -> {
                var intent = Intent(this, AddConnection::class.java)
//                intent.putExtra("connUtil", connUtil)
                startActivityForResult(intent, 1)
                Log.i("DEBUG: ", "After new conn activity launch")
            }
            else -> {
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (requestCode == ActionType.CREATE.ordinal) {
                    val newConn = data.getSerializableExtra("connection") as Connection
                    connUtil.create(newConn)
                    connListView.adapter = connUtil.getAdapter()
                } else if (requestCode == ActionType.UPDATE.ordinal) {
                    val conn = data.getSerializableExtra("connection") as Connection
                    connUtil.update(conn)
                }
                //  else if (requestCode == ActionType.CLOSE.ordinal){
                //    val conn = data.getSerializableExtra("connection") as Connection
                //  connUtil.close(conn)
                //SendMessage().execute(MyParams(conn.host, conn.port, null, null, MessageType.END, null, true, null))
                //}
            }
        }
    }
}
