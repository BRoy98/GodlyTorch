/*
 * This file is part of Godly Torch.
 *
 *     Godly Torch is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Godly Torch is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Godly Torch. If not, see <http://www.gnu.org/licenses/>.
 */

package com.teamdarkness.godlytorch

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.sdsmdg.harjot.crollerTest.Croller
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener
import com.teamdarkness.godlytorch.Utils.Device
import com.teamdarkness.godlytorch.Utils.DeviceList
import com.teamdarkness.godlytorch.Utils.Utils
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class SingleKnobActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false
    private var masterSingleTap = false

    private var masterValue = 0
    private var masterValueOld = 0

    private var singleLedFileLocation = "led:switch/brightness"
    private var brightnessMax: Int = 100

    private var masterProgress = 1
    private var isUnsupported = true
    var deviceId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_knob)

        val deviceId = this.intent?.getStringExtra("device_id")

        val deviceList: ArrayList<Device> = DeviceList.getDevices()

        val masterCroller: Croller = findViewById(R.id.masterCroller)

        if (deviceId != null && deviceId.isNotEmpty()) {
            for (device in deviceList) {
                if (device.deviceId.toLowerCase() == deviceId) {
                    this.isUnsupported = false
                    this.brightnessMax = device.brightnessMax
                    this.singleLedFileLocation = device.singleLedFileLocation
                }
            }
        }



        masterCroller.setOnCrollerChangeListener(object : OnCrollerChangeListener {

            override fun onProgressChanged(croller: Croller?, progress: Int) {

                if (progress != masterProgress) {
                    if (progress == 1) {
                        masterValue = 0
                    } else {
                        masterValue = (brightnessMax / 20) * (progress - 1)
                        if (masterValue > brightnessMax)
                            masterValue = brightnessMax
                    }
                    masterProgress = progress
                }
            }

            override fun onTap(croller: Croller?) {
                if (masterSingleTap) {
                    if (masterProgress > 1)
                        masterCroller.progress = 1
                    else
                        masterCroller.progress = 20
                }
                masterSingleTap = true
                Handler().postDelayed({ masterSingleTap = false }, 300)
            }

            override fun onStartTrackingTouch(croller: Croller?) {
            }

            override fun onStopTrackingTouch(croller: Croller?) {
                if (masterValue != masterValueOld) {
                    controlLed(masterValue)
                    masterValueOld = masterValue
                }
            }
        })

    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun controlLed(singleLed: Int = 0) {

        val command: String = String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation) +
                getString(R.string.cmd_sleep) +
                String.format(getString(R.string.cmd_echo), singleLed, singleLedFileLocation)
        return Utils.runCommand(command)
    }
}
