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
 *     along with Godly Torch. If not, see <http://www.gnu.org/licenses/>.
 */

package com.teamdarkness.godlytorch

import android.content.ActivityNotFoundException
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.teamdarkness.godlytorch.Utils.Common
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class IncompatibleActivity : AppCompatActivity() {

    private lateinit var incompatibleText: TextView
    private lateinit var tryButton: Button
    private lateinit var root: ConstraintLayout
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_incompatible)

        val goTelegram: ImageButton = findViewById(R.id.goTelegram)
        val sendMail: ImageButton = findViewById(R.id.sendMail)
        incompatibleText = findViewById(R.id.incompatibleText)
        tryButton = findViewById(R.id.tryButton)
        root = findViewById(R.id.root)

        /*{
            Log.i("DDDADADJADJADAD", "DADDAD")
            sendMail("rohan.kumquat@gmail.com") }*/

        incompatibleText.text = Common().fromHtml(String.format(getString(R.string.incompatible_message), "${Common().getDeviceName()} (${Common().getDeviceId()})"))

        tryButton.setOnClickListener {
            val intent = Intent(this@IncompatibleActivity, ThreeKnobActivity::class.java)
            startActivity(intent)
            finish()
        }

        goTelegram.setOnClickListener {
            openLink("https://t.me/DNDofficial")
        }
        sendMail.setOnClickListener {
            Log.i("dadadada", "dada")
            sendMail("rohan.kumquat@gmail.com")
        }
    }

    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            finishAffinity()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun sendMail(mailId: String = "") {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", mailId, null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Get help on Godly Torch")
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Write your message here...\n\n-------------------------\n" +
                "REQUIRED DEVICE DETAILS. DO NOT MODIFY!\n\n" +
                "ro.build.version.sdk: ${Common().getSystemProp("ro.build.version.sdk")} \n" +
                "ro.build.version.release: ${Common().getSystemProp("ro.build.version.release")} \n" +
                "ro.product.device: ${Common().getDeviceId()} \n" +
                "ro.product.model: ${Common().getDeviceName()} \n" +
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
}
