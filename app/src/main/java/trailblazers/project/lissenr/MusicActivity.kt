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


class MusicActivity : AppCompatActivity(), MediaPlayer.OnCompletionListener, MusicPlayerListener,
    ServiceConnection {

    var image: Int? = null
    var song: Int? = null
    var artist: String? = null
    var songName: String? = null
    var songPosition: Int = -1
    private var musicService: MusicService? = null
    val handler = Handler()
    var playThread: Thread? = null
    var prevThread: Thread? = null
    var nextThread: Thread? = null

    companion object {
        var songList = ArrayList<MusicModel>()
//        var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)
        getIntentMethod()

        seekBarChange()
        changeDurationPlayed()
    }

    private fun changeDurationPlayed() {
        this@MusicActivity.runOnUiThread(object : Runnable {
            override fun run() {
                if (musicService != null) {
                    val mCurrentPosition = musicService!!.getCurrentPosition() / 1000
                    seekBar.progress = mCurrentPosition
                    tvCurrentPlayingTime.text = formattedTime(mCurrentPosition)
                }
                handler.postDelayed(this, 1000)
            }

        })
    }

    override fun onResume() {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        playThreadBtn()
        prevThreadBtn()
        nextThreadBtn()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        unbindService(this)
    }

    private fun nextThreadBtn() {
        nextThread = object : Thread() {
            override fun run() {
                super.run()
                btnNext.setOnClickListener {
                    nextBtnClicked()
                }
            }
        }
        nextThread!!.start()
    }

    override fun nextBtnClicked() {
        if (musicService!!.isPlaying()) {
            musicService!!.stop()
            musicService!!.release()
            songPosition = ((songPosition + 1) % songList.size)
            song = songList[songPosition].msong
            musicService!!.createMediaPlayer(songPosition)
            songName = songList[songPosition].mName
            image = songList[songPosition].mImg
            setViews()
            seekBar.max = musicService!!.getDuration() / 1000;
            changeDurationPlayed()
            btnPlay.text = "Pause"
            musicService!!.onComplete()
            musicService!!.start()
        } else {
            musicService!!.stop()
            musicService!!.release()
            songPosition = ((songPosition + 1) % songList.size)
            song = songList[songPosition].msong
            musicService!!.createMediaPlayer(songPosition)
            songName = songList[songPosition].mName
            image = songList[songPosition].mImg
            setViews()
            seekBar.max = musicService!!.getDuration() / 1000;
            changeDurationPlayed()
            btnPlay.text = "Play"
            musicService!!.onComplete()
        }
    }

    private fun prevThreadBtn() {
        prevThread = object : Thread() {
            override fun run() {
                super.run()
                btnPrev.setOnClickListener {
                    prevBtnClicked()
                }
            }
        }
        prevThread!!.start()
    }

    override fun prevBtnClicked() {
        if (musicService!!.isPlaying()) {
            musicService!!.stop()
            musicService!!.release()
            songPosition = if (songPosition - 1 < 0) songList.size - 1 else songPosition - 1
            song = songList[songPosition].msong
            musicService!!.createMediaPlayer(songPosition)
            songName = songList[songPosition].mName
            image = songList[songPosition].mImg
            setViews()
            seekBar.max = musicService!!.getDuration() / 1000;
            changeDurationPlayed()
            btnPlay.text = "Pause"
            musicService!!.start()
            musicService!!.onComplete()
        } else {
            musicService!!.stop()
            musicService!!.release()
            songPosition = if (songPosition - 1 < 0) songList.size - 1 else songPosition - 1
            song = songList[songPosition].msong
            musicService!!.createMediaPlayer(songPosition)
            songName = songList[songPosition].mName
            image = songList[songPosition].mImg
            setViews()
            seekBar.max = musicService!!.getDuration() / 1000;
            changeDurationPlayed()
            btnPlay.text = "Play"
            musicService!!.onComplete()
        }
    }

    private fun playThreadBtn() {
        playThread = object : Thread() {
            override fun run() {
                super.run()
                btnPlay.setOnClickListener {
                    playBtnClicked()
                }
            }
        }
        playThread!!.start()
    }

    override fun playBtnClicked() {
        if (musicService!!.isPlaying()) {
            btnPlay.text = "Play"
            musicService!!.pause()
            seekBar.max = musicService!!.getDuration() / 1000;
            changeDurationPlayed()
            musicService!!.onComplete()
        } else {
            btnPlay.text = "Pause"
            musicService!!.start()
            seekBar.max = musicService!!.getDuration() / 1000;
            changeDurationPlayed()
            musicService!!.onComplete()
        }
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
                if (musicService != null && fromUser) {
                    musicService!!.seekTo(progress * 1000)
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
            song = songList[songPosition].msong
        }
        image = intent.getIntExtra("image", 0)
        artist = intent.getStringExtra("artist")
        songName = intent.getStringExtra("songName")

        val intent = Intent(this, MusicService::class.java)
        intent.putExtra("servicePosition", songPosition)
        startService(intent)
    }

    fun setViews() {
        SongName.text = songName
        tvSongName.text = songName
        ivSongImage.setImageResource(image!!)
        if (musicService != null) {
            tvTotalPlayingTime.text = formattedTime(musicService!!.getDuration() / 1000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        nextBtnClicked()
        if (musicService != null) {
            musicService!!.createMediaPlayer(songPosition)
            musicService!!.start()
            musicService!!.onComplete()
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val serviceBinder = service as MusicService.ServiceBinder
        musicService = serviceBinder.musicService
        seekBar.max = musicService!!.getDuration() / 1000
        setViews()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }
}