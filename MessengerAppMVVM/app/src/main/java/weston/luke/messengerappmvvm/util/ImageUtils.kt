package weston.luke.messengerappmvvm.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import java.io.*
import java.time.LocalDateTime
import kotlin.math.sqrt

object ImageUtils {


    fun resizeImage(base64Image: String): String {
        val maxFileSize = 128000 // 128 KB in bytes

        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)

        // Check if the image is already below the maximum size
        if (decodedBytes.size <= maxFileSize) return base64Image

        val inputStream = ByteArrayInputStream(decodedBytes)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        val width = originalBitmap.width
        val height = originalBitmap.height


        // Resize image if it's too large
        val scaleFactor = sqrt(maxFileSize.toDouble() / decodedBytes.size)
        val newWidth = (width * scaleFactor).toInt()
        val newHeight = (height * scaleFactor).toInt()

        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false)

        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

        val resizedBytes = outputStream.toByteArray()
        return Base64.encodeToString(resizedBytes, Base64.DEFAULT)

    }

    fun saveImage(
        context: Context,
        base64ImageString: String,
        fullRes: Boolean = true,
        imageId: String? = null,
        overrideFileName: String? = null
    ): String {
        // Decode base64 string to Bitmap
        val decodedBytes = Base64.decode(base64ImageString, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        // Get directory for the app's private file storage
        var fileDir = context.filesDir

        var fileName = overrideFileName ?: (LocalDateTime.now().toString() + "_image")
        if (imageId != null) {
            fileName += "_$imageId"
        }

        //If not full res, save the image in a hidden folder
        if (!fullRes) {
            // Create a hidden directory inside the file directory
            val hiddenDir = File(fileDir, ".hidden_folder")
            if (!hiddenDir.exists()) {
                hiddenDir.mkdir()
            }
            fileDir = hiddenDir

            // Create a File object for the output file
            val outputFile = File(fileDir, fileName)

            // Use a FileOutputStream to write the bitmap data to the output file
            val outputStream = FileOutputStream(outputFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()

            return outputFile.absolutePath

        } else {

            return saveBase64ImageToImages(base64ImageString, context, fileName)
        }
    }

    //In android 11 and above to access the public directories you need to use media store
    private fun saveBase64ImageToImages(base64String: String, context: Context, fileName: String): String {
        val resolver = context.contentResolver

        // Convert base64 string to bytes
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)

        // Set up metadata for the file
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MessengerMVVM/")
        }

        // Create an output stream for the file using the MediaStore API
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if(uri != null) {
            val outputStream = resolver.openOutputStream(uri)

            // Write the decoded bytes to the output stream
            outputStream?.write(decodedBytes)

            // Close the output stream
            outputStream?.close()

            // Get the absolute file path to the saved image
            val filePath = getFileAbsolutePathFromUri(context, uri)

            return filePath ?: ""
        }
        return ""
    }

    private fun getFileAbsolutePathFromUri(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePath = cursor.getString(column_index)
            }
            cursor.close()
        }
        return filePath
    }







    fun deleteAllHiddenLowResImages(context: Context){
        val directory = File(context.filesDir, ".hidden_folder")

        if (directory.exists() && directory.isDirectory) {
            val files = directory.listFiles()

            if (files != null) {
                for (file in files) {
                    file.delete()
                }
            }
        }
    }


    //Check if an image needs rotating, different devices have their cameras set up differently
    fun rotateByteArrayIfRequired(byteArray: ByteArray): ByteArray {
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        val outputStream = ByteArrayOutputStream()

        val exifInterface = ExifInterface(ByteArrayInputStream(byteArray))
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }

        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()
    }

    //rotate the image
    fun rotateBitmap(bitmap: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)

        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun uriToBase64(uri: Uri, context: Context): String? {
        var inputStream: InputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            return bitmapToBase64(bitmap)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
        return null
    }
}