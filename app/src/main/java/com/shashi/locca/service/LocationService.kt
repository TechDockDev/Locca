package com.shashi.locca.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.shashi.locca.R
import com.shashi.locca.preferences.UserPreferences
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random

/**
 * @author: Shashi
 * @date : 30-11-2021
 * @description : Service for location
 **/
class LocationService  : Service() {

    companion object{
        const val NOTIFICATION_CHANNEL_ID = "LoccaID"
        var isServiceStarted = false
    }

    override fun onCreate() {
        super.onCreate()
        isServiceStarted = true
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setOngoing(true)
                .setContentText("Locca is running..")
                .setSmallIcon(R.drawable.ic_launcher_background)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.description = "Notification is running"
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
            startForeground(1, builder.build())
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val timer = Timer()
        LocationHelper().startListeningUserLocation(
            this, object : MyLocationListener {
                override fun onLocationChanged(location: Location?) {
                    //Here we will get the location
                    Log.d("LocationTAG:","Location is:${location?.latitude}, ${location?.longitude}")
                    updateLocation(location)
                }
            })
        return START_STICKY
    }

    private fun updateLocation(location: Location?) {
        CoroutineScope(Main).launch {
            val r = Random(10000).nextInt(999999)
            UserPreferences.setLocation(applicationContext,"$r:\nLat:${location?.latitude}, Lng:${location?.longitude}")
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        updateLocation(null)
        super.onDestroy()
        isServiceStarted = false
    }
}