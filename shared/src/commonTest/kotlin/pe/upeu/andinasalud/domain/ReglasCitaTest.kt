package pe.upeu.andinasalud.domain

import kotlin.test.*
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.usecase.ReglasCita

class ReglasCitaTest {
    @Test fun motivoLimites() { assertTrue(ReglasCita.motivoValido("1234567890")); assertFalse(ReglasCita.motivoValido("corto")) }
    @Test fun fechaHoraFutura() { val a=FechaHora(Fecha(2026,9,22),Hora(10,0)); val b=FechaHora(Fecha(2026,9,23),Hora(9,0)); assertTrue(ReglasCita.esFutura(b,a)) }
    @Test fun fechaMasDias() { assertEquals("2027-01-01", Fecha(2026,12,31).masDias(1).formatoIso()) }
}
