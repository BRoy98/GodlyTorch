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
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.teamdarkness.godlytorch.Utils

import java.io.BufferedReader
import java.io.InputStreamReader
import android.text.Html
import android.os.Build
import android.support.annotation.NonNull
import android.text.Spanned

class Common {

    fun getDeviceId(): String = getSystemProp("ro.product.device")

    fun getDeviceName(): String = getSystemProp("ro.product.model")

    fun getSystemProp(@NonNull propName: String): String {
        var line = ""
        try {
            val process: Process = Runtime.getRuntime().exec("getprop $propName")
            val reader = BufferedReader(InputStreamReader(process.getInputStream()))
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
        } else {
            Html.fromHtml(source)
        }
    }

}