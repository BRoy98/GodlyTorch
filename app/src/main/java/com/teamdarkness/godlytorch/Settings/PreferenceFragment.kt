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

package com.teamdarkness.godlytorch.Settings

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.preference.PreferenceFragment
import com.teamdarkness.godlytorch.R
import android.preference.PreferenceManager
import com.teamdarkness.godlytorch.Utils.Utils.getDeviceNameById


class PreferenceFragment : PreferenceFragment(), OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences_main)

        updatePrefs()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        updatePrefs()
    }

    override fun onResume() {
        super.onResume()

        // Register preference change listener
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()

        // Remove preference change listener
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun updatePrefs() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        val selectedDevice = findPreference("selectedDevice")
        val tileBehaviour = findPreference("tileBehaviour")
        val toggleIntensity = findPreference("toggleIntensity")
        val intensitySteps = findPreference("intensitySteps")

        prefs.getString("selectedDevice", null)?.let {
            selectedDevice.summary = getDeviceNameById(prefs.getString("selectedDevice", null))
        }

        // Set 'Tile Behaviour' summery and update 'Intensity Steps' state
        tileBehaviour.summary = when(prefs.getString("tileBehaviour", null)) {
            "1" -> {
                toggleIntensity.isEnabled = true
                intensitySteps.isEnabled = false
                "Toggle"
            }
            "2" -> {
                toggleIntensity.isEnabled = false
                intensitySteps.isEnabled = true
                "Intensity"
            }
            "3" -> {
                toggleIntensity.isEnabled = false
                intensitySteps.isEnabled = false
                "Popup Dialog"
            }
            else -> {
                toggleIntensity.isEnabled = true
                intensitySteps.isEnabled = false
                "Toggle"
            }
        }

        // Set 'Intensity Steps' summery
        intensitySteps.summary = when(prefs.getString("intensitySteps", null)) {
            "1" -> "2 steps"
            "2" -> "3 steps"
            "3" -> "4 steps"
            "4" -> "5 steps"
            "5" -> "6 steps"
            else -> "6 steps"
        }

        // Set 'Toggle intensity' summery
        toggleIntensity.summary = when(prefs.getString("toggleIntensity", null)) {
            "15" -> "15%%"
            "35" -> "35%%"
            "50" -> "50%%"
            "65" -> "65%%"
            "85" -> "85%%"
            "100" -> "100%%"
            else -> "100%%"
        }
    }
}
