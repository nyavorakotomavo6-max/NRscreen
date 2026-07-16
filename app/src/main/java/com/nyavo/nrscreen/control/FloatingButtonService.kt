package com.nyavo.nrscreen.control

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Affiche des boutons flottants (overlay système) positionnés dans les zones
 * vivantes de l'écran, qui redirigent les touchers vers l'action voulue
 * (ex: bouton "Home" flottant placé loin d'une zone morte qui couvre la barre système).
 * TODO: WindowManager + LayoutParams TYPE_APPLICATION_OVERLAY
 * TODO: positionnement dynamique basé sur DeadZoneMap (éviter zones DEAD et SUSPECT)
 * TODO: style pixel art cohérent avec le reste de l'app
 */
class FloatingButtonService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}
