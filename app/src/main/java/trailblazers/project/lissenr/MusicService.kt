package trailblazers.project.lissenr

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import trailblazers.project.lissenr.MusicActivity.Companion.songList
import kotlin.math.abs

class MusicService : Service(), SensorEventListener, MediaPlayer.OnCompletionListener {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var sensorManager: SensorManager
    private var isSensorChangeCalled = false
    private var currentlyWalking = false
    private var uri: Uri? = null
    private var musicFiles = ArrayList<MusicModel>()
    private var position: Int = -1
    private var trackingEnabled = false
    private var musicPlayerListener: MusicPlayerListener? = null

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showNotificationAndStartForeGround()
        } else {
            startForeground(1, Notification())
        }

//        sensorSetup()
//        CoroutineScope(Dispatchers.IO).launch {
//            while (true) {
//                if (isSensorChangeCalled) {
//                    if (!currentlyWalking) {
//                        currentlyWalking = true
//                        val intent = Intent("trailblazers.project.lissenr")
//                        intent.putExtra("status", "Walking")
//                        Log.d("KunalService", "walking")
//                        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
//                        start()
//                    }
//                } else {
//                    if (currentlyWalking) {
//                        currentlyWalking = false
//                        val intent = Intent("trailblazers.project.lissenr")
//                        intent.putExtra("status", "Idle")
//                        Log.d("KunalService", "Idle")
//                        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
//                        pause()
//                    }
//                }
//            }
//        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotificationAndStartForeGround() {

        BitmapFactory.decodeResource(resources, R.drawable.wallpic)

        val NOTIFICATION_CHANNEL_ID = "lloyd"
        val channelName = "Background Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan)
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification = notificationBuilder.setOngoing(true)
            .setContentTitle("App is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val myPosition = intent?.getIntExtra("servicePosition", -1)
        if (myPosition != -1) {
            playMedia(myPosition!!)
        }
        sensorSetup()
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                if (trackingEnabled) {
                    if (isSensorChangeCalled) {
                        if (!currentlyWalking) {
                            currentlyWalking = true
                            if (!isPlaying()) {
                                start()
                            }
                        }
                    } else {
                        if (currentlyWalking) {
                            currentlyWalking = false
                            if (isPlaying()) {
                                pause()
                            }
                        }
                    }
                }
            }
        }
        return START_STICKY
    }

    private fun playMedia(startPosition: Int) {
        musicFiles = songList
        position = startPosition
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            createMediaPlayer(position)
        } else {
            createMediaPlayer(position)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return ServiceBinder()
    }

    inner class ServiceBinder : Binder() {
        val musicService: MusicService
            get() = this@MusicService
    }

    fun start() {
        mediaPlayer!!.start()
    }

    fun pause() {
        mediaPlayer!!.pause()
    }

    fun isPlaying(): Boolean {
        return mediaPlayer!!.isPlaying
    }

    fun stop() {
        mediaPlayer!!.stop()
    }

    fun release() {
        mediaPlayer!!.release()
    }

    fun getDuration(): Int {
        return mediaPlayer!!.duration
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer!!.currentPosition
    }

    fun seekTo(position: Int) {
        mediaPlayer!!.seekTo(position)
    }

    fun onComplete() {
        mediaPlayer!!.setOnCompletionListener(this)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        musicPlayerListener?.run {
            onCompletion()
        }
    }

    fun createMediaPlayer(position: Int) {
        uri = musicFiles[position].contentUri
        mediaPlayer = MediaPlayer.create(baseContext, uri!!)
    }


    private fun sensorSetup() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this, it, SensorManager.SENSOR_DELAY_FASTEST, SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val sides = event.values[0].toDouble()
            val upDown = event.values[1].toDouble()
            CoroutineScope(Dispatchers.IO).launch {
                delay(3000)
                val newSides = event.values[0].toDouble()
                val newUpDown = event.values[1].toDouble()
                isSensorChangeCalled =
                    !(abs(newSides - sides) <= 0.3 && abs(newUpDown - upDown) <= 0.3)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    fun trackingEnabled() {
        trackingEnabled = true
    }

    fun trackingDisabled() {
        trackingEnabled = false
    }

    fun setCallBack(musicPlayerListener: MusicPlayerListener) {
        this.musicPlayerListener = musicPlayerListener
    }
}
