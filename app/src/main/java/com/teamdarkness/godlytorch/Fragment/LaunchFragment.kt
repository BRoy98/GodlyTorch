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


import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView

import com.teamdarkness.godlytorch.R
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_IS_DUAL_TONE
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_SELECTED_DEVICE
import com.teamdarkness.godlytorch.Utils.Utils
import com.teamdarkness.godlytorch.Utils.Utils.askRoot
import org.jetbrains.anko.defaultSharedPreferences

class LaunchFragment : Fragment() {

    private var selectedDevice = ""
    private var isDualTone = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_launch, container, false)

        val logText: TextView = view.findViewById(R.id.logText)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)

        val prefs = context?.defaultSharedPreferences

        prefs?.let {
            // get torch file locations
            selectedDevice = prefs.getString(PREF_SELECTED_DEVICE, "")
            // check for dual tone device
            isDualTone = prefs.getBoolean(PREF_IS_DUAL_TONE, true)
        }

        logText.text = getString(R.string.root_check)
        progressBar.visibility = View.VISIBLE



        Handler().postDelayed({
            if (askRoot()) {
                logText.text = getString(R.string.check_device)
                if (selectedDevice.isNotEmpty())
                    launchKnobs()
                else {
                    if (Utils.checkSupport(context)) {
                        launchKnobs()
                    } else {
                        val fragTransaction = activity?.supportFragmentManager?.beginTransaction()
                        fragTransaction?.let {
                            fragTransaction.replace(R.id.mainFrame, IncompatibleFragment())
                            fragTransaction.commit()
                        }
                    }
                }
            } else {
                logText.text = getString(R.string.root_denied)
                progressBar.visibility = View.INVISIBLE
            }
        }, 1000)
        return view
    }

    private fun launchKnobs() {
        if (isDualTone) {
            val fragTransaction = activity?.supportFragmentManager?.beginTransaction()
            fragTransaction?.let {
                fragTransaction.replace(R.id.mainFrame, ThreeKnobFragment())
                fragTransaction.commit()
            }
        } else {
            val fragTransaction = activity?.supportFragmentManager?.beginTransaction()
            fragTransaction?.let {
                fragTransaction.replace(R.id.mainFrame, SingleKnobFragment())
                fragTransaction.commit()
            }
        }
    }
}
