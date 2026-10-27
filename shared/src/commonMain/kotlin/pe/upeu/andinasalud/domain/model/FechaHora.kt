package pe.upeu.andinasalud.domain.model

data class Fecha(val anio: Int, val mes: Int, val dia: Int) : Comparable<Fecha> {
    init {
        require(mes in 1..12)
        require(dia in 1..diasDelMes(anio, mes))
    }

    override fun compareTo(other: Fecha): Int = when {
        anio != other.anio -> anio.compareTo(other.anio)
        mes != other.mes -> mes.compareTo(other.mes)
        else -> dia.compareTo(other.dia)
    }

    fun formatoIso(): String = "${anio.toString().padStart(4,'0')}-${mes.toString().padStart(2,'0')}-${dia.toString().padStart(2,'0')}"
    fun formatoCorto(): String = "${dia.toString().padStart(2,'0')}/${mes.toString().padStart(2,'0')}/$anio"

    fun masDias(dias: Int): Fecha = desdeEpochDay(toEpochDay() + dias)

    fun toEpochDay(): Long {
        var y = anio
        val m = mes
        y -= if (m <= 2) 1 else 0
        val era = floorDiv(y, 400)
        val yoe = y - era * 400
        val mp = m + if (m > 2) -3 else 9
        val doy = (153 * mp + 2) / 5 + dia - 1
        val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
        return era.toLong() * 146097L + doe - 719468L
    }

    companion object {
        fun parse(texto: String): Fecha? {
            val p = texto.trim().split("-")
            if (p.size != 3) return null
            val y = p[0].toIntOrNull() ?: return null
            val m = p[1].toIntOrNull() ?: return null
            val d = p[2].toIntOrNull() ?: return null
            return runCatching { Fecha(y, m, d) }.getOrNull()
        }

        fun desdeEpochDay(epochDay: Long): Fecha {
            var z = epochDay + 719468L
            val era = floorDivLong(z, 146097L)
            val doe = z - era * 146097L
            val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
            var y = (yoe + era * 400).toInt()
            val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
            val mp = (5 * doy + 2) / 153
            val d = (doy - (153 * mp + 2) / 5 + 1).toInt()
            val m = (mp + if (mp < 10) 3 else -9).toInt()
            y += if (m <= 2) 1 else 0
            return Fecha(y, m, d)
        }

        private fun floorDiv(a: Int, b: Int): Int {
            var q = a / b
            val r = a % b
            if (r != 0 && (r < 0) != (b < 0)) q--
            return q
        }

        private fun floorDivLong(a: Long, b: Long): Long {
            var q = a / b
            val r = a % b
            if (r != 0L && (r < 0) != (b < 0)) q--
            return q
        }

        private fun bisiesto(anio: Int) = anio % 400 == 0 || (anio % 4 == 0 && anio % 100 != 0)
        private fun diasDelMes(anio: Int, mes: Int) = when (mes) {
            2 -> if (bisiesto(anio)) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }
    }
}

data class Hora(val hora: Int, val minuto: Int) : Comparable<Hora> {
    init { require(hora in 0..23); require(minuto in 0..59) }
    override fun compareTo(other: Hora): Int = if (hora != other.hora) hora.compareTo(other.hora) else minuto.compareTo(other.minuto)
    fun formato(): String = "${hora.toString().padStart(2,'0')}:${minuto.toString().padStart(2,'0')}"
    fun minutosDelDia(): Int = hora * 60 + minuto
    companion object {
        fun parse(texto: String): Hora? {
            val p = texto.trim().split(":")
            if (p.size != 2) return null
            val h = p[0].toIntOrNull() ?: return null
            val m = p[1].toIntOrNull() ?: return null
            return runCatching { Hora(h, m) }.getOrNull()
        }
    }
}

data class FechaHora(val fecha: Fecha, val hora: Hora) : Comparable<FechaHora> {
    override fun compareTo(other: FechaHora): Int {
        val f = fecha.compareTo(other.fecha)
        return if (f != 0) f else hora.compareTo(other.hora)
    }
    fun minutosDesde(other: FechaHora): Long = (fecha.toEpochDay() - other.fecha.toEpochDay()) * 1440L + hora.minutosDelDia() - other.hora.minutosDelDia()
}
