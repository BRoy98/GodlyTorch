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

package com.teamdarkness.godlytorch.Fragment


import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.teamdarkness.godlytorch.R
import com.teamdarkness.godlytorch.Utils.OnFragmentBackPressListener
import com.teamdarkness.godlytorch.Utils.Utils.fromHtml
import com.teamdarkness.godlytorch.Utils.Utils.getDeviceId
import com.teamdarkness.godlytorch.Utils.Utils.getDeviceName
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.teamdarkness.godlytorch.Settings.DevicelistAdapter
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_IS_DUAL_TONE
import com.teamdarkness.godlytorch.Utils.Constrains.PREF_SELECTED_DEVICE
import com.teamdarkness.godlytorch.Utils.Utils
import com.teamdarkness.godlytorch.Utils.Utils.getDeviceNameById
import com.teamdarkness.godlytorch.Utils.Utils.getSystemProp
import org.jetbrains.anko.defaultSharedPreferences


class IncompatibleFragment : Fragment(), OnFragmentBackPressListener {

    private var doubleBackToExitPressedOnce = false

    private lateinit var root: ConstraintLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_incompatible, container, false)

        val incompatibleText: TextView = view.findViewById(R.id.incompatibleText)
        val contactButton: Button = view.findViewById(R.id.contactButton)
        val btnSelectDevice: Button = view.findViewById(R.id.btnSelectDevice)
        val btnApplyDevice: Button = view.findViewById(R.id.btnApplyDevice)
        val deviceName: TextView = view.findViewById(R.id.deviceName)
        root = view.findViewById(R.id.root)

        val prefs = context?.defaultSharedPreferences

        // get torch file locations
        val selectedDevice = prefs?.getString(PREF_SELECTED_DEVICE, "")

        contactButton.setOnClickListener {
            val colors = arrayOf<CharSequence>("Email Us", "Join Telegram Community")

            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.contact_us)
            builder.setItems(colors, { dialog, which ->
                when(which) {
                    0 -> {
                        sendMail("rohan.kumquat@gmail.com")
                    }
                    1 -> {
                        openLink("https://t.me/DNDofficial")
                    }
                }
                dialog.dismiss()
            })
            builder.show()
        }

        btnSelectDevice.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Choose device")
            val deviceView = RecyclerView(context)
            deviceView.layoutManager = LinearLayoutManager(context)
            deviceView.adapter = DevicelistAdapter(selectedDevice, context)
            builder.setView(deviceView)
            builder.setNegativeButton("Cancel", { dialogInterface, _ ->
                dialogInterface.dismiss()
            })
            builder.setPositiveButton("Select", { dialogInterface, _ ->
                dialogInterface.dismiss()
                val selectedDevice = prefs?.getString(PREF_SELECTED_DEVICE, "")
                deviceName.text = getDeviceNameById(selectedDevice)
            })
            builder.show()
        }

        btnApplyDevice.setOnClickListener {
            val selectedDevice = prefs?.getString(PREF_SELECTED_DEVICE, "")

            selectedDevice?.let {
                if (selectedDevice.isNotEmpty())
                    launchKnobs()
                else {
                    if (Utils.checkSupport(context)) {
                        launchKnobs()
                    } else {
                        val fragTransaction = activity?.supportFragmentManager?.beginTransaction()
                        fragTransaction?.let {
                            fragTransaction.replace(R.id.mainFrame, IncompatibleFragment())
                            fragTransaction.commit()
                        }
                    }
                }
            }
        }

        incompatibleText.text = fromHtml(String.format(getString(R.string.incompatible_message), "${getDeviceName()} (${getDeviceId()})"))

        return view
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            activity?.finishAffinity()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(context, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    private fun sendMail(mailId: String = "") {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", mailId, null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Get help on Godly Torch")
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Write your message here...\n\n-------------------------\n" +
                "REQUIRED DEVICE DETAILS. DO NOT MODIFY!\n\n" +
                "ro.build.version.sdk: ${getSystemProp("ro.build.version.sdk")} \n" +
                "ro.build.version.release: ${getSystemProp("ro.build.version.release")} \n" +
                "ro.product.device: ${getDeviceId()} \n" +
                "ro.product.model: ${getDeviceName()} \n" +
                "android.os.Build.PRODUCT: ${android.os.Build.PRODUCT} \n")
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(Intent.createChooser(emailIntent, "Contact Us"))
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(root, "Please install an email clients and try again", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun openLink(link: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(link)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(root, "Please install a browser and try again", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun launchKnobs() {

        val prefs = context?.defaultSharedPreferences
        val isDualTone = prefs?.getBoolean(PREF_IS_DUAL_TONE, true)

        isDualTone?.let {
            if (isDualTone) {
                val fragTransaction = activity?.supportFragmentManager?.beginTransaction()
                fragTransaction?.let {
                    fragTransaction.replace(R.id.mainFrame, ThreeKnobFragment())
                    fragTransaction.commit()
                }
            } else {
                val fragTransaction = activity?.supportFragmentManager?.beginTransaction()
                fragTransaction?.let {
                    fragTransaction.replace(R.id.mainFrame, SingleKnobFragment())
                    fragTransaction.commit()
                }
            }
        }
    }

}// Required empty public constructor
