package pe.upeu.andinasalud.domain.time

import java.time.LocalDateTime
import pe.upeu.andinasalud.domain.model.Fecha
import pe.upeu.andinasalud.domain.model.FechaHora
import pe.upeu.andinasalud.domain.model.Hora

actual class RelojSistema actual constructor() : Reloj {
    actual override fun ahora(): FechaHora {
        val n = LocalDateTime.now()
        return FechaHora(Fecha(n.year, n.monthValue, n.dayOfMonth), Hora(n.hour, n.minute))
    }
}
