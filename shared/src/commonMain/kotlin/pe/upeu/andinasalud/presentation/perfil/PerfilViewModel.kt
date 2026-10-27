package pe.upeu.andinasalud.presentation.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.usecase.ObtenerPacienteUseCase

sealed interface PerfilUiState { data object Cargando:PerfilUiState; data class Contenido(val paciente:Paciente):PerfilUiState; data class Error(val mensaje:String):PerfilUiState }
class PerfilViewModel(private val obtener: ObtenerPacienteUseCase): ViewModel() {
    private val _uiState=MutableStateFlow<PerfilUiState>(PerfilUiState.Cargando); val uiState:StateFlow<PerfilUiState> = _uiState.asStateFlow()
    init { cargar() }
    fun cargar()=viewModelScope.launch { obtener().fold({_uiState.value=PerfilUiState.Contenido(it)},{_uiState.value=PerfilUiState.Error(it.message?:"No se pudo cargar el perfil")}) }
}
