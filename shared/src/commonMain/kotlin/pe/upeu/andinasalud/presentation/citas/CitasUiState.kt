package pe.upeu.andinasalud.presentation.citas

import pe.upeu.andinasalud.domain.model.Cita

enum class FiltroCitas(
    val etiqueta: String
) {
    Todas("Todas"),
    Programadas("Programada"),
    Atendidas("Atendida"),
    Canceladas("Cancelada")
}

sealed interface FaseCitas {

    data object Cargando : FaseCitas

    data object Vacio : FaseCitas

    data class Contenido(
        val citas: List<Cita>
    ) : FaseCitas

    data class Error(
        val mensaje: String
    ) : FaseCitas
}

data class CitasUiState(
    val fase: FaseCitas = FaseCitas.Cargando,
    val busqueda: String = "",
    val filtro: FiltroCitas = FiltroCitas.Todas,
    val soloHoy: Boolean = false,
    val cantidadProgramadas: Int = 0,
    val puedeSolicitar: Boolean = true
)