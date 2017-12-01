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
import android.preference.DialogPreference
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import com.teamdarkness.godlytorch.Utils.Device
import com.teamdarkness.godlytorch.Utils.DeviceList
import com.teamdarkness.godlytorch.Utils.Utils .getDevicePositionById

class SelectDevicePreference(context: Context, attrs: AttributeSet?) : DialogPreference(context, attrs) {

    private var deviceList = emptyList<Device>()

    init {
        deviceList = DeviceList.getDevices()
    }

    override fun onCreateDialogView(): View {
        val deviceView = RecyclerView(context)
        deviceView.layoutManager = LinearLayoutManager(context)
        deviceView.adapter = DeviceListAdapter(context)

        //Scroll to the selected device
        getPersistedString(null)?.let {
            deviceView.smoothScrollToPosition(getDevicePositionById(getPersistedString(null)))
        }
        return deviceView
    }
}