package com.example.shop

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_parking.*
import org.jetbrains.anko.*
import java.net.URL


class ParkingActivity : AppCompatActivity(), AnkoLogger {
    private val TAG = ParkingActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking)
        val parking = "http://data.tycg.gov.tw/opendata/datalist/datasetMeta/download?id=f4cc0b12-86ac-40f9-8745-885bddc18f79&rid=0daad6e6-0632-44f5-bd25-5e1de1e9146f"
   //     ParkingTask().execute(parking)

        //Another way to create thread
        doAsync {
            val url = URL(parking)
            val json = url.readText()
            info(json)
            uiThread {
                //  Toast.makeText(it,"GOT IT",Toast.LENGTH_LONG)
                toast("GOT IT")
                infor.text = json
                alert ("GOT IT","ALERT"){
                    okButton {  }
                }.show()
            }
        }
    }


    inner class ParkingTask : AsyncTask<String,Void,String>() {
        override fun doInBackground(vararg params: String?): String {
            val url = URL(params[0])
            val json = url.readText()
            Log.d(TAG,"doInBackground: $json")

            return json
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Toast.makeText(this@ParkingActivity,"GOT IT",Toast.LENGTH_LONG).show()
            infor.text = result
        }
    }
}
