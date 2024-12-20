package com.dicoding.storyapp.upload

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Konstanta untuk ukuran maksimum file dan format nama file
private const val MAX_FILE_SIZE = 1024 * 1024 // 1 MB
private const val DATE_PATTERN = "yyyyMMdd_HHmmss"
private val currentTimestamp: String = SimpleDateFormat(DATE_PATTERN, Locale.US).format(Date())

/**
 * Membuat URI untuk menyimpan gambar berdasarkan versi Android.
 */
fun getImageUri(context: Context): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$currentTimestamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/StoryApp/")
        }
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: createPreAndroidQUri(context)
    } else {
        createPreAndroidQUri(context)
    }
}

/**
 * Membuat URI untuk perangkat dengan Android di bawah versi Q.
 */
private fun createPreAndroidQUri(context: Context): Uri {
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(storageDir, "/StoryApp/$currentTimestamp.jpg").apply {
        if (!parentFile.exists()) parentFile.mkdirs()
    }
    return Uri.fromFile(imageFile)
}

/**
 * Membuat file sementara di cache directory.
 */
fun createTemporaryFile(context: Context): File {
    val cacheDir = context.externalCacheDir
    return File.createTempFile(currentTimestamp, ".jpg", cacheDir)
}

/**
 * Mengonversi URI gambar menjadi file fisik.
 */
fun convertUriToFile(uri: Uri, context: Context): File {
    val tempFile = createTemporaryFile(context)
    context.contentResolver.openInputStream(uri).use { input ->
        FileOutputStream(tempFile).use { output ->
            input?.copyTo(output)
        }
    }
    return tempFile
}

/**
 * Mengurangi ukuran file gambar hingga sesuai dengan batas maksimum.
 */
@RequiresApi(Build.VERSION_CODES.Q)
fun File.compressImage(): File {
    val originalBitmap = BitmapFactory.decodeFile(this.path).adjustOrientation(this)
    var quality = 100
    var compressedSize: Int

    do {
        val outputStream = ByteArrayOutputStream()
        originalBitmap?.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        compressedSize = outputStream.toByteArray().size
        quality -= 5
    } while (compressedSize > MAX_FILE_SIZE && quality > 0)

    FileOutputStream(this).use { fos ->
        originalBitmap?.compress(Bitmap.CompressFormat.JPEG, quality, fos)
    }

    return this
}

/**
 * Menyesuaikan orientasi bitmap berdasarkan metadata EXIF.
 */
@RequiresApi(Build.VERSION_CODES.Q)
fun Bitmap.adjustOrientation(file: File): Bitmap {
    val exifInterface = ExifInterface(file)
    val orientation = exifInterface.getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
    )

    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(this, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(this, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(this, 270F)
        else -> this
    }
}

/**
 * Memutar bitmap sesuai sudut tertentu.
 */
fun rotateBitmap(bitmap: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(angle) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}