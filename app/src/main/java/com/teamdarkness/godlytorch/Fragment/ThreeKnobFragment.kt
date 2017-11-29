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
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import com.sdsmdg.harjot.crollerTest.Croller
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener

import com.teamdarkness.godlytorch.R
import com.teamdarkness.godlytorch.Settings.SettingsActivity
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_DOUBLE_TONE_ENABLED
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_TOGGLE_FILE_LOCATION
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_WHITE_FILE_LOCATION
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_YELLOW_FILE_LOCATION
import com.teamdarkness.godlytorch.Utils.OnFragmentBackPressListener
import com.teamdarkness.godlytorch.Utils.Utils
import org.jetbrains.anko.defaultSharedPreferences

class ThreeKnobFragment : Fragment(), OnFragmentBackPressListener {

    private var doubleBackToExitPressedOnce = false
    private var whiteSingleTap = false
    private var yellowSingleTap = false
    private var masterSingleTap = false

    private var whiteOn = false
    private var yellowOn = false
    private var doubleTapEnabled = false

    private var yellowValue = 0
    private var whiteValue = 0
    private var yellowValueOld = 0
    private var whiteValueOld = 0

    private var whiteLedFileLocation = ""
    private var yellowLedFileLocation = ""
    private var toggleFileLocation = ""
    private var brightnessMax = 0

    private var yellowProgress = 1
    private var whiteProgress = 1
    private var masterProgress = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_three_knob, container, false)

        val masterCroller: Croller = view.findViewById(R.id.bothCroller)
        val whiteCroller: Croller = view.findViewById(R.id.whiteCroller)
        val yellowCroller: Croller = view.findViewById(R.id.yellowCroller)
        val settingsButton: ImageButton = view.findViewById(R.id.settingsButton)

        val prefs = context?.defaultSharedPreferences

        prefs?.let {
            // get torch file location
            whiteLedFileLocation = prefs.getString(PREF_WHITE_FILE_LOCATION, null)
            yellowLedFileLocation = prefs.getString(PREF_YELLOW_FILE_LOCATION, null)
            toggleFileLocation = prefs.getString(PREF_TOGGLE_FILE_LOCATION, null)

            doubleTapEnabled = prefs.getBoolean(PREF_DOUBLE_TONE_ENABLED, false)

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
                        whiteCroller.isEnabled = true
                        yellowCroller.isEnabled = true
                        whiteValue = 0
                        yellowValue = 0
                        whiteOn = false
                        yellowOn = false
                    } else {
                        whiteCroller.isEnabled = false
                        yellowCroller.isEnabled = false
                        yellowOn = true
                        whiteOn = true
                        whiteValue = (brightnessMax / 20) * (progress - 1)
                        if (whiteValue > brightnessMax)
                            whiteValue = brightnessMax
                        yellowValue = (brightnessMax / 20) * (progress - 1)
                        if (yellowValue > brightnessMax)
                            yellowValue = brightnessMax
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
                if (whiteValue != whiteValueOld || yellowValue != yellowValueOld) {
                    if (yellowOn)
                        controlLed(whiteValue, yellowValue, true)
                    else
                        controlLed(whiteValue, yellowValue, false)
                    whiteValueOld = whiteValue
                    yellowValueOld = yellowValue
                }
            }
        })

        whiteCroller.setOnCrollerChangeListener(object : OnCrollerChangeListener {
            override fun onProgressChanged(croller: Croller?, progress: Int) {
                if (progress != whiteProgress) {
                    if (progress == 1) {
                        if (yellowCroller.progress == 1)
                            masterCroller.isEnabled = true
                        whiteValue = 0
                        whiteOn = false
                    } else {
                        masterCroller.isEnabled = false
                        whiteOn = true
                        whiteValue = (255 / 20) * (progress - 1)
                        if (whiteValue > 225)
                            whiteValue = 225
                    }
                    whiteProgress = progress
                }
            }

            override fun onTap(croller: Croller?) {
                if (doubleTapEnabled) {
                    if (whiteSingleTap) {
                        if (whiteOn)
                            whiteCroller.progress = 1
                        else
                            whiteCroller.progress = 20
                    }
                    whiteSingleTap = true
                    Handler().postDelayed({ whiteSingleTap = false }, 300)
                }
            }

            override fun onStartTrackingTouch(croller: Croller?) {
            }

            override fun onStopTrackingTouch(croller: Croller?) {
                if (whiteValue != whiteValueOld) {
                    when {
                        whiteOn || yellowOn -> controlLed(whiteValue, yellowValue, true)
                        else -> controlLed(whiteValue, yellowValue, false)
                    }
                    whiteValueOld = whiteValue
                }
            }
        })

        yellowCroller.setOnCrollerChangeListener(object : OnCrollerChangeListener {
            override fun onProgressChanged(croller: Croller?, progress: Int) {
                if (progress != yellowProgress) {
                    if (progress == 1) {
                        if (whiteCroller.progress == 1)
                            masterCroller.isEnabled = true
                        yellowValue = 0
                        yellowOn = false
                    } else {
                        masterCroller.isEnabled = false
                        yellowOn = true
                        yellowValue = (255 / 20) * (progress - 1)
                        if (yellowValue > 225)
                            yellowValue = 225
                    }
                    yellowProgress = progress
                }
            }

            override fun onTap(croller: Croller?) {
                if (doubleTapEnabled) {
                    if (yellowSingleTap) {
                        if (yellowOn)
                            yellowCroller.progress = 1
                        else
                            yellowCroller.progress = 20
                    }
                    yellowSingleTap = true
                    Handler().postDelayed({ yellowSingleTap = false }, 300)
                }
            }

            override fun onStartTrackingTouch(croller: Croller?) {
            }

            override fun onStopTrackingTouch(croller: Croller?) {
                if (yellowValue != yellowValueOld) {
                    when {
                        yellowOn || whiteOn -> controlLed(whiteValue, yellowValue, true)
                        else -> controlLed(whiteValue, yellowValue, false)
                    }
                    yellowValueOld = yellowValue
                }
            }
        })

        return view
    }

    private fun controlLed(whiteLed: Int = 0, yellowLed: Int = 0, torchState: Boolean = false) {
        if (whiteLedFileLocation.isEmpty() || yellowLedFileLocation.isEmpty() || toggleFileLocation.isEmpty())
            return
        var torch = 0
        if (torchState)
            torch = this.brightnessMax

        val command: String = String.format(getString(R.string.cmd_echo), "0", toggleFileLocation) +
                getString(R.string.cmd_sleep) +
                String.format(getString(R.string.cmd_echo), whiteLed, whiteLedFileLocation) +
                String.format(getString(R.string.cmd_echo), yellowLed, yellowLedFileLocation) +
                String.format(getString(R.string.cmd_echo), torch, toggleFileLocation)
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
