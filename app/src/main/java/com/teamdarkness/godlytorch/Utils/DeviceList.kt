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

object DeviceList {

    var deviceList: ArrayList<Device> = java.util.ArrayList()

    fun getDevices(): ArrayList<Device> {
        deviceList.clear()
        deviceList.add(
                Device().setName("Mi 5")
                        .setDeviceId("gemini")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("led:torch_0/brightness")
                        .setWhiteLedFileLocation("led:torch_1/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )
        deviceList.add(
                Device().setName("Redmi Note 4")
                        .setDeviceId("mido")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("led:torch_1/brightness")
                        .setWhiteLedFileLocation("led:torch_0/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )
        deviceList.add(
                Device().setName("Mi 5s Plus")
                        .setDeviceId("natrium")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("led:torch_0/brightness")
                        .setWhiteLedFileLocation("led:torch_1/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )
        deviceList.add(
                Device().setName("Mi 5s")
                        .setDeviceId("capricon")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("led:torch_0/brightness")
                        .setWhiteLedFileLocation("led:torch_1/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )
        deviceList.add(
                Device().setName("Moto G5 Plus")
                        .setDeviceId("potter")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("led:torch_1/brightness")
                        .setWhiteLedFileLocation("led:torch_0/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )
        deviceList.add(
                Device().setName("Mi 5s")
                        .setDeviceId("cedric")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("led:torch_0/brightness")
                        .setWhiteLedFileLocation("led:torch_1/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )
        deviceList.add(
                Device().setName("Mi 5s")
                        .setDeviceId("kuntao")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("led:torch_0/brightness")
                        .setWhiteLedFileLocation("led:torch_1/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )
        deviceList.add(
                Device().setName("LeEco Le 2")
                        .setDeviceId("le_s2")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("led:torch_0/brightness")
                        .setWhiteLedFileLocation("led:torch_1/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )
        deviceList.add(
                Device().setName("Redmi Note 3")
                        .setDeviceId("kenzo")
                        .isDualTone(false)
                        .setBrightnessMax(100)
                        .setSingleLedFileLocation("flashlight/brightness")
        )

        return deviceList
    }

}