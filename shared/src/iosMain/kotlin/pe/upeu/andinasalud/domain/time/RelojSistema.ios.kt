package pe.upeu.andinasalud.domain.time

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import pe.upeu.andinasalud.domain.model.Fecha
import pe.upeu.andinasalud.domain.model.FechaHora
import pe.upeu.andinasalud.domain.model.Hora

actual class RelojSistema actual constructor() : Reloj {
    actual override fun ahora(): FechaHora {
        val f = NSDateFormatter().apply {
            dateFormat = "yyyy-MM-dd-HH-mm"
        }
        val p = f.stringFromDate(NSDate()).split("-")
        return FechaHora(
            Fecha(p[0].toInt(), p[1].toInt(), p[2].toInt()),
            Hora(p[3].toInt(), p[4].toInt())
        )
    }
}
