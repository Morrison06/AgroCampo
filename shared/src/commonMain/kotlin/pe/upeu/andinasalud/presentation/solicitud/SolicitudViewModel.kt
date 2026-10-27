package pe.upeu.andinasalud.presentation.solicitud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.CatalogoCitas
import pe.upeu.andinasalud.domain.model.ModalidadAtencion
import pe.upeu.andinasalud.domain.usecase.ErroresSolicitud
import pe.upeu.andinasalud.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.SolicitudInvalidaException

data class FormularioSolicitud(
    val especialidad: String = "",
    val sedeId: String = "",
    val fecha: String = "",
    val hora: String = "",
    val motivo: String = "",
    val modalidad: ModalidadAtencion = ModalidadAtencion.Presencial
)

data class SolicitudUiState(
    val cargando: Boolean = true,
    val catalogo: CatalogoCitas? = null,
    val formulario: FormularioSolicitud = FormularioSolicitud(),
    val errores: ErroresSolicitud = ErroresSolicitud(),
    val enviando: Boolean = false,
    val mensaje: String? = null,
    val citaCreadaId: Long? = null
)

class SolicitudViewModel(
    private val obtenerCatalogo: ObtenerCatalogoUseCase,
    private val solicitar: SolicitarCitaUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            SolicitudUiState()
        )

    val uiState: StateFlow<SolicitudUiState> =
        _uiState.asStateFlow()

    init {
        cargarCatalogo()
    }

    fun cargarCatalogo() =
        viewModelScope.launch {

            obtenerCatalogo().fold(

                onSuccess = { catalogo ->

                    _uiState.update { state ->

                        state.copy(
                            cargando = false,
                            catalogo = catalogo
                        )
                    }
                },

                onFailure = {

                    _uiState.update { state ->

                        state.copy(
                            cargando = false,
                            mensaje =
                                "No se pudo cargar el formulario"
                        )
                    }
                }
            )
        }

    fun especialidad(
        valor: String
    ) =
        editar {
            copy(
                especialidad = valor
            )
        }

    fun sede(
        valor: String
    ) =
        editar {
            copy(
                sedeId = valor
            )
        }

    fun fecha(
        valor: String
    ) =
        editar {
            copy(
                fecha = valor
            )
        }

    fun hora(
        valor: String
    ) =
        editar {
            copy(
                hora = valor
            )
        }

    fun motivo(
        valor: String
    ) =
        editar {
            copy(
                motivo = valor
            )
        }

    fun modalidad(
        valor: ModalidadAtencion
    ) =
        editar {
            copy(
                modalidad = valor
            )
        }

    private fun editar(
        cambio:
        FormularioSolicitud.() ->
        FormularioSolicitud
    ) {

        _uiState.update { state ->

            state.copy(
                formulario =
                    state.formulario
                        .cambio(),

                errores =
                    ErroresSolicitud(),

                mensaje = null
            )
        }
    }

    fun registrar() =
        viewModelScope.launch {

            val formulario =
                _uiState
                    .value
                    .formulario

            _uiState.update { state ->

                state.copy(
                    enviando = true,
                    errores =
                        ErroresSolicitud(),
                    mensaje = null
                )
            }

            solicitar(
                formulario.especialidad,
                formulario.sedeId,
                formulario.fecha,
                formulario.hora,
                formulario.motivo,
                formulario.modalidad
            ).fold(

                onSuccess = { cita ->

                    _uiState.update { state ->

                        state.copy(
                            enviando = false,
                            mensaje =
                                "Cita solicitada correctamente",
                            citaCreadaId =
                                cita.id,
                            formulario =
                                FormularioSolicitud()
                        )
                    }
                },

                onFailure = { error ->

                    val errores =
                        (
                                error as?
                                        SolicitudInvalidaException
                                )
                            ?.errores
                            ?: ErroresSolicitud(
                                general =
                                    error.message
                                        ?: "No se pudo solicitar la cita"
                            )

                    _uiState.update { state ->

                        state.copy(
                            enviando = false,
                            errores = errores,
                            mensaje =
                                errores.general
                        )
                    }
                }
            )
        }

    fun consumirNavegacion() {

        _uiState.update { state ->

            state.copy(
                citaCreadaId = null
            )
        }
    }
}