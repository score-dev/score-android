package com.team.score.Utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.team.score.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

object ImageUtil {
    fun saveBitmapToMediaStore(context: Context, bitmap: Bitmap, fileName: String = "score_feed_${System.currentTimeMillis()}.jpg"): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH, bitmap.width)
            put(MediaStore.Images.Media.HEIGHT, bitmap.height)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val contentResolver = context.contentResolver
        val imageUri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { uri ->
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(uri, contentValues, null, null)
            }
        }

        return imageUri
    }

    fun captureAndSaveToUri(view: View, context: Context): Uri? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        return saveBitmapToMediaStore(context, bitmap)
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String = "feed_capture.jpg"): File {
        val file = File(context.cacheDir, fileName)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file
    }

    fun fileToMultipart(file: File, partName: String = "file"): MultipartBody.Part {
        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestBody)
    }

    fun captureAndStoreImage(view: View, context: Context) {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        // 1. 파일로 저장
        val imageFile = saveBitmapToFile(context, bitmap)

        // 2. MultipartBody.Part 변환
        val multipart = fileToMultipart(imageFile)

        // 3. MyApplication에 저장
        MyApplication.recordFeedImage = multipart
    }

    fun setLevelProfileImage(level: Int?, targetImageView: ImageView, context: Context) {
        val drawableRes = when (level ?: 1) {
            in 0..1 -> R.drawable.img_level1
            in 2..4 -> R.drawable.img_level2
            in 5..7 -> R.drawable.img_level5
            in 8..10 -> R.drawable.img_level8
            in 11..13 -> R.drawable.img_level11
            in 14..16 -> R.drawable.img_level14
            in 17..19 -> R.drawable.img_level17
            in 20..Int.MAX_VALUE -> R.drawable.img_level20
            else -> R.drawable.img_level1
        }

        Glide.with(context).load(drawableRes).into(targetImageView)
    }


}