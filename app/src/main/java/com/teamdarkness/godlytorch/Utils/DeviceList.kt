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

    private var deviceList: ArrayList<Device> = java.util.ArrayList()

    fun getDevices(): ArrayList<Device> {
        deviceList.clear()
        // Redmi
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
                Device().setName("Mi 6")
                        .setDeviceId("sagit")
                        .isDualTone(true)
                        .setBrightnessMax(200)
                        .setYellowLedFileLocation("led:torch_0/brightness")
                        .setWhiteLedFileLocation("led:torch_1/brightness")
                        .setToggleFileLocation("led:switch_0/brightness")
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
                Device().setName("Mi Max")
                        .setDeviceId("hydrogen")
                        .isDualTone(true)
                        .setBrightnessMax(200)
                        .setYellowLedFileLocation("led:torch_1/brightness")
                        .setWhiteLedFileLocation("led:torch_0/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )
        deviceList.add(
                Device().setName("Mi 3S")
                        .setDeviceId("land")
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
                        .setBrightnessMax(190)
                        .setSingleLedFileLocation("flashlight/brightness")
        )
        // Motorola
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
                Device().setName("Moto G5")
                        .setDeviceId("cedric")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("led:torch_0/brightness")
                        .setWhiteLedFileLocation("led:torch_1/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )
        deviceList.add(
                Device().setName("Moto G4 Plus")
                        .setDeviceId("athene")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("led:torch_0/brightness")
                        .setWhiteLedFileLocation("led:torch_1/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )
        deviceList.add(
                Device().setName("Moto G5S Plus")
                        .setDeviceId("sanders")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("led:torch_1/brightness")
                        .setWhiteLedFileLocation("led:torch_0/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )
        // Lenovo
        deviceList.add(
                Device().setName("Lenovo P2 (kuntao)")
                        .setDeviceId("kuntao")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("led:torch_0/brightness")
                        .setWhiteLedFileLocation("led:torch_1/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )
        deviceList.add(
                Device().setName("Lenovo P2 (p2a42)")
                        .setDeviceId("p2a42")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("led:torch_0/brightness")
                        .setWhiteLedFileLocation("led:torch_1/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )
        deviceList.add(
                Device().setName("Lenovo P2 (p2c72)")
                        .setDeviceId("p2c72")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("led:torch_0/brightness")
                        .setWhiteLedFileLocation("led:torch_1/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )
        deviceList.add(
                Device().setName("Lenovo P2 (p2)")
                        .setDeviceId("p2")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("led:torch_0/brightness")
                        .setWhiteLedFileLocation("led:torch_1/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )
        deviceList.add(
                Device().setName("Lenovo K53")
                        .setDeviceId("karatep")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("led:torch_0/brightness")
                        .setWhiteLedFileLocation("led:torch_1/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )
        deviceList.add(
                Device().setName("Lenovo Vibe P1 Turbo")
                        .setDeviceId("passion")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("lenovo_torch1/brightness")
                        .setWhiteLedFileLocation("lenovo_torch0/brightness")
                        .setToggleFileLocation("dummy") // dummy because it is not required
        )
        // LeEco
        deviceList.add(
                Device().setName("LeEco Le 2")
                        .setDeviceId("le_s2")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("led:torch_0/brightness")
                        .setWhiteLedFileLocation("led:torch_1/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )
        // ZUK
        deviceList.add(
                Device().setName("ZUK Z1")
                        .setDeviceId("z1")
                        .isDualTone(false)
                        .setBrightnessMax(200)
                        .setSingleLedFileLocation("led:torch_0/brightness")
        )
        // Oneplus
        deviceList.add(
                Device().setName("Oneplus One")
                        .setDeviceId("bacon")
                        .isDualTone(false)
                        .setBrightnessMax(250)
                        .setSingleLedFileLocation("torch-light0/brightness")
        )
        deviceList.add(
                Device().setName("Oneplus 3")
                        .setDeviceId("oneplus3")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("led:torch_0/brightness")
                        .setWhiteLedFileLocation("led:torch_1/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )
        //Nexus
        deviceList.add(
                Device().setName("Nexus 5X")
                        .setDeviceId("bullhead")
                        .isDualTone(true)
                        .setBrightnessMax(255)
                        .setYellowLedFileLocation("led:torch_1/brightness")
                        .setWhiteLedFileLocation("led:torch_0/brightness")
                        .setToggleFileLocation("led:switch/brightness")
        )


        return deviceList
    }
}