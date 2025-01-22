package com.example.schedule.feature_schedule.presentation.schedule_menu


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.use_case.appointment.AppointmentUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val appointmentUseCases: AppointmentUseCases
) : ViewModel() {
    val appointmentByDateCache = mutableStateMapOf(
        9012025 to mutableListOf(1, 2, 3),
        10012025 to mutableListOf(4)
    )
    val appointmentCache = mutableListOf(
        Appointment(
            id = 1,
            title = "Reunião",
            notes = "Discussão sobre projetos",
            color = 0xFF0000,
            startDate = LocalDate.of(2025, 1, 21),
            endDate = LocalDate.of(2025, 1, 21),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0)
        ),
        Appointment(
            id = 2,
            title = "Consulta médica",
            notes = "Consulta geral",
            color = 0x00FF00,
            startDate = LocalDate.of(2025, 1, 22),
            endDate = LocalDate.of(2025, 1, 22),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0)
        ),
        Appointment(
            id = 3,
            title = "Jantar",
            notes = "Jantar com amigos",
            color = 0x0000FF,
            startDate = LocalDate.of(2025, 1, 23),
            endDate = LocalDate.of(2025, 1, 23),
            startTime = LocalTime.of(19, 30),
            endTime = LocalTime.of(22, 0)
        ),
        Appointment(
            id = 4,
            title = "Aniversário",
            notes = "Festa de aniversário",
            color = 0xFFFF00,
            startDate = LocalDate.of(2025, 1, 24),
            endDate = LocalDate.of(2025, 1, 24),
            startTime = LocalTime.of(18, 0),
            endTime = LocalTime.of(22, 0)
        )
    )


    private val _state = mutableStateOf(
        ScheduleState(
            appointmentList = listOf()
        )
    )
    val state: State<ScheduleState> get() = _state

    fun onEvent(event: ScheduleEvents) {
        when (event) {
            is ScheduleEvents.SelectDay -> {

            }

            is ScheduleEvents.UpdateViewingDate -> {
                _state.value = _state.value.copy(
                    currentYear = event.year,
                    currentMonth = event.month
                )
            }
        }
    }

}