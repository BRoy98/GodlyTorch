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

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.teamdarkness.godlytorch.Fragment.IncompatibleFragment
import com.teamdarkness.godlytorch.Fragment.LaunchFragment
import com.teamdarkness.godlytorch.Fragment.SingleKnobFragment
import com.teamdarkness.godlytorch.Fragment.ThreeKnobFragment
import com.teamdarkness.godlytorch.R
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_IS_DUAL_TONE
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_SELECTED_DEVICE
import com.teamdarkness.godlytorch.Utils.OnFragmentBackPressListener
import com.teamdarkness.godlytorch.Utils.Utils
import org.jetbrains.anko.defaultSharedPreferences
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper



class MainActivity : AppCompatActivity() {

    private var selectedDevice = ""
    private var isDualTone = true
    private var paused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/sans_regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build())
        setContentView(R.layout.activity_main)

        val fragTransaction = supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.mainFrame, LaunchFragment())
        fragTransaction.commit()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onPause() {
        super.onPause()
        paused = true
    }

    override fun onResume() {
        super.onResume()
        if(paused) {
            paused = false

            val prefs = defaultSharedPreferences

            prefs.let {
                // get torch file locations
                selectedDevice = prefs.getString(PREF_SELECTED_DEVICE, "")
                // check for dual tone device
                isDualTone = prefs.getBoolean(PREF_IS_DUAL_TONE, true)
            }
            if (selectedDevice.isNotEmpty())
                launchKnobs()
            else {
                if (Utils.checkSupport(this)) {
                    launchKnobs()
                } else {
                    val fragTransaction = supportFragmentManager?.beginTransaction()
                    fragTransaction?.let {
                        fragTransaction.replace(R.id.mainFrame, IncompatibleFragment())
                        fragTransaction.commit()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        val fragmentList = supportFragmentManager.fragments
        fragmentList?.filterIsInstance<OnFragmentBackPressListener>()?.forEach {
            it.onBackPressed()
        }
    }


    private fun launchKnobs() {
        if (isDualTone) {
            val fragTransaction = supportFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.mainFrame, ThreeKnobFragment())
            fragTransaction.commit()

        } else {
            val fragTransaction = supportFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.mainFrame, SingleKnobFragment())
            fragTransaction.commit()

        }
    }
}
