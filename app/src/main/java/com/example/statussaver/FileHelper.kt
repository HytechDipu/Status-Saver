package com.example.statussaver

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileOutputStream

object FileHelper {

    fun copyStatuses(context: Context, folderUri: Uri) {

        val folder = DocumentFile.fromTreeUri(context, folderUri)

        folder?.listFiles()?.forEach { document ->

            if (document.name == ".nomedia") {
                return@forEach
            }

            val input = context.contentResolver.openInputStream(document.uri)

            val saveFolder = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                ),
                "SavedStatuses"
            )

            if (!saveFolder.exists()) {
                saveFolder.mkdirs()
            }

            val outFile = File(saveFolder, document.name ?: "status")

            if (outFile.exists()) {
                return@forEach
            }

            input?.use { inputStream ->
                FileOutputStream(outFile).use { output ->
                    inputStream.copyTo(output)
                }
            }

        }

    }

}