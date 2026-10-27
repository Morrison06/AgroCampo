package pe.upeu.andinasalud.presentation.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerPacienteUseCase

sealed interface InicioUiState {
    data object Cargando : InicioUiState
    data class Contenido(val paciente: Paciente, val proximaCita: Cita?) : InicioUiState
    data class Error(val mensaje: String) : InicioUiState
}

class InicioViewModel(private val obtenerPaciente: ObtenerPacienteUseCase, private val obtenerCitas: ObtenerCitasUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow<InicioUiState>(InicioUiState.Cargando)
    val uiState: StateFlow<InicioUiState> = _uiState.asStateFlow()
    init { cargar() }
    fun cargar() = viewModelScope.launch {
        _uiState.value = InicioUiState.Cargando
        val p = async { obtenerPaciente() }.await()
        val c = async { obtenerCitas() }.await()
        if (p.isFailure || c.isFailure) _uiState.value = InicioUiState.Error("No se pudo cargar el inicio")
        else _uiState.value = InicioUiState.Contenido(p.getOrThrow(), c.getOrThrow().firstOrNull { it.estado is pe.upeu.andinasalud.domain.model.EstadoCita.Programada })
    }
}
