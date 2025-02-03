package com.example.schedule.feature_schedule.domain.use_case.appointment

import android.util.Log
import androidx.compose.ui.geometry.Offset
import com.example.schedule.feature_schedule.domain.model.Day
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.job
import org.slf4j.Logger
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChangeVisibleMonthUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "CalendarDebug"
    }

    suspend operator fun invoke(
        currentIndex: Int,
        dragAmount: Offset?,
        day: Day?,
        totalItems: Int
    ): Int {
        Log.d(TAG, "Iniciando UseCase: currentIndex = $currentIndex, totalItems = $totalItems")
        Log.d(TAG, "dragAmount = $dragAmount, day = ${day?.date}")

        var newIndex = currentIndex

        dragAmount?.let { drag ->
            val (dragX, _) = drag
            Log.d(TAG, "Detectado arrasto: dragX = $dragX")

            if (dragX > 0 && newIndex > 0) {
                newIndex--
                Log.d(TAG, "Movendo para o mês anterior: newIndex = $newIndex")
            }
            if (dragX < 0 && newIndex < totalItems - 1) {
                newIndex++
                Log.d(TAG, "Movendo para o próximo mês: newIndex = $newIndex")
            }
        }

        day?.let {
            val year = Year.now().value + (newIndex / 12) - 1
            val month = Month.entries[newIndex % 12]

            Log.d(TAG, "Verificando seleção de dia: ${it.date}")
            if (it.date.isBefore(LocalDate.of(year, month, 1))) {
                if (newIndex > 0) {
                    newIndex--
                    Log.d(
                        TAG,
                        "Selecionado dia de mês anterior, movendo para trás: newIndex = $newIndex"
                    )
                }
            } else if (it.date.isAfter(
                    LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth())
                )
            ) {
                if (newIndex < totalItems - 1) {
                    newIndex++
                    Log.d(
                        TAG,
                        "Selecionado dia de mês posterior, movendo para frente: newIndex = $newIndex"
                    )
                }
            }
        }
        Log.d(TAG, "Finalizando UseCase: newIndex = $newIndex")
        return newIndex
    }
}


