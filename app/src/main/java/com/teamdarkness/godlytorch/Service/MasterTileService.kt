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

package com.teamdarkness.godlytorch.Service

import android.annotation.TargetApi
import android.content.Context
import android.service.quicksettings.TileService
import android.os.Build
import android.service.quicksettings.Tile
import com.teamdarkness.godlytorch.Utils.Utils.askRoot
import com.teamdarkness.godlytorch.Dialog.TileDialog
import com.teamdarkness.godlytorch.R
import com.teamdarkness.godlytorch.Utils.Constrains.MAIN_PREFERENCE
import com.teamdarkness.godlytorch.Utils.Utils.checkSupport
import com.teamdarkness.godlytorch.Utils.Utils.runCommand
import com.google.firebase.analytics.FirebaseAnalytics
import android.os.Bundle
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_BRIGHTNESS_MAX
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_FIRST_INITIALIZATION
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_SINGLE_FILE_LOCATION
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_TILE_MASTER_ON
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_TILE_WHITE_ON
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_TILE_YELLOW_ON
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_TOGGLE_FILE_LOCATION
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_WHITE_FILE_LOCATION
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_YELLOW_FILE_LOCATION

@TargetApi(Build.VERSION_CODES.N)
class MasterTileService : TileService() {

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private val TILE_STATUS = "masterTileStatus"

    override fun onTileAdded() {
        super.onTileAdded()
        val prefs = applicationContext.getSharedPreferences(MAIN_PREFERENCE,
                Context.MODE_PRIVATE)
        val result = checkSupport(prefs)
        if (result.isSupported) {
            if (result.isDualTone) {
                qsTile.label = "Master Torch"
                qsTile.state = Tile.STATE_INACTIVE
            } else {
                qsTile.label = "Torch"
                qsTile.state = Tile.STATE_INACTIVE
            }
        } else {
            qsTile.label = "Unsupported Device"
            qsTile.state = Tile.STATE_UNAVAILABLE
        }
        qsTile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        updateTile()
    }

    private fun updateTile() {

        val prefs = applicationContext.getSharedPreferences(MAIN_PREFERENCE,
                Context.MODE_PRIVATE)

        val isInitialised = prefs.getBoolean(PREF_FIRST_INITIALIZATION, false)

        if (!isInitialised) {
            showDialog(TileDialog.getDialog(this, "Godly Torch",
                    "Please open the app once before using quick settings tiles."))
            return
        }

        val whiteTileIsOn = prefs.getBoolean(PREF_TILE_WHITE_ON, false)
        val yellowTileIsOn = prefs.getBoolean(PREF_TILE_YELLOW_ON, false)

        if (whiteTileIsOn) {
            showDialog(TileDialog.getDialog(this, "Godly Torch",
                    "White Torch tile is active. You can use one tile at a time only."))
            return
        } else if (yellowTileIsOn) {
            showDialog(TileDialog.getDialog(this, "Godly Torch",
                    "Yellow Control tile is active. You can use one tile at a time only."))
            return
        }

        val tile = this.qsTile
        tile.state = Tile.STATE_UNAVAILABLE
        tile.updateTile()

        val editor = prefs.edit()
        val tileStatus = prefs.getInt(TILE_STATUS, 0)

        if (!askRoot()) {
            if (!isLocked) {
                showDialog(TileDialog.getDialog(this, "Godly Torch",
                        "Root access is required to run Godly Torch. " +
                                "Make sure your device is rooted and root access is enabled."))
            }
            tile.state = Tile.STATE_INACTIVE
            tile.updateTile()
            return
        }
        val result = checkSupport(prefs)
        if (result.isSupported) {
            if (result.isDualTone) {

                // Read device specific values
                val whiteLedFileLocation = prefs.getString(PREF_WHITE_FILE_LOCATION, null)
                val yellowLedFileLocation = prefs.getString(PREF_YELLOW_FILE_LOCATION, null)
                val toggleFileLocation = prefs.getString(PREF_TOGGLE_FILE_LOCATION, null)
                val brightnessMax = prefs.getInt(PREF_BRIGHTNESS_MAX, 0)
                when (tileStatus) {
                    0 -> {
                        val torchVal = (brightnessMax * 15) / 100
                        val command: String = String.format(getString(R.string.cmd_echo), "0", toggleFileLocation) +
                                getString(R.string.cmd_sleep) +
                                String.format(getString(R.string.cmd_echo), torchVal, whiteLedFileLocation) +
                                String.format(getString(R.string.cmd_echo), torchVal, yellowLedFileLocation) +
                                String.format(getString(R.string.cmd_echo), brightnessMax, toggleFileLocation)
                        runCommand(command)
                        editor.putBoolean(PREF_TILE_MASTER_ON, true)
                        editor.putInt(TILE_STATUS, 1)
                        editor.apply()
                        tile.label = "15%"
                        tile.state = Tile.STATE_ACTIVE

                        // Log tile click to analytics
                        val bundle = Bundle()
                        bundle.putString("tile_type", "master")
                        bundle.putString("device_type", "dual_tone")
                        mFirebaseAnalytics = FirebaseAnalytics.getInstance(baseContext)
                        mFirebaseAnalytics.logEvent("tile_click", bundle)
                    }
                    1 -> {
                        val torchVal = (brightnessMax * 35) / 100
                        val command: String = String.format(getString(R.string.cmd_echo), "0", toggleFileLocation) +
                                getString(R.string.cmd_sleep) +
                                String.format(getString(R.string.cmd_echo), torchVal, whiteLedFileLocation) +
                                String.format(getString(R.string.cmd_echo), torchVal, yellowLedFileLocation) +
                                String.format(getString(R.string.cmd_echo), brightnessMax, toggleFileLocation)
                        runCommand(command)
                        editor.putBoolean(PREF_TILE_MASTER_ON, true)
                        editor.putInt(TILE_STATUS, 2)
                        editor.apply()
                        tile.label = "35%"
                        tile.state = Tile.STATE_ACTIVE

                        // Log tile click to analytics
                        val bundle = Bundle()
                        bundle.putString("tile_type", "master")
                        bundle.putString("device_type", "dual_tone")
                        mFirebaseAnalytics = FirebaseAnalytics.getInstance(baseContext)
                        mFirebaseAnalytics.logEvent("tile_click", bundle)

                    }
                    2 -> {
                        val torchVal = (brightnessMax * 50) / 100
                        val command: String = String.format(getString(R.string.cmd_echo), "0", toggleFileLocation) +
                                getString(R.string.cmd_sleep) +
                                String.format(getString(R.string.cmd_echo), torchVal, whiteLedFileLocation) +
                                String.format(getString(R.string.cmd_echo), torchVal, yellowLedFileLocation) +
                                String.format(getString(R.string.cmd_echo), brightnessMax, toggleFileLocation)
                        runCommand(command)
                        editor.putBoolean(PREF_TILE_MASTER_ON, true)
                        editor.putInt(TILE_STATUS, 3)
                        editor.apply()
                        tile.label = "50%"
                        tile.state = Tile.STATE_ACTIVE

                    }
                    3 -> {
                        val torchVal = (brightnessMax * 65) / 100
                        val command: String = String.format(getString(R.string.cmd_echo), "0", toggleFileLocation) +
                                getString(R.string.cmd_sleep) +
                                String.format(getString(R.string.cmd_echo), torchVal, whiteLedFileLocation) +
                                String.format(getString(R.string.cmd_echo), torchVal, yellowLedFileLocation) +
                                String.format(getString(R.string.cmd_echo), brightnessMax, toggleFileLocation)
                        runCommand(command)
                        editor.putBoolean(PREF_TILE_MASTER_ON, true)
                        editor.putInt(TILE_STATUS, 4)
                        editor.apply()
                        tile.label = "65%"
                        tile.state = Tile.STATE_ACTIVE

                    }
                    4 -> {
                        val torchVal = (brightnessMax * 85) / 100
                        val command: String = String.format(getString(R.string.cmd_echo), "0", toggleFileLocation) +
                                getString(R.string.cmd_sleep) +
                                String.format(getString(R.string.cmd_echo), torchVal, whiteLedFileLocation) +
                                String.format(getString(R.string.cmd_echo), torchVal, yellowLedFileLocation) +
                                String.format(getString(R.string.cmd_echo), brightnessMax, toggleFileLocation)
                        runCommand(command)
                        editor.putBoolean(PREF_TILE_MASTER_ON, true)
                        editor.putInt(TILE_STATUS, 5)
                        editor.apply()
                        tile.label = "85%"
                        tile.state = Tile.STATE_ACTIVE

                    }
                    5 -> {
                        val command: String = String.format(getString(R.string.cmd_echo), "0", toggleFileLocation) +
                                getString(R.string.cmd_sleep) +
                                String.format(getString(R.string.cmd_echo), brightnessMax, whiteLedFileLocation) +
                                String.format(getString(R.string.cmd_echo), brightnessMax, yellowLedFileLocation) +
                                String.format(getString(R.string.cmd_echo), brightnessMax, toggleFileLocation)
                        runCommand(command)
                        editor.putBoolean(PREF_TILE_MASTER_ON, true)
                        editor.putInt(TILE_STATUS, 6)
                        editor.apply()
                        tile.label = "100%"
                        tile.state = Tile.STATE_ACTIVE

                    }
                    6 -> {
                        val command: String = String.format(getString(R.string.cmd_echo), 0, whiteLedFileLocation) +
                                String.format(getString(R.string.cmd_echo), 0, yellowLedFileLocation) +
                                String.format(getString(R.string.cmd_echo), 0, toggleFileLocation)
                        runCommand(command)
                        editor.putBoolean(PREF_TILE_MASTER_ON, false)
                        editor.putInt(TILE_STATUS, 0)
                        editor.apply()
                        tile.label = "Torch"
                        tile.state = Tile.STATE_INACTIVE

                    }
                }
                tile.updateTile()
            } else {

                // Read device specific values
                val singleLedFileLocation = prefs.getString(PREF_SINGLE_FILE_LOCATION, null)
                val brightnessMax = prefs.getInt("brightnessMax", 0)

                when (tileStatus) {
                    0 -> {
                        val torchVal = (brightnessMax * 15) / 100
                        val command: String = String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation) +
                                getString(R.string.cmd_sleep) +
                                String.format(getString(R.string.cmd_echo), torchVal, singleLedFileLocation)
                        runCommand(command)
                        editor.putInt(TILE_STATUS, 1)
                        editor.apply()
                        tile.label = "15%"
                        tile.state = Tile.STATE_ACTIVE

                        // Log tile click to analytics
                        val bundle = Bundle()
                        bundle.putString("tile_type", "master")
                        bundle.putString("device_type", "single_tone")
                        mFirebaseAnalytics = FirebaseAnalytics.getInstance(baseContext)
                        mFirebaseAnalytics.logEvent("tile_click", bundle)
                    }
                    1 -> {
                        val torchVal = (brightnessMax * 35) / 100
                        val command: String = String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation) +
                                getString(R.string.cmd_sleep) +
                                String.format(getString(R.string.cmd_echo), torchVal, singleLedFileLocation)
                        runCommand(command)
                        editor.putInt(TILE_STATUS, 2)
                        editor.apply()
                        tile.label = "35%"
                        tile.state = Tile.STATE_ACTIVE

                    }
                    2 -> {
                        val torchVal = (brightnessMax * 50) / 100
                        val command: String = String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation) +
                                getString(R.string.cmd_sleep) +
                                String.format(getString(R.string.cmd_echo), torchVal, singleLedFileLocation)
                        runCommand(command)
                        editor.putInt(TILE_STATUS, 3)
                        editor.apply()
                        tile.label = "50%"
                        tile.state = Tile.STATE_ACTIVE

                    }
                    3 -> {
                        val torchVal = (brightnessMax * 65) / 100
                        val command: String = String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation) +
                                getString(R.string.cmd_sleep) +
                                String.format(getString(R.string.cmd_echo), torchVal, singleLedFileLocation)
                        runCommand(command)
                        editor.putInt(TILE_STATUS, 4)
                        editor.apply()
                        tile.label = "65%"
                        tile.state = Tile.STATE_ACTIVE

                    }
                    4 -> {
                        val torchVal = (brightnessMax * 85) / 100
                        val command: String = String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation) +
                                getString(R.string.cmd_sleep) +
                                String.format(getString(R.string.cmd_echo), torchVal, singleLedFileLocation)
                        runCommand(command)
                        editor.putInt(TILE_STATUS, 5)
                        editor.apply()
                        tile.label = "85%"
                        tile.state = Tile.STATE_ACTIVE

                    }
                    5 -> {
                        val command: String = String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation) +
                                getString(R.string.cmd_sleep) +
                                String.format(getString(R.string.cmd_echo), brightnessMax, singleLedFileLocation)
                        runCommand(command)
                        editor.putInt(TILE_STATUS, 6)
                        editor.apply()
                        tile.label = "100%"
                        tile.state = Tile.STATE_ACTIVE

                    }
                    6 -> {
                        val command: String = String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation)
                        runCommand(command)
                        editor.putInt(TILE_STATUS, 0)
                        editor.apply()
                        tile.label = "Torch"
                        tile.state = Tile.STATE_INACTIVE

                    }
                }
                tile.updateTile()
            }
        } else {
            tile.state = Tile.STATE_INACTIVE
            showDialog(TileDialog.getDialog(this, "Godly Torch",
                    "Your device is not supported."))
            tile.updateTile()
        }
        tile.updateTile()
    }

}
