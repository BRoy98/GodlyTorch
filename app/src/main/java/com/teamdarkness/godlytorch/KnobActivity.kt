package com.teamdarkness.godlytorch

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.LayoutInflaterCompat
import com.chrisplus.rootmanager.RootManager
import com.chrisplus.rootmanager.container.Result
import com.mikepenz.iconics.context.IconicsLayoutInflater2
import com.sdsmdg.harjot.crollerTest.Croller
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class KnobActivity : AppCompatActivity() {

    private var whiteOn = false
    private var yellowOn = false

    private var yellowValue = 0
    private var whiteValue = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        LayoutInflaterCompat.setFactory2(layoutInflater, IconicsLayoutInflater2(delegate))
        super.onCreate(savedInstanceState)

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/blowbrush.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )

        setContentView(R.layout.activity_knob)

        val bothCroller: Croller = findViewById(R.id.bothCroller)
        val whiteCroller: Croller = findViewById(R.id.whiteCroller)
        val yellowCroller: Croller = findViewById(R.id.yellowCroller)

        controlLed(0, 0, true)

        bothCroller.setOnCrollerChangeListener(object : OnCrollerChangeListener {
            override fun onProgressChanged(croller: Croller?, progress: Int) {
                if (progress == 0) {
                    whiteValue = 0
                    yellowValue = 0
                    whiteOn = false
                    yellowOn = false
                } else {
                    yellowOn = true
                    whiteOn = true
                    whiteValue = (255 / 20) * (progress - 1)
                    if (whiteValue > 225)
                        whiteValue = 225
                    yellowValue = (255 / 20) * (progress - 1)
                    if (yellowValue > 225)
                        yellowValue = 225
                }
            }

            override fun onStartTrackingTouch(croller: Croller?) {
            }

            override fun onStopTrackingTouch(croller: Croller?) {
                if (yellowOn)
                    controlLed(whiteValue, yellowValue, true)
                else
                    controlLed(whiteValue, yellowValue, false)
            }
        });

        whiteCroller.setOnCrollerChangeListener(object : OnCrollerChangeListener {
            override fun onProgressChanged(croller: Croller?, progress: Int) {
                if (progress == 0) {
                    whiteValue = 0
                    whiteOn = false
                } else {
                    whiteOn = true
                    whiteValue = (255 / 20) * (progress - 1)
                    if (whiteValue > 225)
                        whiteValue = 225
                }
            }

            override fun onStartTrackingTouch(croller: Croller?) {
            }

            override fun onStopTrackingTouch(croller: Croller?) {
                if (yellowOn)
                    controlLed(whiteValue, yellowValue, true)
                else
                    controlLed(whiteValue, yellowValue, false)
            }
        });

        yellowCroller.setOnCrollerChangeListener(object : OnCrollerChangeListener {
            override fun onProgressChanged(croller: Croller?, progress: Int) {
                if (progress == 0) {
                    yellowValue = 0
                    yellowOn = false
                } else {
                    yellowOn = true
                    yellowValue = (255 / 20) * (progress - 1)
                    if (yellowValue > 225)
                        yellowValue = 225
                }
            }

            override fun onStartTrackingTouch(croller: Croller?) {
            }

            override fun onStopTrackingTouch(croller: Croller?) {
                if (whiteOn)
                    controlLed(whiteValue, yellowValue, true)
                else
                    controlLed(whiteValue, yellowValue, false)
            }
        });
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun controlLed(whiteLed: Int = 0, yellowLed: Int = 0, torchState: Boolean = false) {

        runAsyncTask(@SuppressLint("StaticFieldLeak")
        object : AsyncTask<Void, Void, Result>() {
            override fun doInBackground(vararg p0: Void?): Result {
                var torch: Int = 0
                if (torchState)
                    torch = 255

                val command: String = "echo 0 > /sys/class/leds/led\\:switch/brightness;" +
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
