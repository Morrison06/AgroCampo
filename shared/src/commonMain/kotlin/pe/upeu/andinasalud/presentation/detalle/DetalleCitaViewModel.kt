package pe.upeu.andinasalud.presentation.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ErroresReprogramacion
import pe.upeu.andinasalud.domain.usecase.ObtenerDetalleCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ReprogramacionInvalidaException
import pe.upeu.andinasalud.domain.usecase.ReprogramarCitaUseCase

sealed interface DetalleUiState {

    data object Cargando : DetalleUiState

    data class Contenido(
        val cita: Cita,
        val mensaje: String? = null,
        val fechaReprogramacion: String = "",
        val horaReprogramacion: String = "",
        val erroresReprogramacion: ErroresReprogramacion =
            ErroresReprogramacion(),
        val reprogramando: Boolean = false
    ) : DetalleUiState

    data class Error(
        val mensaje: String
    ) : DetalleUiState
}

class DetalleCitaViewModel(
    private val obtener: ObtenerDetalleCitaUseCase,
    private val cancelar: CancelarCitaUseCase,
    private val reprogramar: ReprogramarCitaUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<DetalleUiState>(
            DetalleUiState.Cargando
        )

    val uiState: StateFlow<DetalleUiState> =
        _uiState.asStateFlow()

    private var idActual: Long? = null

    fun cargar(id: Long) {

        if (
            idActual == id &&
            _uiState.value is DetalleUiState.Contenido
        ) {
            return
        }

        idActual = id
        recargar()
    }

    fun recargar() =
        viewModelScope.launch {

            val id =
                idActual
                    ?: return@launch

            _uiState.value =
                DetalleUiState.Cargando

            obtener(id).fold(

                onSuccess = {

                    _uiState.value =
                        DetalleUiState.Contenido(
                            cita = it
                        )
                },

                onFailure = {

                    _uiState.value =
                        DetalleUiState.Error(
                            it.message
                                ?: "No se pudo cargar la cita"
                        )
                }
            )
        }

    fun cambiarFechaReprogramacion(
        valor: String
    ) {

        val actual =
            _uiState.value as?
                    DetalleUiState.Contenido
                ?: return

        _uiState.value =
            actual.copy(
                fechaReprogramacion = valor,
                erroresReprogramacion =
                    actual
                        .erroresReprogramacion
                        .copy(
                            fecha = null,
                            general = null
                        ),
                mensaje = null
            )
    }

    fun cambiarHoraReprogramacion(
        valor: String
    ) {

        val actual =
            _uiState.value as?
                    DetalleUiState.Contenido
                ?: return

        _uiState.value =
            actual.copy(
                horaReprogramacion = valor,
                erroresReprogramacion =
                    actual
                        .erroresReprogramacion
                        .copy(
                            hora = null,
                            general = null
                        ),
                mensaje = null
            )
    }

    fun limpiarFormularioReprogramacion() {

        val actual =
            _uiState.value as?
                    DetalleUiState.Contenido
                ?: return

        _uiState.value =
            actual.copy(
                fechaReprogramacion = "",
                horaReprogramacion = "",
                erroresReprogramacion =
                    ErroresReprogramacion()
            )
    }

    fun reprogramar() =
        viewModelScope.launch {

            val id =
                idActual
                    ?: return@launch

            val actual =
                _uiState.value as?
                        DetalleUiState.Contenido
                    ?: return@launch

            _uiState.value =
                actual.copy(
                    reprogramando = true,
                    mensaje = null,
                    erroresReprogramacion =
                        ErroresReprogramacion()
                )

            reprogramar(
                citaId = id,
                fechaTexto =
                    actual.fechaReprogramacion,
                horaTexto =
                    actual.horaReprogramacion
            ).fold(

                onSuccess = {

                    obtener(id).fold(

                        onSuccess = { citaActualizada ->

                            _uiState.value =
                                DetalleUiState.Contenido(
                                    cita =
                                        citaActualizada,
                                    mensaje =
                                        "Cita reprogramada correctamente"
                                )
                        },

                        onFailure = { error ->

                            _uiState.value =
                                DetalleUiState.Error(
                                    error.message
                                        ?: "No se pudo recargar la cita"
                                )
                        }
                    )
                },

                onFailure = { error ->

                    val errores =
                        (
                                error as?
                                        ReprogramacionInvalidaException
                                )
                            ?.errores
                            ?: ErroresReprogramacion(
                                general =
                                    error.message
                                        ?: "No se pudo reprogramar la cita"
                            )

                    val estadoActual =
                        _uiState.value as?
                                DetalleUiState.Contenido

                    if (estadoActual != null) {

                        _uiState.value =
                            estadoActual.copy(
                                reprogramando = false,
                                erroresReprogramacion =
                                    errores,
                                mensaje =
                                    errores.general
                            )
                    }
                }
            )
        }

    fun cancelar() =
        viewModelScope.launch {

            val id =
                idActual
                    ?: return@launch

            cancelar(id).fold(

                onSuccess = {

                    obtener(id).onSuccess {

                        _uiState.value =
                            DetalleUiState.Contenido(
                                cita = it,
                                mensaje =
                                    "Cita cancelada correctamente"
                            )
                    }
                },

                onFailure = {

                    val actual =
                        (
                                _uiState.value as?
                                        DetalleUiState.Contenido
                                )?.cita

                    if (actual != null) {

                        _uiState.value =
                            DetalleUiState.Contenido(
                                cita = actual,
                                mensaje = it.message
                            )
                    }
                }
            )
        }
}