package com.example.statussaver

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import android.util.Log

class MainActivity : ComponentActivity() {

    private val selectedUri = mutableStateOf<Uri?>(null)

    private val folderPicker =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->

            if (uri != null) {

                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

                selectedUri.value = uri
                val prefs = getSharedPreferences("StatusSaver", MODE_PRIVATE)

                prefs.edit()
                    .putString("status_uri", uri.toString())
                    .apply()
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("StatusSaver", MODE_PRIVATE)

        prefs.getString("status_uri", null)?.let {
            selectedUri.value = Uri.parse(it)
        }

        setContent {
            var fileList by remember {
                mutableStateOf(listOf<String>())
            }

//            var folderSelected by remember {
//                mutableStateOf(false)
//            }

            Surface(modifier = Modifier.fillMaxSize()) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Button(
                        onClick = {
                            folderPicker.launch(null)
                        }
                    ) {
                        Text("Select .Statuses Folder")
                    }
                    Button(
                        onClick = {
                            if (selectedUri.value != null) {
                                FileHelper.copyStatuses(
                                    this@MainActivity,
                                    selectedUri.value!!
                                )
                            }
                        }
                    ) {
                        Text("Copy Statuses")
                    }
                    Button(
                        onClick = {

                            val workRequest =
                                PeriodicWorkRequestBuilder<StatusWorker>(
                                    15,
                                    TimeUnit.MINUTES
                                ).build()

                            WorkManager.getInstance(this@MainActivity)
                                .enqueueUniquePeriodicWork(
                                    "StatusWatcher",
                                    ExistingPeriodicWorkPolicy.KEEP,
                                    workRequest
                                )

                            Log.d("STATUS_WORKER", "Worker Scheduled")

                        }
                    ) {
                        Text("Start Watching")
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (selectedUri.value != null) {
                        Text("Folder Selected ✅")
                    } else {
                        Text("No Folder Selected")
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    fileList.forEach {
                        Text(it)
                    }

                }

            }

        }
    }
}