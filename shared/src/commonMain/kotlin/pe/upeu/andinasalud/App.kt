package pe.upeu.andinasalud

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.andinasalud.presentation.citas.*
import pe.upeu.andinasalud.presentation.detalle.*
import pe.upeu.andinasalud.presentation.inicio.*
import pe.upeu.andinasalud.presentation.navigation.Ruta
import pe.upeu.andinasalud.presentation.navigation.PlatformBackHandler
import pe.upeu.andinasalud.presentation.perfil.*
import pe.upeu.andinasalud.presentation.solicitud.*
import pe.upeu.andinasalud.presentation.theme.AndinaSaludTheme

private data class Destino(
    val ruta: Ruta,
    val titulo: String,
    val icono: ImageVector
)

private val destinos = listOf(
    Destino(
        Ruta.Inicio,
        "Inicio",
        Icons.Default.Home
    ),
    Destino(
        Ruta.Citas,
        "Citas",
        Icons.Default.CalendarMonth
    ),
    Destino(
        Ruta.Perfil,
        "Perfil",
        Icons.Default.Person
    )
)

@Composable
fun App() = KoinContext {

    var darkTheme by rememberSaveable {
        mutableStateOf(false)
    }

    var ruta by remember {
        mutableStateOf<Ruta>(Ruta.Inicio)
    }

    var principalAnterior by remember {
        mutableStateOf<Ruta>(Ruta.Inicio)
    }









    val citasViewModel: CitasViewModel =
        koinViewModel()

    val citasState by
    citasViewModel.uiState.collectAsState()

    fun navegar(nueva: Ruta) {

        if (
            nueva is Ruta.Inicio ||
            nueva is Ruta.Citas ||
            nueva is Ruta.Perfil
        ) {
            principalAnterior = nueva
        }

        ruta = nueva





        if (
            nueva is Ruta.Inicio ||
            nueva is Ruta.Citas
        ) {
            citasViewModel.cargar()
        }
    }

    fun volver() {

        ruta = principalAnterior

        if (
            principalAnterior is Ruta.Inicio ||
            principalAnterior is Ruta.Citas
        ) {
            citasViewModel.cargar()
        }
    }

    PlatformBackHandler(
        enabled =
            ruta is Ruta.Detalle ||
                    ruta is Ruta.Solicitud
    ) {
        volver()
    }

    AndinaSaludTheme(
        darkTheme
    ) {

        Scaffold(

            topBar = {

                TopAppBar(

                    title = {

                        Text(
                            when (ruta) {

                                Ruta.Inicio ->
                                    "AndinaSalud"

                                Ruta.Citas ->
                                    "Mis citas"

                                Ruta.Perfil ->
                                    "Perfil"

                                is Ruta.Detalle ->
                                    "Detalle de cita"

                                Ruta.Solicitud ->
                                    "Solicitar cita"
                            }
                        )
                    },

                    navigationIcon = {

                        if (
                            ruta is Ruta.Detalle ||
                            ruta is Ruta.Solicitud
                        ) {

                            IconButton(
                                onClick = {
                                    volver()
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.ArrowBack,
                                    contentDescription =
                                        "Volver"
                                )
                            }
                        }
                    }
                )
            },

            bottomBar = {

                if (
                    ruta is Ruta.Inicio ||
                    ruta is Ruta.Citas ||
                    ruta is Ruta.Perfil
                ) {

                    NavigationBar {

                        destinos.forEach { destino ->

                            NavigationBarItem(

                                selected =
                                    ruta == destino.ruta,

                                onClick = {
                                    navegar(destino.ruta)
                                },

                                icon = {






                                    if (
                                        destino.ruta == Ruta.Citas
                                    ) {

                                        BadgedBox(

                                            badge = {

                                                Badge {

                                                    Text(
                                                        citasState
                                                            .cantidadProgramadas
                                                            .toString()
                                                    )
                                                }
                                            }
                                        ) {

                                            Icon(
                                                imageVector =
                                                    destino.icono,
                                                contentDescription =
                                                    destino.titulo
                                            )
                                        }

                                    } else {

                                        Icon(
                                            imageVector =
                                                destino.icono,
                                            contentDescription =
                                                destino.titulo
                                        )
                                    }
                                },

                                label = {

                                    Text(
                                        destino.titulo
                                    )
                                }
                            )
                        }
                    }
                }
            }
        ) { padding ->

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
            ) {

                when (val r = ruta) {

                    Ruta.Inicio -> {

                        InicioScreen(

                            viewModel =
                                koinViewModel(),

                            puedeSolicitar =
                                citasState.puedeSolicitar,

                            onMisCitas = {
                                navegar(
                                    Ruta.Citas
                                )
                            },

                            onSolicitar = {

                                if (
                                    citasState.puedeSolicitar
                                ) {
                                    navegar(
                                        Ruta.Solicitud
                                    )
                                }
                            },

                            onDetalle = { id ->

                                navegar(
                                    Ruta.Detalle(id)
                                )
                            }
                        )
                    }

                    Ruta.Citas -> {

                        CitasScreen(

                            viewModel =
                                citasViewModel,

                            puedeSolicitar =
                                citasState.puedeSolicitar,

                            onDetalle = { id ->

                                navegar(
                                    Ruta.Detalle(id)
                                )
                            },

                            onSolicitar = {

                                if (
                                    citasState.puedeSolicitar
                                ) {
                                    navegar(
                                        Ruta.Solicitud
                                    )
                                }
                            }
                        )
                    }

                    Ruta.Perfil -> {

                        PerfilScreen(
                            viewModel =
                                koinViewModel(),
                            darkTheme =
                                darkTheme
                        ) { nuevoTema ->

                            darkTheme =
                                nuevoTema
                        }
                    }

                    is Ruta.Detalle -> {

                        DetalleCitaScreen(
                            id = r.citaId,
                            viewModel = koinViewModel()
                        )
                    }

                    Ruta.Solicitud -> {

                        SolicitudScreen(
                            viewModel =
                                koinViewModel()
                        ) { id ->

                            navegar(
                                Ruta.Detalle(id)
                            )
                        }
                    }
                }
            }
        }
    }
}