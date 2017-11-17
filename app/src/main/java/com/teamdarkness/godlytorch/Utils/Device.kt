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

class Device {
    var deviceName: String = ""
    var deviceId: String = ""
    var isDualTone: Boolean = false

    var brightnessMax: Int = 0

    // Only for single tone led devices
    var singleLedFileLocation: String = ""

    // Only for dual tone led devices
    var whiteLedFileLocation: String = ""
    var yellowLedFileLocation: String = ""
    var toggleFileLocation: String = ""

    fun setName(deviceName: String = ""): Device {
        this.deviceName = deviceName
        return this
    }

    fun setDeviceId(deviceId: String = ""): Device {
        this.deviceId = deviceId
        return this
    }

    fun isDualTone(isDualTone: Boolean = false): Device {
        this.isDualTone = isDualTone
        return this
    }

    fun setBrightnessMax(brightnessMax: Int = 0): Device {
        this.brightnessMax = brightnessMax
        return this
    }

    fun setSingleLedFileLocation(singleLedFileLocation: String = ""): Device {
        this.singleLedFileLocation = singleLedFileLocation
        return this
    }

    fun setWhiteLedFileLocation(whiteLedFileLocation: String = ""): Device {
        this.whiteLedFileLocation = whiteLedFileLocation
        return this
    }

    fun setYellowLedFileLocation(yellowLedFileLocation: String = ""): Device {
        this.yellowLedFileLocation = yellowLedFileLocation
        return this
    }

    fun setToggleFileLocation(toggleFileLocation: String = ""): Device {
        this.toggleFileLocation = toggleFileLocation
        return this
    }
}
