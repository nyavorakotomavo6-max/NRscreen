package com.nyavo.nrscreen.onboarding

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity

/**
 * Premier écran : demande overlay (boutons flottants) + accessibilité (gestes/boutons externes).
 * Une fois les deux accordées, lance GridTestActivity.
 */
class PermissionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: layout pixel art avec 2 boutons "Autoriser l'affichage" / "Autoriser l'accessibilité"
        checkAndRequestOverlay()
    }

    private fun checkAndRequestOverlay() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, REQ_OVERLAY)
        }
        // TODO: enchaîner avec la demande d'activation du service d'accessibilité
        // (Settings.ACTION_ACCESSIBILITY_SETTINGS, l'utilisateur doit l'activer manuellement)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_OVERLAY) {
            // TODO: vérifier, puis passer à la permission accessibilité ou au test de grille
        }
    }

    companion object {
        private const val REQ_OVERLAY = 1001
    }
}
