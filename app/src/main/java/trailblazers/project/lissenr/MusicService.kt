package trailblazers.project.lissenr

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val TAG = javaClass.name

    override fun onCreate() {
        mediaPlayer = MediaPlayer.create(this, R.raw.sunflower)
        Log.d(TAG, "onCreate: ")
        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder? {
        return ServiceBinder()
    }

    fun play() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.sunflower)
        }
        mediaPlayer!!.start()
        Log.d(TAG, "play: ")
    }

    fun pause() {
        mediaPlayer!!.pause()
        Log.d(TAG, "pause: ")
    }

    fun stop() {
        mediaPlayer!!.stop()
        mediaPlayer!!.release()
        mediaPlayer = null
        Log.d(TAG, "stop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer!!.release()
        Log.d(TAG, "onDestroy: ")
    }

    inner class ServiceBinder : Binder() {
        val musicService: MusicService
            get() = this@MusicService
    }
}