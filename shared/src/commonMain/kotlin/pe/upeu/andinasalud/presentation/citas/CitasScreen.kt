package pe.upeu.andinasalud.presentation.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.components.*

@Composable
fun CitasScreen(
    viewModel: CitasViewModel,
    puedeSolicitar: Boolean,
    onDetalle: (Long) -> Unit,
    onSolicitar: () -> Unit
) {

    val state by
    viewModel.uiState.collectAsState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        verticalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {

        OutlinedTextField(
            value = state.busqueda,
            onValueChange =
                viewModel::onBusquedaChange,
            modifier =
                Modifier.fillMaxWidth(),
            leadingIcon = {

                Icon(
                    imageVector =
                        Icons.Default.Search,
                    contentDescription =
                        "Buscar"
                )
            },
            label = {

                Text(
                    "Buscar especialidad o médico"
                )
            },
            singleLine = true
        )

        LazyRow(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            item {

                FilterChip(
                    selected =
                        state.soloHoy,
                    onClick =
                        viewModel::onHoyChange,
                    label = {
                        Text("Hoy")
                    }
                )
            }

            items(
                FiltroCitas.entries.size
            ) { index ->

                val filtro =
                    FiltroCitas.entries[index]

                FilterChip(
                    selected =
                        state.filtro == filtro,
                    onClick = {
                        viewModel
                            .onFiltroChange(
                                filtro
                            )
                    },
                    label = {
                        Text(
                            filtro.etiqueta
                        )
                    }
                )
            }
        }

        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
        ) {

            when (
                val fase = state.fase
            ) {

                FaseCitas.Cargando -> {

                    EstadoCarga()
                }

                FaseCitas.Vacio -> {

                    EstadoVacio(
                        titulo =
                            "No hay citas",
                        descripcion =
                            "Prueba otro filtro o término de búsqueda"
                    )
                }

                is FaseCitas.Error -> {

                    EstadoError(
                        mensaje =
                            fase.mensaje,
                        onReintentar =
                            viewModel::cargar
                    )
                }

                is FaseCitas.Contenido -> {

                    LazyColumn(
                        verticalArrangement =
                            Arrangement
                                .spacedBy(10.dp),
                        contentPadding =
                            PaddingValues(
                                bottom = 100.dp
                            )
                    ) {

                        items(
                            items = fase.citas,
                            key = {
                                it.id
                            }
                        ) { cita ->

                            CitaCard(
                                cita = cita,
                                onClick = {
                                    onDetalle(
                                        cita.id
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Column(
                modifier =
                    Modifier
                        .align(
                            Alignment.BottomEnd
                        )
                        .padding(16.dp),
                horizontalAlignment =
                    Alignment.End
            ) {

                if (!puedeSolicitar) {

                    Text(
                        text =
                            "Máximo 3 citas programadas",
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .error
                    )

                    Spacer(
                        Modifier.height(4.dp)
                    )
                }

                Button(
                    onClick =
                        onSolicitar,
                    enabled =
                        puedeSolicitar
                ) {

                    Text(
                        "Solicitar cita"
                    )
                }
            }
        }
    }
}