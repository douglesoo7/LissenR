package trailblazers.project.lissenr

import android.content.ComponentName
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.View
import android.widget.Button
import trailblazers.project.lissenr.MusicService.ServiceBinder


class MusicActivity : AppCompatActivity() {

    private var mBtnStart: Button? = null
    private var mBtnPause: Button? = null
    private var mBtnStop: Button? = null
    private var musicService: MusicService? = null


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
        initViews()
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }

    private fun initViews() {
        mBtnStart = findViewById(R.id.btnPlayBackgroundMusic)
        mBtnPause = findViewById(R.id.btnPauseBackgroundMusic)
        mBtnStop = findViewById(R.id.btnStopBackgroundMusic)
        mBtnStart?.setOnClickListener(View.OnClickListener { musicService!!.play() })
        mBtnPause?.setOnClickListener(View.OnClickListener { musicService!!.pause() })
        mBtnStop?.setOnClickListener(View.OnClickListener { musicService!!.stop() })
    }
}