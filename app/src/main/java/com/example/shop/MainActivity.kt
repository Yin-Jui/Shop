package com.example.shop

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.URL

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_nickname.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.row_function.view.*
import org.jetbrains.anko.*
import kotlin.String as String1

class MainActivity : AppCompatActivity(), AnkoLogger {

    private val TAG = MainActivity::class.java.simpleName
    var cacheService: Intent? = null
    val functions = listOf<kotlin.String>("Camera", "Invite friends", "Parking", "Coupons", "News", "Movies", "Maps")
    var n = 9
    private val RC_NICKNAME: Int = 210
    private val RC_SIGNUP: Int = 200
    var signup = false
    val auth = FirebaseAuth.getInstance()

    val broadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(p0: Context?, intent: Intent?) {
            if (intent?.action.equals(CacheService.ACTION_CACHE_DONE)) {
                info("MainActivity cache informed")
                //toast("MainActivity cache informed")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        /*       if(!signup){
                   val intent = Intent(this,SignUp::class.java)
                   startActivityForResult(intent,RC_SIGNUP)
               }*/
        auth.addAuthStateListener { auth ->
            authChanged(auth)
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        //RecycleView

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)
        recycler.adapter = FunctionAdapter()

        //Spinner
        val colors = arrayOf("Red", "Green", "Blue")
        val adapter = ArrayAdapter<kotlin.String>(this, android.R.layout.simple_spinner_item, colors)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                Log.d("MainActivity", "onItemSelected:  ${colors[position]}")
            }


        }
    }

    inner class FunctionAdapter() : RecyclerView.Adapter<FunctionHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FunctionHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_function, parent, false)
            val holder = FunctionHolder(view)
            return holder
        }

        override fun getItemCount(): Int {
            return functions.size
        }

        override fun onBindViewHolder(holder: FunctionHolder, position: Int) {
            holder.nameText.text = functions.get(position)
            holder.itemView.setOnClickListener { view ->
                functionClicked(holder, position)
            }
        }

    }

    private fun functionClicked(holder: FunctionHolder, position: Int) {
        Log.d(TAG, "functionClicked:  $position")
        when (position) {
            1 -> startActivity(Intent(this, ContactActivity::class.java))
            2 -> startActivity(Intent(this, ParkingActivity::class.java))
            5 -> startActivity(Intent(this, MovieActivity::class.java))
            4->startActivity(Intent(this, NewsActivity::class.java))
            6->startActivity(Intent(this, MapsActivity::class.java))
        }

    }

    class FunctionHolder(view: View) : RecyclerView.ViewHolder(view) {

        var nameText: TextView = view.name
    }

    private fun authChanged(auth: FirebaseAuth) {
        if (auth.currentUser == null) {
            val intent = Intent(this, SignUp::class.java)
            startActivityForResult(intent, RC_SIGNUP)
        } else {
            Log.d("MainActivity", "authChanged:  ${auth.currentUser?.uid}")

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGNUP) {
            if (resultCode == Activity.RESULT_OK) {
                val intent = Intent(this, Nickname::class.java)
                startActivityForResult(intent, RC_NICKNAME)
            }

        }
        if (requestCode == RC_NICKNAME) {

        }
    }

    override fun onResume() {
        super.onResume()
        //NICKNAME.text = getNickname()
        if (auth.currentUser != null) {
            FirebaseDatabase.getInstance()
                .getReference("users")
                .child(auth.currentUser!!.uid)
                .child("nickname")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.value != null)
                            NICKNAME.text = dataSnapshot.value as kotlin.String
                    }


                })   //use only one time
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_cache -> {
                doAsync {
                    val json = URL("https://api.myjson.com/bins/sebkp").readText()
                    val movies = Gson().fromJson<List<Movie>>(json, object : TypeToken<List<Movie>>() {}.type)
                    movies.forEach {
                        startService(
                            intentFor<CacheService>(
                                "TITLE" to it.Title,
                                "URL" to it.Poster
                            )
                        )
                    }
                }

                /* cacheService = Intent(this, CacheService::class.java)
                 startService(cacheService)
                 startService(Intent(this,CacheService::class.java))
                 startService(Intent(this,CacheService::class.java))*/
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(CacheService.ACTION_CACHE_DONE)
        registerReceiver(broadcastReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        if (cacheService != null) {
            //  stopService(cacheService)
            unregisterReceiver(broadcastReceiver)
        }
    }
}
