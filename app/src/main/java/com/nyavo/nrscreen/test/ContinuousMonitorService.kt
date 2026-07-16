package com.nyavo.nrscreen.test

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Service de fond : observe en continu les taps ignorés (via NRAccessibilityService)
 * pour repérer une dégradation progressive de l'écran AVANT que l'utilisateur ne s'en
 * rende compte lui-même. Alerte si une zone suspecte grandit.
 * TODO: écouter les évènements transmis par NRAccessibilityService
 * TODO: notifier l'utilisateur ("ta zone morte s'agrandit, pense à sauvegarder tes données")
 * TODO: distinguer vraie dégradation vs faux positif -> voir FalsePositiveDetector
 */
class ContinuousMonitorService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // TODO: promouvoir en foreground service avec notification persistante discrète
        return START_STICKY
    }
}
