package trailblazers.project.lissenr

import android.os.Parcelable

interface MusicPlayerListener {
    fun nextBtnClicked()
    fun prevBtnClicked()
    fun playBtnClicked()
}