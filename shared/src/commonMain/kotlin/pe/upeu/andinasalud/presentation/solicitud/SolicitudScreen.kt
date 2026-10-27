package pe.upeu.andinasalud.presentation.solicitud

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.ModalidadAtencion
import pe.upeu.andinasalud.presentation.components.EstadoCarga

@Composable
fun SolicitudScreen(
    viewModel: SolicitudViewModel,
    onCitaCreada: (Long) -> Unit
) {

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(
        state.citaCreadaId
    ) {
        state.citaCreadaId?.let {

            onCitaCreada(it)

            viewModel
                .consumirNavegacion()
        }
    }

    if (state.cargando) {

        EstadoCarga(
            "Cargando opciones…"
        )

        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Solicitar cita",
            style =
                MaterialTheme
                    .typography
                    .headlineSmall
        )

        SelectorTexto(
            label = "Especialidad",
            selected =
                state.formulario
                    .especialidad,
            opciones =
                state.catalogo
                    ?.especialidades
                    .orEmpty(),
            on =
                viewModel::especialidad,
            error =
                state.errores
                    .especialidad
        )

        SelectorPar(
            label = "Sede",
            selected =
                state.formulario
                    .sedeId,
            opciones =
                state.catalogo
                    ?.sedes
                    ?.map {
                        it.id to it.nombre
                    }
                    .orEmpty(),
            on =
                viewModel::sede,
            error =
                state.errores.sede
        )

        Text(
            text =
                "Modalidad de atención",
            style =
                MaterialTheme
                    .typography
                    .titleMedium
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            FilterChip(
                selected =
                    state.formulario
                        .modalidad ==
                            ModalidadAtencion
                                .Presencial,

                onClick = {

                    viewModel.modalidad(
                        ModalidadAtencion
                            .Presencial
                    )
                },

                label = {
                    Text(
                        "Presencial"
                    )
                },

                leadingIcon = {

                    Icon(
                        imageVector =
                            Icons.Default
                                .LocationOn,
                        contentDescription =
                            null
                    )
                }
            )

            FilterChip(
                selected =
                    state.formulario
                        .modalidad ==
                            ModalidadAtencion
                                .Teleconsulta,

                onClick = {

                    viewModel.modalidad(
                        ModalidadAtencion
                            .Teleconsulta
                    )
                },

                label = {
                    Text(
                        "Teleconsulta"
                    )
                },

                leadingIcon = {

                    Icon(
                        imageVector =
                            Icons.Default
                                .Videocam,
                        contentDescription =
                            null
                    )
                }
            )
        }

        Campo(
            label =
                "Fecha (AAAA-MM-DD)",
            value =
                state.formulario.fecha,
            on =
                viewModel::fecha,
            error =
                state.errores.fecha
        )

        Campo(
            label =
                "Hora (HH:mm)",
            value =
                state.formulario.hora,
            on =
                viewModel::hora,
            error =
                state.errores.hora
        )

        OutlinedTextField(
            value =
                state.formulario.motivo,

            onValueChange =
                viewModel::motivo,

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text(
                    "Motivo"
                )
            },

            minLines = 3,

            isError =
                state.errores
                    .motivo != null,

            supportingText = {

                state.errores
                    .motivo
                    ?.let {

                        Text(it)

                    }
                    ?: Text(
                        "Entre 10 y 200 caracteres"
                    )
            }
        )

        state.mensaje?.let {

            Text(
                text = it,

                color =
                    if (
                        state.errores
                            .general != null
                    ) {
                        MaterialTheme
                            .colorScheme
                            .error
                    } else {
                        MaterialTheme
                            .colorScheme
                            .primary
                    }
            )
        }

        Button(
            onClick =
                viewModel::registrar,

            enabled =
                !state.enviando,

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                if (state.enviando) {
                    "Enviando…"
                } else {
                    "Solicitar cita"
                }
            )
        }
    }
}

@Composable
private fun Campo(
    label: String,
    value: String,
    on: (String) -> Unit,
    error: String?
) {

    OutlinedTextField(
        value = value,
        onValueChange = on,
        modifier =
            Modifier.fillMaxWidth(),
        label = {
            Text(label)
        },
        singleLine = true,
        isError = error != null,
        supportingText = {

            error?.let {
                Text(it)
            }
        }
    )
}

@Composable
private fun SelectorTexto(
    label: String,
    selected: String,
    opciones: List<String>,
    on: (String) -> Unit,
    error: String?
) {

    var abierto by remember {
        mutableStateOf(false)
    }

    Box(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        OutlinedButton(
            onClick = {
                abierto = true
            },
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                if (
                    selected.isBlank()
                ) {
                    label
                } else {
                    selected
                }
            )
        }

        DropdownMenu(
            expanded = abierto,
            onDismissRequest = {
                abierto = false
            }
        ) {

            opciones.forEach {
                    opcion ->

                DropdownMenuItem(
                    text = {
                        Text(opcion)
                    },
                    onClick = {

                        on(opcion)

                        abierto = false
                    }
                )
            }
        }
    }

    error?.let {

        Text(
            text = it,
            color =
                MaterialTheme
                    .colorScheme
                    .error,
            style =
                MaterialTheme
                    .typography
                    .bodySmall
        )
    }
}

@Composable
private fun SelectorPar(
    label: String,
    selected: String,
    opciones:
    List<Pair<String, String>>,
    on: (String) -> Unit,
    error: String?
) {

    var abierto by remember {
        mutableStateOf(false)
    }

    val texto =
        opciones
            .firstOrNull {
                it.first == selected
            }
            ?.second
            ?: label

    Box(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        OutlinedButton(
            onClick = {
                abierto = true
            },
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(texto)
        }

        DropdownMenu(
            expanded = abierto,
            onDismissRequest = {
                abierto = false
            }
        ) {

            opciones.forEach {
                    opcion ->

                DropdownMenuItem(
                    text = {
                        Text(
                            opcion.second
                        )
                    },

                    onClick = {

                        on(
                            opcion.first
                        )

                        abierto = false
                    }
                )
            }
        }
    }

    error?.let {

        Text(
            text = it,
            color =
                MaterialTheme
                    .colorScheme
                    .error,
            style =
                MaterialTheme
                    .typography
                    .bodySmall
        )
    }
}