package pe.upeu.andinasalud.domain.model

data class Reprogramacion(
    val fechaAnterior: Fecha,
    val horaAnterior: Hora,
    val fechaNueva: Fecha,
    val horaNueva: Hora
)