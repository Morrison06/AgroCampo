package pe.upeu.andinasalud.presentation.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.ModalidadAtencion
import pe.upeu.andinasalud.presentation.components.*

@Composable
fun DetalleCitaScreen(
    id: Long,
    viewModel: DetalleCitaViewModel
) {

    LaunchedEffect(id) {
        viewModel.cargar(id)
    }

    val state by viewModel.uiState.collectAsState()

    var confirmarCancelacion by remember {
        mutableStateOf(false)
    }

    var mostrarReprogramacion by remember {
        mutableStateOf(false)
    }

    when (val s = state) {

        DetalleUiState.Cargando -> {

            EstadoCarga(
                "Cargando detalle…"
            )
        }

        is DetalleUiState.Error -> {

            EstadoError(
                s.mensaje,
                viewModel::recargar
            )
        }

        is DetalleUiState.Contenido -> {

            val cita = s.cita

            LaunchedEffect(s.mensaje) {

                if (
                    s.mensaje ==
                    "Cita reprogramada correctamente"
                ) {
                    mostrarReprogramacion = false
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(20.dp),
                verticalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    text = cita.especialidad,
                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall
                )

                Text(
                    text =
                        "Médico: ${cita.medico.nombre}"
                )

                Text(
                    text =
                        "Sede: ${cita.sede.nombre}"
                )

                Text(
                    text =
                        "Fecha: ${cita.fecha.formatoCorto()}"
                )

                Text(
                    text =
                        "Hora: ${cita.hora.formato()}"
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            when (cita.modalidad) {

                                ModalidadAtencion.Presencial ->
                                    Icons.Default.LocationOn

                                ModalidadAtencion.Teleconsulta ->
                                    Icons.Default.Videocam
                            },
                        contentDescription =
                            cita.modalidad.etiqueta()
                    )

                    Text(
                        text =
                            "Modalidad: ${cita.modalidad.etiqueta()}"
                    )
                }

                Text(
                    text =
                        "Estado: ${cita.estado.etiqueta()}"
                )

                Text(
                    text =
                        "Motivo: ${cita.motivo}"
                )

                when (
                    val estado = cita.estado
                ) {

                    is EstadoCita.Atendida -> {

                        Text(
                            text =
                                "Indicaciones: ${estado.indicaciones}"
                        )
                    }

                    is EstadoCita.Cancelada -> {

                        Text(
                            text =
                                "Motivo de cancelación: ${estado.motivo}"
                        )
                    }

                    is EstadoCita.Programada -> {

                        Text(
                            text =
                                if (
                                    estado.recordatorioActivo
                                ) {
                                    "Recordatorio activo"
                                } else {
                                    "Recordatorio desactivado"
                                }
                        )
                    }
                }

                if (
                    cita.reprogramaciones
                        .isNotEmpty()
                ) {

                    HorizontalDivider()

                    Text(
                        text =
                            "Historial de reprogramación",
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )

                    cita.reprogramaciones
                        .forEachIndexed {
                                indice,
                                cambio ->

                            ElevatedCard(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                            ) {

                                Column(
                                    modifier =
                                        Modifier
                                            .padding(14.dp),
                                    verticalArrangement =
                                        Arrangement
                                            .spacedBy(5.dp)
                                ) {

                                    Text(
                                        text =
                                            "Cambio ${indice + 1}",
                                        style =
                                            MaterialTheme
                                                .typography
                                                .labelLarge
                                    )

                                    Text(
                                        text =
                                            "Anterior: " +
                                                    "${cambio.fechaAnterior.formatoCorto()} " +
                                                    "${cambio.horaAnterior.formato()}"
                                    )

                                    Text(
                                        text =
                                            "Nueva: " +
                                                    "${cambio.fechaNueva.formatoCorto()} " +
                                                    "${cambio.horaNueva.formato()}"
                                    )
                                }
                            }
                        }
                }

                s.mensaje?.let {

                    AssistChip(
                        onClick = {},
                        label = {
                            Text(it)
                        }
                    )
                }

                if (
                    cita.estado is
                            EstadoCita.Programada
                ) {

                    Button(
                        onClick = {

                            viewModel
                                .limpiarFormularioReprogramacion()

                            mostrarReprogramacion =
                                true
                        },
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text(
                            "Reprogramar cita"
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            confirmarCancelacion =
                                true
                        },
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text(
                            "Cancelar cita",
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .error
                        )
                    }
                }
            }

            if (mostrarReprogramacion) {

                AlertDialog(
                    onDismissRequest = {

                        mostrarReprogramacion =
                            false

                        viewModel
                            .limpiarFormularioReprogramacion()
                    },

                    title = {

                        Text(
                            "Reprogramar cita"
                        )
                    },

                    text = {

                        Column(
                            verticalArrangement =
                                Arrangement
                                    .spacedBy(10.dp)
                        ) {

                            Text(
                                text =
                                    "Cita actual: " +
                                            "${cita.fecha.formatoCorto()} " +
                                            "${cita.hora.formato()}"
                            )

                            OutlinedTextField(
                                value =
                                    s.fechaReprogramacion,

                                onValueChange =
                                    viewModel::
                                    cambiarFechaReprogramacion,

                                modifier =
                                    Modifier.fillMaxWidth(),

                                label = {
                                    Text(
                                        "Nueva fecha"
                                    )
                                },

                                placeholder = {
                                    Text(
                                        "AAAA-MM-DD"
                                    )
                                },

                                singleLine = true,

                                isError =
                                    s.erroresReprogramacion
                                        .fecha != null,

                                supportingText = {

                                    s.erroresReprogramacion
                                        .fecha
                                        ?.let {
                                            Text(it)
                                        }
                                }
                            )

                            OutlinedTextField(
                                value =
                                    s.horaReprogramacion,

                                onValueChange =
                                    viewModel::
                                    cambiarHoraReprogramacion,

                                modifier =
                                    Modifier.fillMaxWidth(),

                                label = {
                                    Text(
                                        "Nueva hora"
                                    )
                                },

                                placeholder = {
                                    Text(
                                        "HH:mm"
                                    )
                                },

                                singleLine = true,

                                isError =
                                    s.erroresReprogramacion
                                        .hora != null,

                                supportingText = {

                                    s.erroresReprogramacion
                                        .hora
                                        ?.let {
                                            Text(it)
                                        }
                                }
                            )

                            s.erroresReprogramacion
                                .general
                                ?.let {

                                    Text(
                                        text = it,
                                        color =
                                            MaterialTheme
                                                .colorScheme
                                                .error
                                    )
                                }
                        }
                    },

                    confirmButton = {

                        TextButton(
                            onClick = {
                                viewModel.reprogramar()
                            },
                            enabled =
                                !s.reprogramando
                        ) {

                            Text(
                                if (
                                    s.reprogramando
                                ) {
                                    "Guardando…"
                                } else {
                                    "Reprogramar"
                                }
                            )
                        }
                    },

                    dismissButton = {

                        TextButton(
                            onClick = {

                                mostrarReprogramacion =
                                    false

                                viewModel
                                    .limpiarFormularioReprogramacion()
                            }
                        ) {

                            Text(
                                "Volver"
                            )
                        }
                    }
                )
            }
        }
    }

    if (confirmarCancelacion) {

        AlertDialog(
            onDismissRequest = {
                confirmarCancelacion =
                    false
            },

            title = {
                Text(
                    "Cancelar cita"
                )
            },

            text = {
                Text(
                    "¿Confirmas que deseas cancelar esta cita?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        confirmarCancelacion =
                            false

                        viewModel.cancelar()
                    }
                ) {

                    Text(
                        "Sí, cancelar"
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        confirmarCancelacion =
                            false
                    }
                ) {

                    Text(
                        "Volver"
                    )
                }
            }
        )
    }
}