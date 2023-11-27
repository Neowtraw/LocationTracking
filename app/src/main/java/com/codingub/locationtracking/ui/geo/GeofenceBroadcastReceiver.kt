package com.codingub.locationtracking.ui.geo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import timber.log.Timber


class GeofenceBroadcastReceiver(
    systemAction: String,
    systemEvent: (userActivity: String) -> Unit
) : BroadcastReceiver() {

    val TAG = "GeofenceReceiver"


    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = intent?.let { GeofencingEvent.fromIntent(it) } ?: return

        if(geofencingEvent.hasError()) {
            val errorMsg =
                GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Timber.tag(TAG).e("onReceive: %s", errorMsg)
            return
        }
        val alertString = "Geofence Alert :" +
                " Trigger ${geofencingEvent.triggeringGeofences}" +
                " Transition ${geofencingEvent.geofenceTransition}"
        Timber.tag(TAG).d(alertString)
    }
}