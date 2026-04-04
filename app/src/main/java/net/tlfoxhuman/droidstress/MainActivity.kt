/*
 * DroidStress
 * Copyright (C) 2026 半狐 (半透明狐人間,TlFoxHuman,TranslucentFoxHuman)
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tlfoxhuman.droidstress

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.addTextChangedListener
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    // Global variables
    private var isTextChanged: Boolean = false
    var isTimerEnabled: Boolean = false
    var timerRemain: Long = 0

    lateinit var svcobj: Intent

    private fun reloadServiceStatus() {
        //// Read current service status
        if (StressService.isRunning) {
            findViewById<Button>(R.id.startbutton).setText(R.string.stop)
            findViewById<TextView>(R.id.statusText).setText(getText(R.string.running))
        } else {
            findViewById<Button>(R.id.startbutton).setText(R.string.start)
            findViewById<TextView>(R.id.statusText).setText(getText(R.string.stopped))
        }
        isTimerEnabled = StressService.isTimerEnabled
        timerRemain = StressService.remainTimerSec
        //// Restore running threads
        findViewById<EditText>(R.id.threadsInput).setText(StressService.threadCount.toString())
        if (StressService.remainTimerMin != 0) {
            findViewById<EditText>(R.id.durationInput).setText(StressService.remainTimerMin.toString())
        }
        if(isTimerEnabled) {
            thread { timerUpdateProc() }
        }
    }

    // Notification related codes
    // Ahhhhh!! F**k Google!!! I wasted more than an hour writing code that controls the permissions of notifications and notifications itself!! Android is evil because it has a lot of destructive changes. At the same time, all object-oriented programming languages are evil. Yes, They are EVIL. I LOVE C. I HATE Kotlin.
    private val permissionReq = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),) {}
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notif_channel_name)
            val descriptionText = getString(R.string.notif_channel_description)
            val importance = NotificationManager.IMPORTANCE_NONE
            val channel = NotificationChannel("net.tlfoxhuman.droidstress.notif.fgservice", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun timerUpdateProc() {
        while (isTimerEnabled) {
            if ((System.currentTimeMillis()/1000) >= timerRemain) {
                isTimerEnabled=false
                stopService(svcobj)
                runOnUiThread {
                    findViewById<Button>(R.id.startbutton).setText(R.string.start)
                    findViewById<TextView>(R.id.statusText).setText(getText(R.string.stopped))
                }
                return
            }
            var leftsec: Long = timerRemain - (System.currentTimeMillis()/1000)
            var lefttimemin: String = (leftsec/60).toInt().toString()
            var lefttimesec_int: Int = (leftsec%60).toInt()
            var lefttimesec: String = lefttimesec_int.toString()
            var lefttimeString: String = getText(R.string.sremaining).toString()

            if (lefttimesec_int < 10) {
                lefttimesec = "0" + lefttimesec_int.toString()
            }
            runOnUiThread {
                    findViewById<TextView>(R.id.statusText).setText(lefttimeString.replace("%s",lefttimemin + ":" + lefttimesec))
            }

            Thread.sleep(1000)
        }
    }

    private fun startStress(intentobj: Intent) {
        val runMin = findViewById<EditText>(R.id.durationInput).text.toString().toIntOrNull()
        if (runMin != null && runMin != 0) {
            isTimerEnabled = true
            StressService.isTimerEnabled = true
            timerRemain = (System.currentTimeMillis()/1000) + (runMin*60)
            StressService.remainTimerSec = timerRemain
            StressService.remainTimerMin = runMin
        }
        StressService.threadCount = findViewById<EditText>(R.id.threadsInput).text.toString().toIntOrNull() ?: 8
        reloadServiceStatus()
        startService(intentobj)
        findViewById<Button>(R.id.startbutton).setText(R.string.stop)
        findViewById<TextView>(R.id.statusText).setText(getText(R.string.running))
    }

    // int main(void) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // init
        svcobj = Intent(application, StressService::class.java)

        reloadServiceStatus()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissionReq.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        createNotificationChannel()
        ////F**k Google!! Don't make unnecessary changes!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            fun isLight(): Boolean {
                val uiMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                return uiMode == Configuration.UI_MODE_NIGHT_YES
            }
            val wic = WindowInsetsControllerCompat(window, window.decorView)
            wic.isAppearanceLightStatusBars = !isLight()
        }

        findViewById<EditText>(R.id.threadsInput).setText(Runtime.getRuntime().availableProcessors().toString())


        // Button functions
        findViewById<Button>(R.id.startbutton).setOnClickListener {
            if (StressService.isRunning) {
                // Stop service
                stopService(svcobj)
                isTimerEnabled = false
                if (isTextChanged) {
                    startStress(svcobj)
                    isTextChanged = false
                } else {
                    findViewById<Button>(R.id.startbutton).setText(R.string.start)
                    findViewById<TextView>(R.id.statusText).setText(getText(R.string.stopped))
                }
            } else {
                // Start service
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    permissionReq.launch(Manifest.permission.POST_NOTIFICATIONS)
                    return@setOnClickListener
                }
                startStress(svcobj)
            }
        }
        findViewById<Button>(R.id.blankscreenButton).setOnClickListener {
            val intent = Intent(this, BlankScreenActivity::class.java)
            startActivity(intent)
        }



        findViewById<EditText>(R.id.threadsInput).addTextChangedListener {
            if (StressService.isRunning) {
                isTextChanged = true
                findViewById<Button>(R.id.startbutton).setText(R.string.restart)
            }
        }

    }

    // Menu items functions
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_about) {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mainactivity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        isTimerEnabled = false
        super.onDestroy()
    }

}