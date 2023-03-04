package trailblazers.project.lissenr

import android.net.Uri

data class MusicModel(
    val contentUri: Uri?,
    val fileName: String?,
    val duration: Int,
    val size: Int
)
