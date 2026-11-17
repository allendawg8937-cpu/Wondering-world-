package com.example.ui.studio

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val STUDIO_GALLERY_TAG = "CreativeGalleryStorage"

data class LocalStudioPhoto(
    val id: String,
    val title: String,
    val filePath: String,
    val personaId: String,
    val personaLabel: String,
    val filterId: String,
    val filterName: String,
    val frameLabel: String,
    val createdAtMillis: Long,
    val createdAtLabel: String,
    val bitmap: Bitmap? = null
)

object CreativePhotoGalleryStorage {
    private const val GALLERY_DIR_NAME = "creative_studio_gallery"
    private const val METADATA_FILE_NAME = "studio_gallery_index.json"

    private fun galleryDir(context: Context): File {
        val dir = File(context.filesDir, GALLERY_DIR_NAME)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    suspend fun loadSavedPhotos(context: Context): List<LocalStudioPhoto> = withContext(Dispatchers.IO) {
        val dir = galleryDir(context)
        val metaFile = File(dir, METADATA_FILE_NAME)
        if (!metaFile.exists()) return@withContext emptyList()

        val result = mutableListOf<LocalStudioPhoto>()
        try {
            val array = JSONArray(metaFile.readText())
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val id = obj.optString("id").takeIf { it.isNotBlank() } ?: continue
                val filePath = obj.optString("filePath").takeIf { it.isNotBlank() } ?: continue
                val file = File(filePath)
                if (!file.exists()) continue
                val bmp = BitmapFactory.decodeFile(file.absolutePath) ?: continue
                result.add(
                    LocalStudioPhoto(
                        id = id,
                        title = obj.optString("title", "Studio Portrait"),
                        filePath = filePath,
                        personaId = obj.optString("personaId", "adult"),
                        personaLabel = obj.optString("personaLabel", "Adult"),
                        filterId = obj.optString("filterId", "adult_studio_natural"),
                        filterName = obj.optString("filterName", "Studio Natural HD"),
                        frameLabel = obj.optString("frameLabel", "Clean"),
                        createdAtMillis = obj.optLong("createdAtMillis", file.lastModified()),
                        createdAtLabel = obj.optString("createdAtLabel", "Saved"),
                        bitmap = bmp
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(STUDIO_GALLERY_TAG, "Failed to load local studio gallery", e)
        }
        result.sortedByDescending { it.createdAtMillis }
    }

    suspend fun savePhotoToLocalGallery(
        context: Context,
        bitmap: Bitmap,
        title: String,
        persona: StudioPersonaStage,
        filter: PersonaStylizedFilter,
        frameStyle: StudioFilterFrameStyle
    ): LocalStudioPhoto? = withContext(Dispatchers.IO) {
        try {
            val dir = galleryDir(context)
            val now = System.currentTimeMillis()
            val id = "studio_${now}_${(100..999).random()}"
            val imageFile = File(dir, "$id.png")
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 95, out)
            }
            val timestampLabel = SimpleDateFormat("MMM d, h:mm a", Locale.US).format(Date(now))
            val safeTitle = title.trim().ifBlank { "${persona.displayName} • ${filter.name}" }
            val created = LocalStudioPhoto(
                id = id,
                title = safeTitle,
                filePath = imageFile.absolutePath,
                personaId = persona.id,
                personaLabel = persona.displayName,
                filterId = filter.id,
                filterName = filter.name,
                frameLabel = frameStyle.label,
                createdAtMillis = now,
                createdAtLabel = timestampLabel,
                bitmap = bitmap
            )
            val existing = readIndexOnly(dir).filterNot { it.id == created.id }
            writeIndex(dir, listOf(created) + existing)
            created
        } catch (e: Exception) {
            Log.e(STUDIO_GALLERY_TAG, "Failed to save photo to local gallery", e)
            null
        }
    }

    suspend fun deletePhoto(context: Context, photo: LocalStudioPhoto): Boolean = withContext(Dispatchers.IO) {
        try {
            val dir = galleryDir(context)
            val file = File(photo.filePath)
            if (file.exists()) {
                file.delete()
            }
            val remaining = readIndexOnly(dir).filterNot { it.id == photo.id }
            writeIndex(dir, remaining)
            true
        } catch (e: Exception) {
            Log.e(STUDIO_GALLERY_TAG, "Failed to delete photo from local gallery", e)
            false
        }
    }

    fun getShareableUri(context: Context, photo: LocalStudioPhoto): Uri? {
        return try {
            val file = File(photo.filePath)
            if (!file.exists()) return null
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            Log.e(STUDIO_GALLERY_TAG, "Failed to resolve FileProvider URI", e)
            null
        }
    }

    suspend fun exportToDeviceMediaStore(
        context: Context,
        bitmap: Bitmap,
        title: String
    ): Uri? = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return@withContext null
        }
        try {
            val safeName = title.replace(Regex("[^A-Za-z0-9_ -]"), "").trim()
                .ifBlank { "StagesOfLife_Studio" }
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "${safeName}_${System.currentTimeMillis()}.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/StagesOfLifeStudio")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return@withContext null
            resolver.openOutputStream(uri)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 95, out)
            }
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            uri
        } catch (e: Exception) {
            Log.e(STUDIO_GALLERY_TAG, "Failed to export photo to device MediaStore", e)
            null
        }
    }

    private fun readIndexOnly(dir: File): List<LocalStudioPhoto> {
        val metaFile = File(dir, METADATA_FILE_NAME)
        if (!metaFile.exists()) return emptyList()
        val list = mutableListOf<LocalStudioPhoto>()
        try {
            val array = JSONArray(metaFile.readText())
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val id = obj.optString("id").takeIf { it.isNotBlank() } ?: continue
                val filePath = obj.optString("filePath").takeIf { it.isNotBlank() } ?: continue
                list.add(
                    LocalStudioPhoto(
                        id = id,
                        title = obj.optString("title", "Studio Portrait"),
                        filePath = filePath,
                        personaId = obj.optString("personaId", "adult"),
                        personaLabel = obj.optString("personaLabel", "Adult"),
                        filterId = obj.optString("filterId", "adult_studio_natural"),
                        filterName = obj.optString("filterName", "Studio Natural HD"),
                        frameLabel = obj.optString("frameLabel", "Clean"),
                        createdAtMillis = obj.optLong("createdAtMillis", 0L),
                        createdAtLabel = obj.optString("createdAtLabel", "Saved"),
                        bitmap = null
                    )
                )
            }
        } catch (_: Exception) {
        }
        return list
    }

    private fun writeIndex(dir: File, items: List<LocalStudioPhoto>) {
        val array = JSONArray()
        items.forEach { item ->
            array.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("title", item.title)
                    put("filePath", item.filePath)
                    put("personaId", item.personaId)
                    put("personaLabel", item.personaLabel)
                    put("filterId", item.filterId)
                    put("filterName", item.filterName)
                    put("frameLabel", item.frameLabel)
                    put("createdAtMillis", item.createdAtMillis)
                    put("createdAtLabel", item.createdAtLabel)
                }
            )
        }
        File(dir, METADATA_FILE_NAME).writeText(array.toString())
    }
}
