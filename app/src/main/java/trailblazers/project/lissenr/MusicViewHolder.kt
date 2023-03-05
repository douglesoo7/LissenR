package trailblazers.project.lissenr

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MusicViewHolder(itemView: View, private val itemClickListener: ItemClickListener) :
    RecyclerView.ViewHolder(itemView) {

    private var mName: TextView? = null
    private var mDuration: TextView? = null
    private var mImg: ImageView? = null

    private fun initData(itemView: View) {
        mName = itemView.findViewById(R.id.Sname)
        mDuration = itemView.findViewById(R.id.Sdur)
        mImg = itemView.findViewById(R.id.Img1)
    }

    fun setData(music: MusicModel, position: Int) {
        mName!!.text = music.fileName
        mDuration!!.text = music.duration.toString()
//        mImg!!.setImageResource(music.mImg)
        itemView.setOnClickListener {
            itemClickListener.onClicked(music, position)
        }
    }

    init {
        initData(itemView)
    }
}
