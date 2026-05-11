package com.forwardex.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.forwardex.domain.SimRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RuleRecoveryWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val simRepository: SimRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        simRepository.refreshProfiles()
        return Result.success()
    }
}
