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

package com.teamdarkness.godlytorch

import android.app.Application

import com.crashlytics.android.core.CrashlyticsCore

import io.fabric.sdk.android.Fabric

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val fabric = Fabric.Builder(this)
                .kits(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .debuggable(true)
                .build()
        Fabric.with(fabric)
    }
}
