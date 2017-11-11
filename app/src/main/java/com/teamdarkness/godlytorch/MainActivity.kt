package com.teamdarkness.godlytorch

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast

import com.chrisplus.rootmanager.RootManager
import com.chrisplus.rootmanager.container.Result


class MainActivity : AppCompatActivity() {

    private var whiteSeekBar: SeekBar? = null
    private var yellowSeekBar: SeekBar? = null
    private var buttonOffAll: Button? = null
    private var buttonOnAll: Button? = null
    private var doubleBackToExitPressedOnce = false
    private var whiteOn = false
    private var yellowOn = false
    private var command = ""

    private var yellowValue = 0
    private var whiteValue = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        whiteSeekBar = findViewById(R.id.whiteSeekBar)
        yellowSeekBar = findViewById(R.id.yellowSeekBar)
        buttonOffAll = findViewById(R.id.buttonOffAll)
        buttonOnAll = findViewById(R.id.buttonOnAll)

        command = "echo 0 > /sys/class/leds/led\\:torch_0/brightness;" +
                "echo 0 > /sys/class/leds/led\\:torch_1/brightness;" +
                "echo 0 > /sys/class/leds/led\\:switch/brightness;"
        RootManager.getInstance().runCommand(command)

        buttonOffAll!!.setOnClickListener {
            yellowSeekBar!!.progress = 0
            whiteSeekBar!!.progress = 0
        }

        buttonOnAll!!.setOnClickListener {
            yellowSeekBar!!.progress = 5
            whiteSeekBar!!.progress = 5
        }

        yellowSeekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {

                when (i) {
                    0 -> {
                        yellowValue = 0
                        yellowOn = false
                        if (whiteOn) {
                            controlLed(whiteValue,yellowValue,true)

                        } else {
                            controlLed(0,0,true)

                        }
                    }
                    1 -> {
                        yellowValue = 30
                        yellowOn = true
                        controlLed(whiteValue,yellowValue,true)
                    }
                    2 -> {
                        yellowValue = 70
                        yellowOn = true
                        controlLed(whiteValue,yellowValue,true)
                    }
                    3 -> {
                        yellowValue = 150
                        yellowOn = true
                        controlLed(whiteValue,yellowValue,true)
                    }
                    4 -> {
                        yellowValue = 200
                        yellowOn = true
                        controlLed(whiteValue,yellowValue,true)
                    }
                    5 -> {
                        yellowValue = 255
                        yellowOn = true
                        controlLed(whiteValue,yellowValue,true)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        whiteSeekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                Log.i("YELLOW PROGRESS", i.toString())

                when (i) {
                    0 -> {
                        whiteValue = 0
                        whiteOn = false
                        if (yellowOn) {
                            controlLed(whiteValue,yellowValue,true)

                        } else {
                            controlLed(0,0,false)

                        }
                    }
                    1 -> {
                        whiteValue = 30
                        whiteOn = true
                        controlLed(whiteValue,yellowValue,true)
                    }
                    2 -> {
                        whiteValue = 70
                        whiteOn = true
                        controlLed(whiteValue,yellowValue,true)
                    }
                    3 -> {
                        whiteValue = 150
                        whiteOn = true
                        controlLed(whiteValue,yellowValue,true)
                    }
                    4 -> {
                        whiteValue = 200
                        whiteOn = true
                        controlLed(whiteValue,yellowValue,true)
                    }
                    5 -> {
                        whiteValue = 255
                        whiteOn = true

                        controlLed(whiteValue,yellowValue,true)
                        RootManager.getInstance().runCommand(command)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setCancelable(false)
            alertDialogBuilder.setMessage("Please wait...")
            alertDialogBuilder.show()

            controlLed(0,0,false)

            RootManager.getInstance().runCommand(command)

            Handler().postDelayed({
                finishAffinity()
            }, 1000)

            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)

    }

    private fun controlLed(whiteLed: Int = 0, yellowLed: Int = 0, torchState: Boolean = false) {

        runAsyncTask(@SuppressLint("StaticFieldLeak")
        object : AsyncTask<Void, Void, Result>() {
            override fun doInBackground(vararg p0: Void?): Result {
                var torch: Int = 0
                if(torchState)
                    torch = 255

                command = "echo 0 > /sys/class/leds/led\\:switch/brightness;" +
                        "sleep 0.01;" +
                        "echo $whiteLed > /sys/class/leds/led\\:torch_0/brightness;" +
                        "echo $yellowLed > /sys/class/leds/led\\:torch_1/brightness;" +
                        "echo $torch > /sys/class/leds/led\\:switch/brightness;"
                return RootManager.getInstance().runCommand(command)
            }

            override fun onPostExecute(result: Result?) {

                super.onPostExecute(result)
            }

        })
    }

    private fun <T> runAsyncTask(asyncTask: AsyncTask<T, *, *>, vararg params: T) {
        asyncTask.execute(*params)
    }
}
