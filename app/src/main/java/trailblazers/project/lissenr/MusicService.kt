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
import android.view.ViewGroup
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

class MusicService : Service(), SensorEventListener {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var sensorManager: SensorManager
    var isSensorChangeCalled = false
    var currentlyWalking = false

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

                if (abs(newSides - sides) <= .2 && abs(newUpdown - updown) <= .2) {
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