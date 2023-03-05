package trailblazers.project.lissenr

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import trailblazers.project.lissenr.util.AudioScanner

class MainActivity : AppCompatActivity(), ItemClickListener {

    companion object {
        var musicArrayList = ArrayList<MusicModel>()
    }

    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        createMusicList()
        buildRecyclerView()
        ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }


    private fun buildRecyclerView() {
        val musicAdopter = MusicAdapter(musicArrayList, this)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.adapter = musicAdopter
    }


    private fun createMusicList() {
        Thread {
            val scannedMusicList = AudioScanner.scanForAudio(this)
            musicArrayList.addAll(scannedMusicList)
        }.start()
    }

    override fun onClicked(music: MusicModel?, position: Int) {
        val intent = Intent(this, MusicActivity::class.java)
        intent.putExtra("contentUri", music?.contentUri)
        intent.putExtra("name", music?.fileName)
        intent.putExtra("duration", music?.duration)
        intent.putExtra("size", music?.size)
        intent.putExtra("songPosition", position)
        startActivity(intent)
    }
}
