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
import android.service.quicksettings.TileService
import android.os.Build
import android.service.quicksettings.Tile
import com.teamdarkness.godlytorch.Utils.Utils.askRoot
import com.teamdarkness.godlytorch.Dialog.TileDialog
import com.teamdarkness.godlytorch.R
import com.teamdarkness.godlytorch.Utils.Utils.runCommand
import com.google.firebase.analytics.FirebaseAnalytics
import android.os.Bundle
import android.util.Log
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_SELECTED_DEVICE
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_SINGLE_FILE_LOCATION
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_TILE_MASTER_NAME
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_TILE_MASTER_ON
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_TILE_MASTER_STATE
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_TILE_WHITE_ON
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_TILE_YELLOW_ON
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_TOGGLE_FILE_LOCATION
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_WHITE_FILE_LOCATION
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_YELLOW_FILE_LOCATION
import com.teamdarkness.godlytorch.Utils.Utils.readDevice
import org.jetbrains.anko.defaultSharedPreferences

@TargetApi(Build.VERSION_CODES.N)
class MasterTileService : TileService() {

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private val TILE_STATUS = "masterTileStatus"

    override fun onStartListening() {
        super.onStartListening()
        val prefs = applicationContext.defaultSharedPreferences

        // get torch file locations
        val selectedDevice = prefs.getString(PREF_SELECTED_DEVICE, "")

        // show no device selected warning
        if (selectedDevice.isEmpty()) {
            qsTile.label = "Godly Torch Unsupported"
            qsTile.updateTile()
            return
        }
        qsTile.label = prefs.getString(PREF_TILE_MASTER_NAME, "Torch")
        qsTile.state = prefs.getInt(PREF_TILE_MASTER_STATE, Tile.STATE_INACTIVE)
        qsTile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        updateTile()
    }

    private fun updateTile() {

        val prefs = applicationContext.defaultSharedPreferences

        // get torch file locations
        val selectedDevice = prefs.getString(PREF_SELECTED_DEVICE, "")

        // show no device selected warning
        if (selectedDevice.isEmpty()) {
            showDialog(TileDialog.getDialog(this, "Godly Torch",
                    "Your device is not supported yet. Open the app to request support."))
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

        // get torch file locations
        val whiteLedFileLocation = prefs.getString(PREF_WHITE_FILE_LOCATION, null)
        val yellowLedFileLocation = prefs.getString(PREF_YELLOW_FILE_LOCATION, null)
        val toggleFileLocation = prefs.getString(PREF_TOGGLE_FILE_LOCATION, null)
        val singleLedFileLocation = prefs.getString(PREF_SINGLE_FILE_LOCATION, null)

        // get max brightness
        val brightnessMax = prefs.getInt("brightnessMax", 0)

        when (prefs.getString("tileBehaviour", "1")) {
            "1" -> {
                val toggleIntensity = prefs.getString("toggleIntensity", "100")
                val currentDevice = readDevice(baseContext)

                currentDevice?.let {
                    if (currentDevice.isDualTone) {

                        if (tileStatus > 0) {
                            val command: String = String.format(getString(R.string.cmd_echo), 0, whiteLedFileLocation) +
                                    String.format(getString(R.string.cmd_echo), 0, yellowLedFileLocation) +
                                    String.format(getString(R.string.cmd_echo), 0, toggleFileLocation)

                            runCommand(command)
                            editor.putInt(TILE_STATUS, 0)
                            editor.putBoolean(PREF_TILE_MASTER_ON, false)
                            editor.putString(PREF_TILE_MASTER_NAME, "Torch")
                            editor.putInt(PREF_TILE_MASTER_STATE, Tile.STATE_INACTIVE)
                            editor.apply()
                            tile.label = "Torch"
                            tile.state = Tile.STATE_INACTIVE
                            tile.updateTile()
                        } else {
                            val torchVal = (brightnessMax * toggleIntensity.toInt()) / 100

                            val command: String = String.format(getString(R.string.cmd_echo), "0", toggleFileLocation) +
                                    getString(R.string.cmd_sleep) +
                                    String.format(getString(R.string.cmd_echo), torchVal, whiteLedFileLocation) +
                                    String.format(getString(R.string.cmd_echo), torchVal, yellowLedFileLocation) +
                                    String.format(getString(R.string.cmd_echo), brightnessMax, toggleFileLocation)

                            runCommand(command)
                            editor.putInt(TILE_STATUS, 1)
                            editor.putBoolean(PREF_TILE_MASTER_ON, true)
                            editor.putString(PREF_TILE_MASTER_NAME, "Torch")
                            editor.putInt(PREF_TILE_MASTER_STATE, Tile.STATE_ACTIVE)
                            editor.apply()
                            tile.label = "Torch"
                            tile.state = Tile.STATE_ACTIVE
                            tile.updateTile()
                        }

                    } else {

                        if (tileStatus > 0) {
                            val command: String = String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation)
                            runCommand(command)
                            editor.putInt(TILE_STATUS, 0)
                            editor.putBoolean(PREF_TILE_MASTER_ON, false)
                            editor.putString(PREF_TILE_MASTER_NAME, "Torch")
                            editor.putInt(PREF_TILE_MASTER_STATE, Tile.STATE_INACTIVE)
                            editor.apply()
                            tile.label = "Torch"
                            tile.state = Tile.STATE_INACTIVE
                            tile.updateTile()
                        } else {

                            val torchVal = (brightnessMax * toggleIntensity.toInt()) / brightnessMax
                            val command: String = String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation) +
                                    getString(R.string.cmd_sleep) +
                                    String.format(getString(R.string.cmd_echo), torchVal, singleLedFileLocation)
                            runCommand(command)
                            editor.putInt(TILE_STATUS, 1)
                            editor.putBoolean(PREF_TILE_MASTER_ON, true)
                            editor.putString(PREF_TILE_MASTER_NAME, "Torch")
                            editor.putInt(PREF_TILE_MASTER_STATE, Tile.STATE_ACTIVE)
                            editor.apply()
                            tile.label = "Torch"
                            tile.state = Tile.STATE_ACTIVE
                            tile.updateTile()
                        }
                    }
                }
            }

            "2" -> {

                val intensitySteps = prefs.getString("intensitySteps", "5")
                val currentDevice = readDevice(baseContext)
                currentDevice?.let {
                    when (tileStatus) {
                        0 -> {
                            val torchVal: Int = 100 / (intensitySteps.toInt() + 1)

                            val command = if (currentDevice.isDualTone) {
                                String.format(getString(R.string.cmd_echo), "0", toggleFileLocation) +
                                        getString(R.string.cmd_sleep) +
                                        String.format(getString(R.string.cmd_echo), torchVal, whiteLedFileLocation) +
                                        String.format(getString(R.string.cmd_echo), torchVal, yellowLedFileLocation) +
                                        String.format(getString(R.string.cmd_echo), brightnessMax, toggleFileLocation)
                            } else {
                                String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation) +
                                        getString(R.string.cmd_sleep) +
                                        String.format(getString(R.string.cmd_echo), torchVal, singleLedFileLocation)
                            }

                            runCommand(command)
                            editor.putBoolean(PREF_TILE_MASTER_ON, true)
                            editor.putInt(TILE_STATUS, 1)
                            editor.putString(PREF_TILE_MASTER_NAME, "$torchVal%")
                            editor.putInt(PREF_TILE_MASTER_STATE, Tile.STATE_ACTIVE)
                            editor.apply()
                            tile.label = "$torchVal%"
                            tile.state = Tile.STATE_ACTIVE
                            tile.updateTile()
                        }
                        1 -> {
                            val torchVal: Int = (100 / (intensitySteps.toInt() + 1)) * 2

                            val command = if (currentDevice.isDualTone) {
                                String.format(getString(R.string.cmd_echo), "0", toggleFileLocation) +
                                        getString(R.string.cmd_sleep) +
                                        String.format(getString(R.string.cmd_echo), torchVal, whiteLedFileLocation) +
                                        String.format(getString(R.string.cmd_echo), torchVal, yellowLedFileLocation) +
                                        String.format(getString(R.string.cmd_echo), brightnessMax, toggleFileLocation)
                            } else {
                                String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation) +
                                        getString(R.string.cmd_sleep) +
                                        String.format(getString(R.string.cmd_echo), torchVal, singleLedFileLocation)
                            }
                            runCommand(command)
                            editor.putBoolean(PREF_TILE_MASTER_ON, true)
                            editor.putInt(TILE_STATUS, 2)
                            editor.putString(PREF_TILE_MASTER_NAME, "$torchVal%")
                            editor.putInt(PREF_TILE_MASTER_STATE, Tile.STATE_ACTIVE)
                            editor.apply()
                            tile.label = "$torchVal%"
                            tile.state = Tile.STATE_ACTIVE
                            tile.updateTile()

                            // Log tile click to analytics
                            val bundle = Bundle()
                            bundle.putString("tile_type", "master")
                            bundle.putString("device_type", "dual_tone")
                            mFirebaseAnalytics = FirebaseAnalytics.getInstance(baseContext)
                            mFirebaseAnalytics.logEvent("tile_click", bundle)
                        }
                        2 -> {
                            var torchVal: Int = (100 / (intensitySteps.toInt() + 1)) * 3
                            if (torchVal > 90)
                                torchVal = 100

                            if (tileStatus == intensitySteps.toInt() + 1) {
                                val command = if (currentDevice.isDualTone) {
                                    String.format(getString(R.string.cmd_echo), "0", whiteLedFileLocation) +
                                            String.format(getString(R.string.cmd_echo), "0", yellowLedFileLocation) +
                                            String.format(getString(R.string.cmd_echo), "0", toggleFileLocation)
                                } else {
                                    String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation)
                                }
                                runCommand(command)
                                editor.putBoolean(PREF_TILE_MASTER_ON, false)
                                editor.putInt(TILE_STATUS, 0)
                                editor.putString(PREF_TILE_MASTER_NAME, "Torch")
                                editor.putInt(PREF_TILE_MASTER_STATE, Tile.STATE_INACTIVE)
                                editor.apply()
                                tile.label = "Torch"
                                tile.state = Tile.STATE_INACTIVE
                                tile.updateTile()
                                return
                            } else {
                                val command = if (currentDevice.isDualTone) {
                                    String.format(getString(R.string.cmd_echo), "0", toggleFileLocation) +
                                            getString(R.string.cmd_sleep) +
                                            String.format(getString(R.string.cmd_echo), torchVal, whiteLedFileLocation) +
                                            String.format(getString(R.string.cmd_echo), torchVal, yellowLedFileLocation) +
                                            String.format(getString(R.string.cmd_echo), brightnessMax, toggleFileLocation)
                                } else {
                                    String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation) +
                                            getString(R.string.cmd_sleep) +
                                            String.format(getString(R.string.cmd_echo), torchVal, singleLedFileLocation)
                                }
                                runCommand(command)
                            }
                            editor.putBoolean(PREF_TILE_MASTER_ON, true)
                            editor.putInt(TILE_STATUS, 3)
                            editor.putString(PREF_TILE_MASTER_NAME, "$torchVal%")
                            editor.putInt(PREF_TILE_MASTER_STATE, Tile.STATE_ACTIVE)
                            editor.apply()
                            tile.label = "$torchVal%"
                            tile.state = Tile.STATE_ACTIVE
                            tile.updateTile()
                        }
                        3 -> {
                            val torchVal: Int = (100 / (intensitySteps.toInt() + 1)) * 4

                            if (tileStatus == intensitySteps.toInt() + 1) {
                                val command = if (currentDevice.isDualTone) {
                                    String.format(getString(R.string.cmd_echo), "0", whiteLedFileLocation) +
                                            String.format(getString(R.string.cmd_echo), "0", yellowLedFileLocation) +
                                            String.format(getString(R.string.cmd_echo), "0", toggleFileLocation)
                                } else {
                                    String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation)
                                }
                                runCommand(command)
                                editor.putBoolean(PREF_TILE_MASTER_ON, false)
                                editor.putInt(TILE_STATUS, 0)
                                editor.putString(PREF_TILE_MASTER_NAME, "Torch")
                                editor.putInt(PREF_TILE_MASTER_STATE, Tile.STATE_INACTIVE)
                                editor.apply()
                                tile.label = "Torch"
                                tile.state = Tile.STATE_INACTIVE
                                tile.updateTile()
                                return
                            } else {
                                val command = if (currentDevice.isDualTone) {
                                    String.format(getString(R.string.cmd_echo), "0", toggleFileLocation) +
                                            getString(R.string.cmd_sleep) +
                                            String.format(getString(R.string.cmd_echo), torchVal, whiteLedFileLocation) +
                                            String.format(getString(R.string.cmd_echo), torchVal, yellowLedFileLocation) +
                                            String.format(getString(R.string.cmd_echo), brightnessMax, toggleFileLocation)
                                } else {
                                    String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation) +
                                            getString(R.string.cmd_sleep) +
                                            String.format(getString(R.string.cmd_echo), torchVal, singleLedFileLocation)
                                }
                                runCommand(command)
                            }
                            editor.putBoolean(PREF_TILE_MASTER_ON, true)
                            editor.putInt(TILE_STATUS, 4)
                            editor.putString(PREF_TILE_MASTER_NAME, "$torchVal%")
                            editor.putInt(PREF_TILE_MASTER_STATE, Tile.STATE_ACTIVE)
                            editor.apply()
                            tile.label = "$torchVal%"
                            tile.state = Tile.STATE_ACTIVE
                            tile.updateTile()
                        }
                        4 -> {
                            val torchVal: Int = (100 / (intensitySteps.toInt() + 1)) * 5

                            if (tileStatus == intensitySteps.toInt() + 1) {
                                val command = if (currentDevice.isDualTone) {
                                    String.format(getString(R.string.cmd_echo), "0", whiteLedFileLocation) +
                                            String.format(getString(R.string.cmd_echo), "0", yellowLedFileLocation) +
                                            String.format(getString(R.string.cmd_echo), "0", toggleFileLocation)
                                } else {
                                    String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation)
                                }
                                runCommand(command)
                                editor.putBoolean(PREF_TILE_MASTER_ON, false)
                                editor.putInt(TILE_STATUS, 0)
                                editor.putString(PREF_TILE_MASTER_NAME, "Torch")
                                editor.putInt(PREF_TILE_MASTER_STATE, Tile.STATE_INACTIVE)
                                editor.apply()
                                tile.label = "Torch"
                                tile.state = Tile.STATE_INACTIVE
                                tile.updateTile()
                                return
                            } else {
                                val command = if (currentDevice.isDualTone) {
                                    String.format(getString(R.string.cmd_echo), "0", toggleFileLocation) +
                                            getString(R.string.cmd_sleep) +
                                            String.format(getString(R.string.cmd_echo), torchVal, whiteLedFileLocation) +
                                            String.format(getString(R.string.cmd_echo), torchVal, yellowLedFileLocation) +
                                            String.format(getString(R.string.cmd_echo), brightnessMax, toggleFileLocation)
                                } else {
                                    String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation) +
                                            getString(R.string.cmd_sleep) +
                                            String.format(getString(R.string.cmd_echo), torchVal, singleLedFileLocation)
                                }
                                runCommand(command)
                            }
                            editor.putBoolean(PREF_TILE_MASTER_ON, true)
                            editor.putInt(TILE_STATUS, 5)
                            editor.putString(PREF_TILE_MASTER_NAME, "$torchVal%")
                            editor.putInt(PREF_TILE_MASTER_STATE, Tile.STATE_ACTIVE)
                            editor.apply()
                            tile.label = "$torchVal%"
                            tile.state = Tile.STATE_ACTIVE
                            tile.updateTile()
                        }
                        5 -> {

                            var torchVal: Int = (100 / (intensitySteps.toInt() + 1)) * 6
                            if (torchVal > 90)
                                torchVal = 100

                            if (tileStatus == intensitySteps.toInt() + 1) {
                                val command = if (currentDevice.isDualTone) {
                                    String.format(getString(R.string.cmd_echo), "0", whiteLedFileLocation) +
                                            String.format(getString(R.string.cmd_echo), "0", yellowLedFileLocation) +
                                            String.format(getString(R.string.cmd_echo), "0", toggleFileLocation)
                                } else {
                                    String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation)
                                }
                                runCommand(command)
                                editor.putBoolean(PREF_TILE_MASTER_ON, false)
                                editor.putInt(TILE_STATUS, 0)
                                editor.putString(PREF_TILE_MASTER_NAME, "Torch")
                                editor.putInt(PREF_TILE_MASTER_STATE, Tile.STATE_INACTIVE)
                                editor.apply()
                                tile.label = "Torch"
                                tile.state = Tile.STATE_INACTIVE
                                tile.updateTile()
                                return
                            } else {
                                val command = if (currentDevice.isDualTone) {
                                    String.format(getString(R.string.cmd_echo), "0", toggleFileLocation) +
                                            getString(R.string.cmd_sleep) +
                                            String.format(getString(R.string.cmd_echo), torchVal, whiteLedFileLocation) +
                                            String.format(getString(R.string.cmd_echo), torchVal, yellowLedFileLocation) +
                                            String.format(getString(R.string.cmd_echo), brightnessMax, toggleFileLocation)
                                } else {
                                    String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation) +
                                            getString(R.string.cmd_sleep) +
                                            String.format(getString(R.string.cmd_echo), torchVal, singleLedFileLocation)
                                }
                                runCommand(command)
                            }
                            editor.putBoolean(PREF_TILE_MASTER_ON, true)
                            editor.putInt(TILE_STATUS, 6)
                            editor.putString(PREF_TILE_MASTER_NAME, "$torchVal%")
                            editor.putInt(PREF_TILE_MASTER_STATE, Tile.STATE_ACTIVE)
                            editor.apply()
                            tile.label = "$torchVal%"
                            tile.state = Tile.STATE_ACTIVE
                            tile.updateTile()

                        }
                        6 -> {
                            val command = if (currentDevice.isDualTone) {
                                String.format(getString(R.string.cmd_echo), "0", whiteLedFileLocation) +
                                        String.format(getString(R.string.cmd_echo), "0", yellowLedFileLocation) +
                                        String.format(getString(R.string.cmd_echo), "0", toggleFileLocation)
                            } else {
                                String.format(getString(R.string.cmd_echo), "0", singleLedFileLocation)
                            }
                            runCommand(command)
                            editor.putBoolean(PREF_TILE_MASTER_ON, false)
                            editor.putInt(TILE_STATUS, 0)
                            editor.putString(PREF_TILE_MASTER_NAME, "Torch")
                            editor.putInt(PREF_TILE_MASTER_STATE, Tile.STATE_INACTIVE)
                            editor.apply()
                            tile.label = "Torch"
                            tile.state = Tile.STATE_INACTIVE
                            tile.updateTile()
                        }
                    }
                }
            }
        }
    }

}
