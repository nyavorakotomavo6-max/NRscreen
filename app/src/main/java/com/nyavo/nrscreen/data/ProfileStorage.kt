package com.nyavo.nrscreen.data

import android.content.Context
import com.nyavo.nrscreen.test.DeadZoneMap

/**
 * Sauvegarde locale des profils (carte de zones mortes, historique d'usage,
 * préférences). 100% offline : stockage interne de l'app, aucun serveur.
 * TODO: sérialisation JSON simple (org.json, déjà inclus dans Android, zéro dépendance)
 * TODO: format exportable pour le partage entre device (QR code / Bluetooth,
 *       utile entre deux personnes ayant une casse similaire sur le même modèle)
 */
class ProfileStorage(context: Context) {

    private val prefs = context.getSharedPreferences("nrscreen_profile", Context.MODE_PRIVATE)

    fun saveMap(map: DeadZoneMap) {
        // TODO: sérialiser map en JSON et stocker dans prefs ou un fichier interne
    }

    fun loadMap(): DeadZoneMap? {
        // TODO: désérialiser depuis le stockage
        return null
    }

    fun exportToFile(map: DeadZoneMap, targetPath: String) {
        // TODO: écrire un fichier .json partageable (profil de zones mortes)
    }
}
