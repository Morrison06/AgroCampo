package pe.upeu.andinasalud.data.local

import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.time.Reloj

data class DatosSemilla(
    val paciente: Paciente,
    val catalogo: CatalogoCitas,
    val citas: List<Cita>
)

object CitasSimuladas {

    fun crear(reloj: Reloj): DatosSemilla {

        val paciente = Paciente(
            "P-0417",
            "Lucía Quispe Mamani",
            "70154823",
            "lucia.quispe@correo.pe",
            "987 654 321"
        )

        val sedes = listOf(
            Sede("nana", "Ñaña"),
            Sede("chosica", "Chosica"),
            Sede("chaclacayo", "Chaclacayo"),
            Sede("santa-anita", "Santa Anita")
        )

        val especialidades = listOf(
            "Medicina General",
            "Odontología",
            "Pediatría",
            "Nutrición",
            "Psicología"
        )

        val medicos = listOf(
            Medico(
                "M01",
                "Dr. Iván Rojas",
                "Medicina General",
                listOf("nana", "chosica")
            ),
            Medico(
                "M02",
                "Dra. Elena Paredes",
                "Medicina General",
                listOf("chaclacayo", "santa-anita")
            ),
            Medico(
                "M03",
                "Dra. Rosa Flores",
                "Odontología",
                listOf("chosica", "santa-anita")
            ),
            Medico(
                "M04",
                "Dr. Diego Salazar",
                "Odontología",
                listOf("nana", "chaclacayo")
            ),
            Medico(
                "M05",
                "Dra. Carla Núñez",
                "Pediatría",
                listOf("chaclacayo", "nana")
            ),
            Medico(
                "M06",
                "Dr. Marco León",
                "Pediatría",
                listOf("chosica", "santa-anita")
            ),
            Medico(
                "M07",
                "Lic. Ana Bermúdez",
                "Nutrición",
                listOf("santa-anita", "nana")
            ),
            Medico(
                "M08",
                "Lic. José Huamán",
                "Nutrición",
                listOf("chosica", "chaclacayo")
            ),
            Medico(
                "M09",
                "Ps. Luis Tapia",
                "Psicología",
                listOf("nana", "santa-anita")
            ),
            Medico(
                "M10",
                "Ps. Andrea Ríos",
                "Psicología",
                listOf("chosica", "chaclacayo")
            )
        )

        val hoy = reloj.ahora().fecha

        fun sede(id: String) =
            sedes.first { it.id == id }

        fun medico(id: String) =
            medicos.first { it.id == id }

        val citas = listOf(
            Cita(
                id = 1,
                pacienteId = paciente.id,
                especialidad = "Medicina General",
                medico = medico("M01"),
                sede = sede("nana"),
                fecha = hoy.masDias(2),
                hora = Hora(9, 0),
                motivo = "Dolor de cabeza recurrente",
                modalidad = ModalidadAtencion.Presencial,
                estado = EstadoCita.Programada(true)
            ),

            Cita(
                id = 2,
                pacienteId = paciente.id,
                especialidad = "Odontología",
                medico = medico("M03"),
                sede = sede("chosica"),
                fecha = hoy.masDias(5),
                hora = Hora(16, 30),
                motivo = "Control preventivo y limpieza dental",
                modalidad = ModalidadAtencion.Presencial,
                estado = EstadoCita.Programada(false)
            ),

            Cita(
                id = 3,
                pacienteId = paciente.id,
                especialidad = "Nutrición",
                medico = medico("M07"),
                sede = sede("santa-anita"),
                fecha = hoy.masDias(9),
                hora = Hora(11, 15),
                motivo = "Evaluación nutricional y plan de alimentación",
                modalidad = ModalidadAtencion.Teleconsulta,
                estado = EstadoCita.Programada(true)
            ),

            Cita(
                id = 4,
                pacienteId = paciente.id,
                especialidad = "Pediatría",
                medico = medico("M05"),
                sede = sede("chaclacayo"),
                fecha = hoy.masDias(-12),
                hora = Hora(8, 45),
                motivo = "Control general",
                modalidad = ModalidadAtencion.Presencial,
                estado = EstadoCita.Atendida(
                    "Control en tres meses"
                )
            ),

            Cita(
                id = 5,
                pacienteId = paciente.id,
                especialidad = "Psicología",
                medico = medico("M09"),
                sede = sede("nana"),
                fecha = hoy.masDias(-6),
                hora = Hora(15, 0),
                motivo = "Seguimiento psicológico",
                modalidad = ModalidadAtencion.Teleconsulta,
                estado = EstadoCita.Atendida(
                    "Continuar sesiones quincenales"
                )
            ),

            Cita(
                id = 6,
                pacienteId = paciente.id,
                especialidad = "Medicina General",
                medico = medico("M01"),
                sede = sede("chosica"),
                fecha = hoy.masDias(-3),
                hora = Hora(10, 30),
                motivo = "Consulta general",
                modalidad = ModalidadAtencion.Presencial,
                estado = EstadoCita.Cancelada(
                    "Viaje del paciente",
                    true
                )
            )
        )

        return DatosSemilla(
            paciente = paciente,
            catalogo = CatalogoCitas(
                sedes,
                especialidades,
                medicos
            ),
            citas = citas
        )
    }
}