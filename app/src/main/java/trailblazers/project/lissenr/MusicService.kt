package trailblazers.project.lissenr

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import trailblazers.project.lissenr.MusicActivity.Companion.songList
import java.net.URI
import java.util.ArrayList
import kotlin.math.abs

class MusicService : Service(), SensorEventListener, MediaPlayer.OnCompletionListener {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var sensorManager: SensorManager
    var isSensorChangeCalled = false
    var currentlyWalking = false
    var mBinder = ServiceBinder()
    var uri: Int? = null
    var musicFiles = ArrayList<MusicModel>()
    var position: Int = -1

    override fun onCreate() {
        super.onCreate()
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var myPosition = intent?.getIntExtra("servicePosition", -1)
        if (myPosition != -1) {
            playMedia(myPosition!!)
        }
        sensorSetup()
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                if (isSensorChangeCalled) {
                    if (!currentlyWalking) {
                        currentlyWalking = true
                        Log.d("KunalService", "walking")
                        if (!isPlaying()) {
                            start()
                        }
                    }
                } else {
                    if (currentlyWalking) {
                        currentlyWalking = false
                        Log.d("KunalService", "Idle")
                        if (isPlaying()) {
                            pause()
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
            if (musicFiles != null) {
                createMediaPlayer(position)
            }
        } else {
            createMediaPlayer(position)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
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

    }

    fun createMediaPlayer(position: Int) {
        uri = musicFiles[position].msong
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
            val updown = event.values[1].toDouble()
            CoroutineScope(Dispatchers.IO).launch {
                delay(3000)
                val newSides = event.values[0].toDouble()
                val newUpdown = event.values[1].toDouble()

                isSensorChangeCalled =
                    !(abs(newSides - sides) <= 0.3 && abs(newUpdown - updown) <= 0.3)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

}