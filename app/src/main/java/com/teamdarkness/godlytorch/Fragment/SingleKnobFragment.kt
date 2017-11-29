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

package com.teamdarkness.godlytorch.Fragment

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat.finishAffinity
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import com.sdsmdg.harjot.crollerTest.Croller
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener
import com.teamdarkness.godlytorch.R
import com.teamdarkness.godlytorch.R.layout
import com.teamdarkness.godlytorch.Settings.SettingsActivity
import com.teamdarkness.godlytorch.Utils.Constrains
import com.teamdarkness.godlytorch.Utils.OnFragmentBackPressListener
import com.teamdarkness.godlytorch.Utils.Utils
import org.jetbrains.anko.defaultSharedPreferences

class SingleKnobFragment : Fragment(), OnFragmentBackPressListener {

    private var doubleBackToExitPressedOnce = false
    private var masterSingleTap = false
    private var doubleTapEnabled = false

    private var masterValue = 0
    private var masterValueOld = 0

    private var singleLedFileLocation = ""
    private var brightnessMax: Int = 0

    private var masterProgress = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(layout.fragment_single_knob, container, false)

        val masterCroller: Croller = view.findViewById(R.id.masterCroller)
        val settingsButton: ImageButton = view.findViewById(R.id.settingsButton)

        // get default preference
        val prefs = context?.defaultSharedPreferences

        prefs?.let {
            // get torch file location
            singleLedFileLocation = prefs.getString(Constrains.PREF_SINGLE_FILE_LOCATION, null)

            doubleTapEnabled = prefs.getBoolean(Constrains.PREF_DOUBLE_TONE_ENABLED, false)

            // get max brightness
            brightnessMax = prefs.getInt("brightnessMax", 0)
        }

        settingsButton.setOnClickListener {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
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
                if (doubleTapEnabled) {
                    if (masterSingleTap) {
                        if (masterProgress > 1)
                            masterCroller.progress = 1
                        else
                            masterCroller.progress = 20
                    }
                    masterSingleTap = true
                    Handler().postDelayed({ masterSingleTap = false }, 300)
                }
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

        return view
    }

    private fun controlLed(singleLed: Int = 0) {

        if (singleLedFileLocation.isEmpty())
            return
        val command: String = String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation) +
                getString(R.string.cmd_sleep) +
                String.format(getString(R.string.cmd_echo), singleLed, singleLedFileLocation)
        return Utils.runCommand(command)
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {

            val alertDialogBuilder = ProgressDialog.show(context, "Quit", "Please wait...")
            alertDialogBuilder.setCancelable(false)
            alertDialogBuilder.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            alertDialogBuilder.show()

            controlLed(0)

            Handler().postDelayed({
                alertDialogBuilder.dismiss()
                activity?.finishAffinity()
            }, 300)

            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(context, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

}
