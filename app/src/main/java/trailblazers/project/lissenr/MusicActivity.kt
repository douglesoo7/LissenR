package trailblazers.project.lissenr

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import androidx.annotation.MainThread
import kotlinx.android.synthetic.main.activity_music.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import trailblazers.project.lissenr.MainActivity.Companion.musicArrayList
import trailblazers.project.lissenr.MusicService.ServiceBinder
import java.util.ArrayList


class MusicActivity : AppCompatActivity() {

    var image: Int? = null
    var song: Int? = null
    var artist: String? = null
    var songName: String? = null
    var songPosition: Int = -1
    private var musicService: MusicService? = null
//    lateinit var broadcastReceiver: BroadcastReceiver
    val handler = Handler()

    companion object {
        var songList = ArrayList<MusicModel>()
        var mediaPlayer: MediaPlayer? = null
    }


    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            val serviceBinder = binder as ServiceBinder
            musicService = serviceBinder.musicService
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)
        getIntentMethod()
        setViews()
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        seekBarChange()
        changeDurationPlayed()
    }

    private fun changeDurationPlayed() {
        this@MusicActivity.runOnUiThread(object : Runnable {
            override fun run() {
                if (mediaPlayer != null) {
                    val mCurrentPosition = mediaPlayer!!.currentPosition / 1000
                    seekBar.progress = mCurrentPosition
                    tvCurrentPlayingTime.text = formattedTime(mCurrentPosition)
                }
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun formattedTime(mCurrentPosition: Int): String {
        var totalOut = ""
        var totalNew = ""
        var seconds = (mCurrentPosition % 60).toString()
        var minutes = (mCurrentPosition / 60).toString()
        totalOut = "$minutes:$seconds"
        totalNew = "$minutes:0$seconds"
        if (seconds.length == 1) {
            return totalNew
        } else {
            return totalOut
        }
    }

    private fun seekBarChange() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer!!.seekTo(progress * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    private fun getIntentMethod() {
        songPosition = intent.getIntExtra("songPosition", -1)
        songList = musicArrayList
        if (songList != null) {
            btnPlay.text = "Pause"
            song = songList[songPosition].msong
        }
        image = intent.getIntExtra("image", 0)
        artist = intent.getStringExtra("artist")
        songName = intent.getStringExtra("songName")
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = MediaPlayer.create(applicationContext, song!!)
            mediaPlayer!!.start()
        } else {
            mediaPlayer = MediaPlayer.create(applicationContext, song!!)
            mediaPlayer!!.start()
            seekBar.max = mediaPlayer!!.duration / 1000
        }
    }

    fun setViews() {
        SongName.text = songName
        ivSongImage.setImageResource(image!!)
        if (mediaPlayer != null) {
            tvTotalPlayingTime.text = formattedTime(mediaPlayer!!.duration/1000)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
//        unregisterReceiver(broadcastReceiver)
    }
}