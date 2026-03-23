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

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class StressService : Service() {
    companion object {
        @Volatile var isRunning = false
        var threadCount : Int = 8
    }

    suspend fun stress() {
        var number: Long = System.currentTimeMillis()
        while (StressService.isRunning) {
            if (number % 4 == 0L) {
                number = number*2
            } else if (number % 3 == 0L) {
                number = number/2
            } else if (number % 2 == 0L) {
                number = number + 1
            } else {
                number = number -1
            }
        }
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val backToMainIntent = Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }
        var notifBuilder = NotificationCompat.Builder(this, "net.tlfoxhuman.droidstress.notif.fgservice")
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
            .setContentTitle(getString(R.string.notif_fgsvc_title))
            .setContentText(getString(R.string.notif_fgsvc_desc))
            .setContentIntent(backToMainIntent)
            .setPriority(NotificationCompat.PRIORITY_MIN).build()

        startForeground(1,notifBuilder)

        // Wake lock here

        StressService.isRunning = true
        val scope = CoroutineScope(Dispatchers.Default)
        val jobs = (1..threadCount).map {
            scope.launch { stress() }
        }
        //runBlocking { jobs.joinAll() }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        StressService.isRunning = false
        super.onDestroy()
    }
    override fun onCreate() {
        super.onCreate()
    }
}