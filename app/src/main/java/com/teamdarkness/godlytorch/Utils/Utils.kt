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
import android.os.Build
import android.support.annotation.NonNull
import android.text.Html
import android.text.Spanned
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_DEVICE
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_DEVICE_ID
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_IS_DUAL_TONE
import eu.chainfire.libsuperuser.Shell
import org.jetbrains.anko.doAsync
import java.io.BufferedReader
import java.io.InputStreamReader

object Utils {

    fun askRoot(): Boolean = Shell.SU.available()

    fun runCommand(cmd: String = "") {
        doAsync {
            Shell.SU.run(cmd)
        }
    }

    fun checkSupport(mSharedPreferences: SharedPreferences): DeviceResult {

        val deviceList: ArrayList<Device> = DeviceList.getDevices()
        val deviceId = getDeviceId().toLowerCase()
        val deviceProduct = android.os.Build.PRODUCT.toLowerCase()
        var deviceProductSplit = ""
        var result = DeviceResult()

        val editor = mSharedPreferences.edit()

        val prefDevice = mSharedPreferences.getString(PREF_DEVICE, null)
        val prefDeviceId = mSharedPreferences.getString(PREF_DEVICE_ID, null)
        val prefDeviceType = mSharedPreferences.getBoolean(PREF_IS_DUAL_TONE, false)

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
                    editor.putString(PREF_DEVICE, device.deviceId)
                    editor.putString(PREF_DEVICE_ID, deviceId)
                    editor.putBoolean(PREF_IS_DUAL_TONE, device.isDualTone)
                    editor.apply()
                    result.isSupported = true
                    result.deviceId = device.deviceId
                    result.isDualTone = device.isDualTone
                    return result
                }
                device.deviceId.toLowerCase().contains(deviceProduct) -> {
                    editor.putString(PREF_DEVICE, device.deviceId)
                    editor.putString(PREF_DEVICE_ID, deviceProduct)
                    editor.putBoolean(PREF_IS_DUAL_TONE, device.isDualTone)
                    editor.apply()
                    result.isSupported = true
                    result.deviceId = device.deviceId
                    result.isDualTone = device.isDualTone
                    return result
                }
                deviceProductSplit.isNotEmpty() && device.deviceId.toLowerCase().contains(deviceProductSplit) -> {
                    editor.putString(PREF_DEVICE, device.deviceId)
                    editor.putString(PREF_DEVICE_ID, deviceProductSplit)
                    editor.putBoolean(PREF_IS_DUAL_TONE, device.isDualTone)
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



    fun getDeviceId(): String = getSystemProp("ro.product.device")

    fun getDeviceName(): String = getSystemProp("ro.product.model")

    fun getSystemProp(@NonNull propName: String): String {
        var line = ""
        try {
            val process: Process = Runtime.getRuntime().exec("getprop $propName")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            line = reader.readLine()
            process.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return line
    }

    @NonNull
    fun fromHtml(@NonNull source: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
        } else Html.fromHtml(source)
    }
}
