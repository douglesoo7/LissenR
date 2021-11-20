package trailblazers.project.lissenr

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import android.app.NotificationManager

import androidx.core.app.NotificationCompat

import android.app.NotificationChannel
import android.content.Context
import android.graphics.Color


class MusicService : Service(), SensorEventListener {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var sensorManager: SensorManager
    var isSensorChangeCalled = false
    var currentlyWalking = false

    @RequiresApi(api = Build.VERSION_CODES.O)

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer.create(this, R.raw.sunflower)
        var view=View(this@MusicService)
        val men=view.findViewById<LottieAnimationView>(R.id.animationV)
        sensorSetup()
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                if (isSensorChangeCalled) {
                    if (!currentlyWalking) {
                        currentlyWalking = true
                        val intent = Intent("trailblazers.project.lissenr")
                        intent.putExtra("status", "Walking")
                        Log.d("KunalService", "walking")
                        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                        play()
                    }
                } else {
                    if (currentlyWalking) {
                        currentlyWalking = false
                        val intent = Intent("trailblazers.project.lissenr")
                        intent.putExtra("status", "Idle")
                        Log.d("KunalService", "Idle")
                        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                        pause()

                    }
                }
            }
        }

        //notification
        Log.d("umang", "onCreate")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showNotificationAndStartForeGround()
        } else {
            startForeground(1, Notification())
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotificationAndStartForeGround() {
        val NOTIFICATION_CHANNEL_ID = "lloyd"
        val channelName = "Background Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        assert(manager != null)
        manager!!.createNotificationChannel(chan)

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification = notificationBuilder.setOngoing(true)
            .setContentTitle("App is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
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

    override fun onBind(intent: Intent): IBinder? {
        return ServiceBinder()
    }

    inner class ServiceBinder : Binder() {
        val musicService: MusicService
            get() = this@MusicService
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val sides = event.values[0].toDouble()
            val updown = event.values[1].toDouble()
            CoroutineScope(Dispatchers.IO).launch {
                delay(3000)
                val newSides = event.values[0].toDouble()
                val newUpdown = event.values[1].toDouble()

                if (abs(newSides - sides) <= 1 && abs(newUpdown - updown) <= 1) {
//                    Log.d(
//                        "Kunal", "oldside: $sides  newSide:$newSides\n" +
//                                "oldupDown: $updown  newUpdown: $newUpdown "
//                    )
                    isSensorChangeCalled = false
                } else {
                    isSensorChangeCalled = true
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    fun play() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.sunflower)
        }
        mediaPlayer!!.start()
    }

    fun pause() {
        mediaPlayer!!.pause()
    }

    fun stop() {
//        mediaPlayer!!.stop()
//        mediaPlayer!!.release()
//        mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
//        mediaPlayer!!.release()
    }
}