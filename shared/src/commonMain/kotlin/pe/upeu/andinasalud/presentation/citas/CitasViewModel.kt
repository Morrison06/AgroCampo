package pe.upeu.andinasalud.presentation.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.time.Reloj
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.usecase.ReglasCita

class CitasViewModel(
    private val obtenerCitas: ObtenerCitasUseCase,
    private val reloj: Reloj
) : ViewModel() {

    private var originales: List<Cita> = emptyList()

    private val _uiState = MutableStateFlow(
        CitasUiState()
    )

    val uiState: StateFlow<CitasUiState> =
        _uiState.asStateFlow()

    init {
        cargar()
    }

    fun cargar() = viewModelScope.launch {

        _uiState.update { state ->
            state.copy(
                fase = FaseCitas.Cargando
            )
        }

        obtenerCitas().fold(

            onSuccess = { citas ->

                originales = citas

                aplicarFiltros()
            },

            onFailure = { error ->

                _uiState.update { state ->
                    state.copy(
                        fase = FaseCitas.Error(
                            error.message
                                ?: "No se pudieron cargar las citas"
                        )
                    )
                }
            }
        )
    }

    fun onBusquedaChange(
        valor: String
    ) {

        _uiState.update { state ->
            state.copy(
                busqueda = valor
            )
        }

        aplicarFiltros()
    }

    fun onFiltroChange(
        filtro: FiltroCitas
    ) {

        _uiState.update { state ->
            state.copy(
                filtro = filtro
            )
        }

        aplicarFiltros()
    }

    fun onHoyChange() {

        _uiState.update { state ->
            state.copy(
                soloHoy = !state.soloHoy
            )
        }

        aplicarFiltros()
    }

    private fun aplicarFiltros() {

        val state = _uiState.value

        val consulta =
            normalizar(
                state.busqueda
            )

        val hoy =
            reloj.ahora().fecha







        val cantidadProgramadas =
            ReglasCita.cantidadProgramadas(
                originales
            )






        val puedeSolicitar =
            !ReglasCita.limiteProgramadasAlcanzado(
                originales
            )

        val filtradas =
            originales.filter { cita ->




                val cumpleEstado =
                    when (state.filtro) {

                        FiltroCitas.Todas ->
                            true

                        FiltroCitas.Programadas ->
                            cita.estado is EstadoCita.Programada

                        FiltroCitas.Atendidas ->
                            cita.estado is EstadoCita.Atendida

                        FiltroCitas.Canceladas ->
                            cita.estado is EstadoCita.Cancelada
                    }





                val cumpleBusqueda =
                    consulta.isBlank() ||
                            normalizar(
                                cita.especialidad
                            ).contains(
                                consulta
                            ) ||
                            normalizar(
                                cita.medico.nombre
                            ).contains(
                                consulta
                            )







                val cumpleHoy =
                    !state.soloHoy ||
                            cita.fecha == hoy

                cumpleEstado &&
                        cumpleBusqueda &&
                        cumpleHoy
            }

        _uiState.value =
            state.copy(

                fase =
                    if (filtradas.isEmpty()) {

                        FaseCitas.Vacio

                    } else {

                        FaseCitas.Contenido(
                            filtradas
                        )
                    },

                cantidadProgramadas =
                    cantidadProgramadas,

                puedeSolicitar =
                    puedeSolicitar
            )
    }

    private fun normalizar(
        texto: String
    ): String {

        return texto
            .lowercase()
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
            .replace("ü", "u")
            .replace("ñ", "n")
    }
}