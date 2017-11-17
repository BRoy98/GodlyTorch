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

import android.app.ActivityOptions
import android.content.Intent
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView

import com.teamdarkness.godlytorch.Utils.Common
import android.support.constraint.ConstraintLayout
import com.teamdarkness.godlytorch.Utils.Utils


class LaunchActivity : AppCompatActivity() {

    lateinit var logText: TextView
    lateinit var progressBar: ProgressBar
    lateinit var root: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        logText = findViewById(R.id.logText)
        progressBar = findViewById(R.id.progressBar)
        root = findViewById(R.id.root)

        logText.text = getString(R.string.root_check)
        progressBar.visibility = View.VISIBLE

        Handler().postDelayed({
            if (Utils.askRoot()) {
                logText.text = getString(R.string.check_device)

                if (checkSupport()) {
                    val intent = Intent(this@LaunchActivity, ThreeKnobActivity::class.java)
                    intent.putExtra("device_id", Common().getDeviceId())
                    val bundle = ActivityOptions.makeCustomAnimation(baseContext, R.anim.fade_in, R.anim.fade_out).toBundle()
                    startActivity(intent, bundle)
                    finish()
                } else {
                    val intent = Intent(this@LaunchActivity, IncompatibleActivity::class.java)
                    intent.putExtra("device_id", Common().getDeviceId())
                    val bundle = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out).toBundle()
                    startActivity(intent, bundle)
                    finish()
                }

            } else {
                logText.text = getString(R.string.root_denied)
                progressBar.visibility = View.INVISIBLE
            }
        }, 1000)
    }

    private fun checkSupport(): Boolean {
        val supportedDevices = resources.getStringArray(R.array.supported_devices)
        val deviceId = Common().getDeviceId()
        var deviceProduct = android.os.Build.PRODUCT
        var deviceProductSplit = ""


        if (deviceProduct.contains("_")) {
            val product = deviceProduct.split("_")
            deviceProductSplit = product[1]
        }

        return when {
            supportedDevices.contains(deviceId) -> true
            supportedDevices.contains(deviceProduct) -> true
            supportedDevices.contains(deviceProductSplit) -> true
            else -> false
        }
    }

}
