package com.teamdarkness.godlytorch

import android.content.Intent
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView

import com.chrisplus.rootmanager.RootManager

class LaunchActivity : AppCompatActivity() {

    private var logText: TextView? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        logText = findViewById(R.id.logText)
        progressBar = findViewById(R.id.progressBar)

        if (RootManager.getInstance().hasRooted()) {
            logText!!.text = getString(R.string.root_check)
            progressBar!!.visibility = View.VISIBLE

            Handler().postDelayed({
                if (RootManager.getInstance().obtainPermission()) {
                    logText!!.text = getString(R.string.root_success)

                    Handler().postDelayed({
                        val intent = Intent(this@LaunchActivity, ThreeKnobActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 1000)

                } else {
                    logText!!.text = getString(R.string.root_denied)
                    progressBar!!.visibility = View.INVISIBLE
                }
            }, 1200)

        } else {
            logText!!.text = getString(R.string.not_rooted)
            progressBar!!.visibility = View.INVISIBLE
        }
    }
}
