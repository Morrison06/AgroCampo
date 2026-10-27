package pe.upeu.andinasalud.presentation.navigation

sealed interface Ruta {
    data object Inicio : Ruta
    data object Citas : Ruta
    data object Perfil : Ruta
    data class Detalle(val citaId: Long) : Ruta
    data object Solicitud : Ruta
}
