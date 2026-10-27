package pe.upeu.andinasalud.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.ModalidadAtencion

fun EstadoCita.etiqueta(): String =
    when (this) {
        is EstadoCita.Programada -> "Programada"
        is EstadoCita.Atendida -> "Atendida"
        is EstadoCita.Cancelada -> "Cancelada"
    }

fun ModalidadAtencion.etiqueta(): String =
    when (this) {
        ModalidadAtencion.Presencial -> "Presencial"
        ModalidadAtencion.Teleconsulta -> "Teleconsulta"
    }

@Composable
fun CitaCard(
    cita: Cita,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = cita.especialidad,
                    style = MaterialTheme.typography.titleMedium
                )

                AssistChip(
                    onClick = {},
                    label = {
                        Text(cita.estado.etiqueta())
                    }
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = cita.medico.nombre,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = cita.sede.nombre,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = "${cita.fecha.formatoCorto()} · ${cita.hora.formato()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        when (cita.modalidad) {
                            ModalidadAtencion.Presencial ->
                                Icons.Default.LocationOn

                            ModalidadAtencion.Teleconsulta ->
                                Icons.Default.Videocam
                        },
                    contentDescription = cita.modalidad.etiqueta(),
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = cita.modalidad.etiqueta(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}