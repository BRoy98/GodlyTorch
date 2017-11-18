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

package com.teamdarkness.godlytorch.Utils

import android.content.SharedPreferences
import eu.chainfire.libsuperuser.Shell
import org.jetbrains.anko.doAsync

object Utils {

    fun askRoot(): Boolean = Shell.SU.available()

    fun runCommand(cmd: String = "") {
        doAsync {
            Shell.SU.run(cmd)
        }
    }

    fun checkSupport(mSharedPreferences: SharedPreferences): DeviceResult {

        val deviceList: ArrayList<Device> = DeviceList.getDevices()
        val deviceId = Common().getDeviceId().toLowerCase()
        val deviceProduct = android.os.Build.PRODUCT.toLowerCase()
        var deviceProductSplit = ""
        var result = DeviceResult()

        val editor = mSharedPreferences.edit()

        val prefDevice = mSharedPreferences.getString("device", null)
        val prefDeviceId = mSharedPreferences.getString("deviceId", null)
        val prefDeviceType = mSharedPreferences.getBoolean("deviceDualTone", false)

        if (prefDevice == deviceId && prefDeviceId != null) {
            result.isSupported = true
            result.deviceId = prefDeviceId
            result.isDualTone = prefDeviceType
            return result
        }

        if (deviceProduct.contains("_")) {
            val product = deviceProduct.split("_")
            deviceProductSplit = product[1]
        }

        for ((i, device) in deviceList.withIndex()) {
            when {
                device.deviceId.toLowerCase().contains(deviceId) -> {
                    editor.putString("device", device.deviceId)
                    editor.putString("deviceId", deviceId)
                    editor.putBoolean("deviceDualTone", device.isDualTone)
                    editor.apply()
                    result.isSupported = true
                    result.deviceId = device.deviceId
                    result.isDualTone = device.isDualTone
                    return result
                }
                device.deviceId.toLowerCase().contains(deviceProduct) -> {
                    editor.putString("device", device.deviceId)
                    editor.putString("deviceId", deviceProduct)
                    editor.putBoolean("deviceDualTone", device.isDualTone)
                    editor.apply()
                    result.isSupported = true
                    result.deviceId = device.deviceId
                    result.isDualTone = device.isDualTone
                    return result
                }
                deviceProductSplit.isNotEmpty() && device.deviceId.toLowerCase().contains(deviceProductSplit) -> {
                    editor.putString("device", device.deviceId)
                    editor.putString("deviceId", deviceProductSplit)
                    editor.putBoolean("deviceDualTone", device.isDualTone)
                    editor.apply()
                    result.isSupported = true
                    result.deviceId = device.deviceId
                    result.isDualTone = device.isDualTone
                    return result
                }
                i == deviceList.size - 1 -> {
                    return result
                }
                else -> {
                }
            }

        }
        return result
    }
}
