package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.*

data class ValidacionProgramacion(
    val fecha: Fecha? = null,
    val hora: Hora? = null,
    val errorFecha: String? = null,
    val errorHora: String? = null
) {
    val esValida: Boolean
        get() =
            fecha != null &&
                    hora != null &&
                    errorFecha == null &&
                    errorHora == null
}

object ReglasCita {

    const val MAX_CITAS_PROGRAMADAS = 3
    const val MOTIVO_MIN = 10
    const val MOTIVO_MAX = 200
    const val HORAS_MIN_CANCELACION = 24

    fun esFutura(
        cita: FechaHora,
        ahora: FechaHora
    ): Boolean =
        cita > ahora

    fun cantidadProgramadas(
        citas: List<Cita>
    ): Int =
        citas.count {
            it.estado is EstadoCita.Programada
        }

    fun limiteProgramadasAlcanzado(
        citas: List<Cita>
    ): Boolean =
        cantidadProgramadas(citas) >=
                MAX_CITAS_PROGRAMADAS

    fun hayDuplicada(
        citas: List<Cita>,
        fecha: Fecha,
        hora: Hora,
        ignorarCitaId: Long? = null
    ): Boolean =
        citas.any { cita ->

            cita.id != ignorarCitaId &&
                    cita.estado is EstadoCita.Programada &&
                    cita.fecha == fecha &&
                    cita.hora == hora
        }

    fun motivoValido(
        motivo: String
    ): Boolean =
        motivo
            .trim()
            .length in MOTIVO_MIN..MOTIVO_MAX

    fun puedeCancelar(
        cita: Cita,
        ahora: FechaHora
    ): Boolean =
        cita.estado is EstadoCita.Programada &&
                cita.fechaHora.minutosDesde(ahora) >
                HORAS_MIN_CANCELACION * 60L

    fun validarProgramacion(
        fechaTexto: String,
        horaTexto: String,
        ahora: FechaHora,
        citas: List<Cita>,
        ignorarCitaId: Long? = null
    ): ValidacionProgramacion {

        if (fechaTexto.isBlank()) {
            return ValidacionProgramacion(
                errorFecha = "La fecha es obligatoria"
            )
        }

        if (horaTexto.isBlank()) {
            return ValidacionProgramacion(
                errorHora = "La hora es obligatoria"
            )
        }

        val fecha =
            Fecha.parse(fechaTexto)

        if (fecha == null) {
            return ValidacionProgramacion(
                errorFecha =
                    "Usa el formato AAAA-MM-DD"
            )
        }

        val hora =
            Hora.parse(horaTexto)

        if (hora == null) {
            return ValidacionProgramacion(
                fecha = fecha,
                errorHora =
                    "Usa el formato HH:mm"
            )
        }

        val momento =
            FechaHora(
                fecha = fecha,
                hora = hora
            )

        if (
            !esFutura(
                momento,
                ahora
            )
        ) {
            return ValidacionProgramacion(
                fecha = fecha,
                hora = hora,
                errorFecha =
                    "La cita debe ser posterior al momento actual"
            )
        }

        if (
            hayDuplicada(
                citas = citas,
                fecha = fecha,
                hora = hora,
                ignorarCitaId =
                    ignorarCitaId
            )
        ) {
            return ValidacionProgramacion(
                fecha = fecha,
                hora = hora,
                errorHora =
                    "Ya tienes una cita programada en esa fecha y hora"
            )
        }

        return ValidacionProgramacion(
            fecha = fecha,
            hora = hora
        )
    }
}