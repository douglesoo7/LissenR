package trailblazers.project.lissenr

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import trailblazers.project.lissenr.ItemClickListener
import trailblazers.project.lissenr.MusicModel
import trailblazers.project.lissenr.R


class MusicViewHolder(itemView: View, private val itemClickListener: ItemClickListener) :
    RecyclerView.ViewHolder(itemView) {

    private var mname: TextView? = null
    private var mduration: TextView? = null
    private var mImg: ImageView? = null

    private fun initData(itemView: View) {
        mname = itemView.findViewById(R.id.Sname)
        mduration = itemView.findViewById(R.id.Sdur)
        mImg = itemView.findViewById(R.id.Img1)
    }

    fun setData(music: MusicModel, position: Int) {
        mname!!.setText(music.mName)
        mduration!!.text = music.artist
        mImg!!.setImageResource(music.mImg)
        itemView.setOnClickListener {
            itemClickListener.onclicked(music, position)
        }
    }

    init {
        initData(itemView)
    }
}