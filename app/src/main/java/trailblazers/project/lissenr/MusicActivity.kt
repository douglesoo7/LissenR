package trailblazers.project.lissenr

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_music.*
import trailblazers.project.lissenr.MusicService.ServiceBinder


class MusicActivity : AppCompatActivity() {

    var image: Int? = null
    var song: Int? = null
    var artist: String? = null
    var songName: String? = null
    private var musicService: MusicService? = null
    lateinit var broadcastReceiver: BroadcastReceiver


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
        getData()
        setViews()
        initViewsAndClickListeners()
        receiveSensorUpdates()
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }

    private fun receiveSensorUpdates() {
        Log.d("KunalActivity", "receiveSensorUpdates")
        val intentFilter = IntentFilter("trailblazers.project.lissenr")

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("KunalActivity", "msg")
                val Status = intent?.getStringExtra("status")
                if (Status.equals("Walking")) {
                    Log.d("KunalActivity", "walking")
                    if (tvStatus.visibility != View.VISIBLE)
                        tvStatus.visibility = View.VISIBLE
                } else {
                    Log.d("KunalActivity", "Idle")
                    if (tvStatus.visibility == View.VISIBLE)
                        tvStatus.visibility = View.INVISIBLE
                }
            }
        }
        registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun getData() {
        image = intent.getIntExtra("image", 0)
        artist = intent.getStringExtra("artist")
        song = intent.getIntExtra("music", 0)
        songName = intent.getStringExtra("songName")
    }

    fun setViews() {
        tvSongName.text = songName
        ivSongImage.setImageResource(image!!)
    }

    private fun initViewsAndClickListeners() {

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}