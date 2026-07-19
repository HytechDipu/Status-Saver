package com.example.statussaver

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.net.Uri

class StatusWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {

        val prefs = applicationContext.getSharedPreferences(
            "StatusSaver",
            Context.MODE_PRIVATE
        )

        val uriString = prefs.getString("status_uri", null)

        if (uriString != null) {

            FileHelper.copyStatuses(
                applicationContext,
                Uri.parse(uriString)
            )

            Log.d("STATUS_WORKER", "Statuses copied automatically")

        } else {

            Log.d("STATUS_WORKER", "No folder selected")

        }

        return Result.success()
    }

}