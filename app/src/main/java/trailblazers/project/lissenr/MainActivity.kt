package trailblazers.project.lissenr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class MainActivity : AppCompatActivity(), ItemClickListener {

    companion object {
        var musicArrayList = ArrayList<MusicModel>()
    }

    var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        buildData()
        buildrecyclerview()
    }


    fun buildrecyclerview() {
        val musicAdopter = MusicAdapter(musicArrayList, this)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.adapter = musicAdopter
    }


    fun buildData() {
        musicArrayList = ArrayList()
        musicArrayList.add(MusicModel("calii", "Eminem", R.drawable.wallpic, R.raw.calii))
        musicArrayList.add(MusicModel("down for you", "Raftaar", R.drawable.panda, R.raw.down))
        musicArrayList.add(MusicModel("panda", "G-Easy", R.drawable.epic, R.raw.panda))
        musicArrayList.add(MusicModel("epic", "Shawn", R.drawable.ambient, R.raw.epic))
        musicArrayList.add(MusicModel("ambient", "Ariana", R.drawable.calii, R.raw.ambient))
        musicArrayList.add(MusicModel("panda", "G-Easy", R.drawable.epic, R.raw.panda))
        musicArrayList.add(MusicModel("epic", "Shawn", R.drawable.ambient, R.raw.epic))
        musicArrayList.add(MusicModel("ambient", "Ariana", R.drawable.calii, R.raw.ambient))
    }

    override fun onclicked(music: MusicModel?, position: Int) {
        val intent = Intent(this, MusicActivity::class.java)
        intent.putExtra("music", music!!.msong)
        intent.putExtra("image", music.mImg)
        intent.putExtra("artist", music.artist)
        intent.putExtra("songName", music.mName)
        intent.putExtra("songPosition", position)
        startActivity(intent)
    }
}