package trailblazers.project.lissenr

interface MusicPlayerListener {
    fun nextBtnClicked()
    fun prevBtnClicked()
    fun playBtnClicked()
    fun onCompletion()
}
