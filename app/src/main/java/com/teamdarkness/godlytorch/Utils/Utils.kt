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

import android.content.Context
import android.os.Build
import android.support.annotation.NonNull
import android.text.Html
import android.text.Spanned
import android.util.Log
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_BRIGHTNESS_MAX
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_IS_DUAL_TONE
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_SELECTED_DEVICE
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_SINGLE_FILE_LOCATION
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_TOGGLE_FILE_LOCATION
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_WHITE_FILE_LOCATION
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_YELLOW_FILE_LOCATION
import eu.chainfire.libsuperuser.Shell
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.doAsync
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_USE_INTERNAL_BUSYBOX
import github.nisrulz.easydeviceinfo.base.EasyDeviceMod


object Utils {

    fun askRoot(): Boolean = Shell.SU.available()

    fun runCommand(cmd: String = "") {
        doAsync {
            Shell.SU.run(cmd)
        }
    }

    private fun readMaxBrightness(ledFile: String = "", context: Context?): String {
        val defPref = context?.defaultSharedPreferences
        var maxBr = ""
        var maxBrightnessList = Shell.SU.run("cat /sys/class/leds/${ledFile.replace("brightness", "max_brightness")}")
        for (value in maxBrightnessList) {
            maxBr = value
            return maxBr
        }
        // Use internal busybox because busybox is not found
        if (maxBr.isEmpty()) {
            copyBusyBox(context)
            val prefEditor = defPref?.edit()
            prefEditor?.putBoolean(PREF_USE_INTERNAL_BUSYBOX, true)
            prefEditor?.apply()
            maxBrightnessList = Shell.SU.run("${context?.filesDir?.absolutePath}/busybox cat /sys/class/leds/${ledFile.replace("brightness", "max_brightness")}")

            for (value in maxBrightnessList) {
                maxBr = value
                return maxBr
            }
        }
        return ""
    }

    // Check if device is supported
    fun checkSupport(context: Context?): Boolean {
        val deviceList: ArrayList<Device> = DeviceList.getDevices()

        val easyDeviceMod = EasyDeviceMod(context)
        val deviceId = easyDeviceMod.device.toLowerCase()

        for ((i, device) in deviceList.withIndex()) {
            when {
                device.deviceId.toLowerCase().contains(deviceId) -> {
                    selectDevice(context, device)
                    return true
                }
                i == deviceList.size - 1 -> {
                    return false
                }
                else -> {
                }
            }
        }


        //val deviceId = getDeviceId().toLowerCase()
        /*val deviceProduct = android.os.Build.PRODUCT.toLowerCase()
        var deviceProductSplit = ""

        if (deviceProduct.contains("_")) {
            val product = deviceProduct.split("_")
            deviceProductSplit = product[1]
        }

        for ((i, device) in deviceList.withIndex()) {
            when {
                device.deviceId.toLowerCase().contains(deviceId) -> {
                    selectDevice(context, device)
                    return true
                }
                device.deviceId.toLowerCase().contains(deviceProduct) -> {
                    selectDevice(context, device)
                    return true
                }
                deviceProductSplit.isNotEmpty() && device.deviceId.toLowerCase().contains(deviceProductSplit) -> {
                    selectDevice(context, device)
                    return true
                }
                i == deviceList.size - 1 -> {
                    return false
                }
                else -> {
                }
            }
        }*/
        return false
    }

    fun readDevice(context: Context): Device? {
        val device = Device()
        val defPref = context.defaultSharedPreferences

        defPref.getString(PREF_SELECTED_DEVICE, null)?.let {
            device.deviceId = defPref.getString(PREF_SELECTED_DEVICE, "")
            device.isDualTone = defPref.getBoolean(PREF_IS_DUAL_TONE, false)
            device.whiteLedFileLocation = defPref.getString(PREF_WHITE_FILE_LOCATION, "")
            device.yellowLedFileLocation = defPref.getString(PREF_YELLOW_FILE_LOCATION, "")
            device.toggleFileLocation = defPref.getString(PREF_TOGGLE_FILE_LOCATION, "")
            device.singleLedFileLocation = defPref.getString(PREF_SINGLE_FILE_LOCATION, "")
            return device
        }
        return null
    }

    fun selectDevice(context: Context?, device: Device?): Boolean {
        device?.let {
            val defPref = context?.defaultSharedPreferences

            defPref?.let {

                try {
                    val prefEditor = defPref.edit()
                    prefEditor.putString(PREF_SELECTED_DEVICE, device.deviceId)
                    prefEditor.putBoolean(PREF_IS_DUAL_TONE, device.isDualTone)
                    if (device.isDualTone) {
                        prefEditor.putString(PREF_WHITE_FILE_LOCATION, device.whiteLedFileLocation)
                        prefEditor.putString(PREF_YELLOW_FILE_LOCATION, device.yellowLedFileLocation)
                        prefEditor.putString(PREF_TOGGLE_FILE_LOCATION, device.toggleFileLocation)
                        prefEditor.putInt(PREF_BRIGHTNESS_MAX, readMaxBrightness(device.whiteLedFileLocation, context).toInt())
                    } else {
                        prefEditor.putString(PREF_SINGLE_FILE_LOCATION, device.singleLedFileLocation)
                        prefEditor.putInt(PREF_BRIGHTNESS_MAX, readMaxBrightness(device.singleLedFileLocation, context).toInt())
                        prefEditor.putString(PREF_WHITE_FILE_LOCATION, "")
                        prefEditor.putString(PREF_YELLOW_FILE_LOCATION, "")
                        prefEditor.putString(PREF_TOGGLE_FILE_LOCATION, "")
                    }
                    prefEditor.apply()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }
            }
        }
        return true
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

    fun getSingleFileLocationById(deviceId: String): String? {
        return DeviceList.getDevices()
                .firstOrNull { it.deviceId == deviceId }
                ?.singleLedFileLocation
    }

    fun getWhiteLedFileLocationById(deviceId: String): String? {
        return DeviceList.getDevices()
                .firstOrNull { it.deviceId == deviceId }
                ?.whiteLedFileLocation
    }

    fun getYellowLedFileLocationById(deviceId: String): String? {
        return DeviceList.getDevices()
                .firstOrNull { it.deviceId == deviceId }
                ?.yellowLedFileLocation
    }

    fun getToggleFileLocationById(deviceId: String): String? {
        return DeviceList.getDevices()
                .firstOrNull { it.deviceId == deviceId }
                ?.toggleFileLocation
    }

    fun getDeviceNameById(deviceId: String?): String {
        return DeviceList.getDevices()
                .firstOrNull { it.deviceId == deviceId }
                ?.deviceName
                ?: ""
    }

    fun getDevicePositionById(deviceId: String?): Int {
        for ((i, dev) in DeviceList.getDevices().withIndex()) {
            if (dev.deviceId == deviceId)
                return i
        }
        return 0
    }

    fun getDeviceById(deviceId: String): Device? {
        return DeviceList.getDevices().firstOrNull { it.deviceId == deviceId }
    }


    fun copyBusyBox(context: Context?) {
        context?.let {
            val assetManager = context.assets
            val inStream: InputStream?
            val out: OutputStream?
            try {
                inStream = assetManager.open("binary/busybox") //binary/busybox

                val outDir = context.filesDir?.absolutePath

                val outFile = File(outDir, "busybox")

                out = FileOutputStream(outFile)
                copyFile(inStream, out, context)
            } catch (e: Exception) {
                Log.e("Utils", "Failed to copy asset file", e)
            }
        }
    }

    @Throws(IOException::class)
    private fun copyFile(inStream: InputStream, out: OutputStream, context: Context?) {
        try {
            val buffer = ByteArray(1024)
            var read = inStream.read(buffer)
            while (read != -1) {
                out.write(buffer, 0, read)
                read = inStream.read(buffer)
            }
            val file = File(context?.filesDir?.absolutePath + "/busybox")
            file.setExecutable(true)
            out.flush()
            out.close()
            inStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
