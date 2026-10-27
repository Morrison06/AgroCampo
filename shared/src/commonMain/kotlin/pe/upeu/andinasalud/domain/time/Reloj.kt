package pe.upeu.andinasalud.domain.time

import pe.upeu.andinasalud.domain.model.FechaHora

interface Reloj { fun ahora(): FechaHora }
expect class RelojSistema() : Reloj { override fun ahora(): FechaHora }
