package trailblazers.project.lissenr

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MusicPlayerApplication : Application() {
    companion object {
        public const val CHANNEL_ID_1 = "channel1"
        public const val CHANNEL_ID_2 = "channel2"
        public const val ACTION_PREVIOUS = "actionprevious"
        public const val ACTION_NEXT = "actionnext"
        public const val ACTION_PLAY = "actionplay"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel1 = NotificationChannel(CHANNEL_ID_1, "channel(1)", NotificationManager.IMPORTANCE_HIGH)
            channel1.description = "Channel 1 Desc..."

            val channel2 = NotificationChannel(CHANNEL_ID_2, "channel(2)", NotificationManager.IMPORTANCE_HIGH)
            channel2.description = "Channel 2 Desc..."
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel1)
            notificationManager.createNotificationChannel(channel2)
        }
    }
}