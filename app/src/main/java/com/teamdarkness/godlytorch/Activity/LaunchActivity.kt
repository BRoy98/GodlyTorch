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
 *     along with Godly Torch.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.teamdarkness.godlytorch.Activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.crashlytics.android.Crashlytics

import com.teamdarkness.godlytorch.R
import com.teamdarkness.godlytorch.Utils.Utils.checkSupport
import com.teamdarkness.godlytorch.Utils.Utils.askRoot


class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        val logText: TextView = findViewById(R.id.logText)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        val pref = applicationContext.getSharedPreferences("MainPref", 0)

        logText.text = getString(R.string.root_check)
        progressBar.visibility = View.VISIBLE

        Handler().postDelayed({
            if (askRoot()) {
                logText.text = getString(R.string.check_device)

                val result = checkSupport(pref)

                if (result.isSupported) {
                    if (result.isDualTone) {
                        val intent = Intent(this@LaunchActivity, ThreeKnobActivity::class.java)
                        intent.putExtra("device_id", result.deviceId)
                        val bundle = ActivityOptions.makeCustomAnimation(baseContext, R.anim.fade_in, R.anim.fade_out).toBundle()
                        startActivity(intent, bundle)
                        finish()
                    } else {
                        val intent = Intent(this@LaunchActivity, SingleKnobActivity::class.java)
                        intent.putExtra("device_id", result.deviceId)
                        val bundle = ActivityOptions.makeCustomAnimation(baseContext, R.anim.fade_in, R.anim.fade_out).toBundle()
                        startActivity(intent, bundle)
                        finish()
                    }
                } else {
                    val intent = Intent(this@LaunchActivity, IncompatibleActivity::class.java)
                    intent.putExtra("device_id", result.deviceId)
                    val bundle = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out).toBundle()
                    startActivity(intent, bundle)
                    finish()
                }

            } else {
                logText.text = getString(R.string.root_denied)
                progressBar.visibility = View.INVISIBLE
            }
        }, 500)
    }
}
