package pe.upeu.andinasalud.presentation.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.components.*

@Composable
fun PerfilScreen(viewModel: PerfilViewModel, darkTheme:Boolean, onDarkTheme:(Boolean)->Unit) {
    val state by viewModel.uiState.collectAsState()
    when(val s=state) {
        PerfilUiState.Cargando -> EstadoCarga("Cargando perfil…")
        is PerfilUiState.Error -> EstadoError(s.mensaje, viewModel::cargar)
        is PerfilUiState.Contenido -> Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement=Arrangement.spacedBy(16.dp)) {
            Icon(Icons.Default.Person, null, modifier=Modifier.size(72.dp).align(Alignment.CenterHorizontally), tint=MaterialTheme.colorScheme.primary)
            Text(s.paciente.nombre, style=MaterialTheme.typography.headlineSmall, modifier=Modifier.align(Alignment.CenterHorizontally))
            HorizontalDivider()
            Text("Documento: ${s.paciente.documento}")
            Text("Correo: ${s.paciente.correo}")
            Text("Teléfono: ${s.paciente.telefono}")
            HorizontalDivider()
            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically) {
                Column { Text("Modo oscuro", style=MaterialTheme.typography.titleMedium); Text("Aplicar a toda la aplicación", color=MaterialTheme.colorScheme.onSurfaceVariant) }
                Switch(checked=darkTheme, onCheckedChange=onDarkTheme)
            }
        }
    }
}
