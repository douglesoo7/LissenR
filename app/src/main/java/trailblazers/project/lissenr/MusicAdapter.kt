package trailblazers.project.lissenr

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import trailblazers.project.lissenr.MusicViewHolder
import java.util.ArrayList

class MusicAdapter(private val musiclist: ArrayList<MusicModel>, private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<MusicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return MusicViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val music = musiclist[position]
        holder.setData(music, position)
    }

    override fun getItemCount(): Int {
        return musiclist.size
    }
}
