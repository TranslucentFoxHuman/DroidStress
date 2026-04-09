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
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlin.concurrent.thread

class StressService : Service() {
    companion object {
        @Volatile var isRunning = false
        @Volatile var isTimerEnabled = false
        @Volatile var remainTimerSec: Long = 0
        @Volatile var remainTimerMin: Int = 0
        @Volatile var threadCount : Int = 8
        @Volatile var memTotal: Int = 0
    }

    var memPerThread:Long = 0L

    private lateinit var wakelock: PowerManager.WakeLock

    private fun stress() {
        var number: Long = System.currentTimeMillis()
        while (isRunning) {

            if (memTotal > 0) {
                //Memory Stress
                try {
                    var memStressArray = Array<Long>(memPerThread.toInt()) { 0L }
                    for (i in 0..(memPerThread - 1)) {
                        if (number % 4 == 0L) {
                            number = number * 2
                        } else if (number % 3 == 0L) {
                            number = number / 2
                        } else if (number % 2 == 0L) {
                            number = number + 1
                        } else {
                            number = number - 1
                        }
                        memStressArray[i.toInt()] = number
                    }
                    memStressArray = emptyArray()
                    System.gc()
                } catch (e: OutOfMemoryError){
                    //Fall back if VM memory is full
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
            } else {
                //CPU Stress
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
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun timercheck() {
        while (isTimerEnabled) {
            if ((System.currentTimeMillis()/1000) >= remainTimerSec) {
                isRunning = false
                isTimerEnabled = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                } else {
                    stopForeground(true)
                }
                stopSelf()
                return
            }
            Thread.sleep(1000)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if ((memTotal*1024*1024) > Runtime.getRuntime().maxMemory()) {
            memPerThread = Runtime.getRuntime().maxMemory()
        } else {
            memPerThread = (memTotal * 1024 * 1024) / threadCount.toLong()
        }

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

        wakelock = (getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"StressService::StressTestRunning")
        wakelock.acquire()


        isRunning = true
        for (i in 1..threadCount) {
            thread {stress()}
        }
        if (isTimerEnabled) {
            thread { timercheck() }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        isRunning = false
        isTimerEnabled = false
        remainTimerSec = 0
        remainTimerMin = 0
        wakelock.release()
        super.onDestroy()
    }
    override fun onCreate() {
        super.onCreate()
    }
}