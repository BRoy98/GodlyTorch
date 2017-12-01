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

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import com.teamdarkness.godlytorch.R
import com.teamdarkness.godlytorch.Settings.DeviceListAdapter.DeviceHolder
import com.teamdarkness.godlytorch.Utils.Constrains
import com.teamdarkness.godlytorch.Utils.Device
import com.teamdarkness.godlytorch.Utils.DeviceList
import com.teamdarkness.godlytorch.Utils.Utils
import com.teamdarkness.godlytorch.Utils.Utils.getDevicePositionById
import org.jetbrains.anko.defaultSharedPreferences

class DeviceListAdapter(val context: Context?) : RecyclerView.Adapter<DeviceHolder>() {

    override fun getItemCount(): Int {
        return DeviceList.getDevices().size
    }

    override fun onBindViewHolder(holder: DeviceHolder?, position: Int) {
        holder?.bind(DeviceList.getDevices()[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DeviceHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.select_device_layout, parent, false)
        return DeviceHolder(view)
    }

    inner class DeviceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val radioButton: RadioButton = itemView.findViewById(R.id.radioButton)
        private val deviceName: TextView = itemView.findViewById(R.id.deviceName)
        private val deviceType: TextView = itemView.findViewById(R.id.deviceType)


        fun bind(device: Device) {

            val prefs = context?.defaultSharedPreferences

            // get torch file locations
            var selectedDevice = prefs?.getString(Constrains.PREF_SELECTED_DEVICE, "")

            deviceName.text = device.deviceName
            deviceType.text = device.deviceId
            selectedDevice?.let {
                radioButton.isChecked = device.deviceId == selectedDevice
            }

            itemView.setOnClickListener {
                radioButton.performClick()
            }

            radioButton.setOnClickListener {
                selectedDevice = prefs?.getString(Constrains.PREF_SELECTED_DEVICE, "")
                val lastSelectPosition = getDevicePositionById(selectedDevice)
                selectedDevice?.let {
                    notifyItemChanged(lastSelectPosition)
                }
                Utils.selectDevice(context, device)
            }
        }
    }
}