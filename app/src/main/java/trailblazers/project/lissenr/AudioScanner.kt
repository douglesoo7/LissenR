package trailblazers.project.lissenr

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.util.concurrent.TimeUnit

object AudioScanner {

    private val collection = getAudioUri()

    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.SIZE
    )

    // Show only Audios that are at least 30 seconds in duration.
    private const val selection = "${MediaStore.Audio.Media.DURATION} >= ?"
    private val selectionArgs = arrayOf(
        TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS).toString()
    )

    // Display Audios in alphabetical order based on their display name.
    private const val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

    fun scanForAudio(context: Context): List<MusicModel> {
        val musicList = mutableListOf<MusicModel>()
        val query = context.applicationContext.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val columnId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val columnName = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val columnDuration = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val columnSize = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(columnId)
                val name = cursor.getString(columnName)
                val duration = cursor.getInt(columnDuration)
                val size = cursor.getInt(columnSize)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                musicList += MusicModel(contentUri, name, duration, size)
            }
        }
        return musicList
    }

    private fun getAudioUri(): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
    }
}
