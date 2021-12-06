package com.shashi.locca

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.shashi.locca.preferences.UserPreferences
import com.shashi.locca.service.LocationService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    companion object{
        const val PERMISSION_ID = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        uiPart()
    }

    private fun uiPart() {
        val btn = findViewById<Button>(R.id.startService)
        btn.setOnClickListener {
            if (!checkPermission()) {
                requestPermission()

            }else{
                if (isLocationEnabled())
                {
                    if(!LocationService.isServiceStarted)
                    {
                        ContextCompat.startForegroundService(this, Intent(this, LocationService::class.java))
                    }else{
                        btn.text = "Service already running..."
                    }
                }
            }

        }
        val tv = findViewById<TextView>(R.id.textView)
        lifecycleScope.launch {

            UserPreferences.getLocation(this@MainActivity).collectLatest {

                tv.text="$it"
            }
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
        val result1 = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_ID)
    }

    // If everything is alright then
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled())
                {
                    ContextCompat.startForegroundService(this, Intent(this, LocationService::class.java))
                }else{
                    Toast.makeText(applicationContext,"Location not enabled..Enable location and then start the service.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )

        if (!gpsEnabled) {
            showAlertDialog(
                "Gps not enabled",
                message = "Please enable location service",
                posBtnText = "Enable",
                negBtnText = "Cancel",
                callback = {
                    Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG)
                        .show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            )
        }
        return gpsEnabled
    }

}