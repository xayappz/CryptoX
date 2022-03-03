package com.xayappz.cryptox.network

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class GetCoinsWM(context: Context, parameters: WorkerParameters) :
    Worker(context, parameters) {

    override fun doWork(): Result {
        try {
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()

        }

    }


}