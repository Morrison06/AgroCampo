package pe.upeu.andinasalud.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pe.upeu.andinasalud.data.local.CitasSimuladas
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.time.Reloj

class CitaRepositoryFake(reloj: Reloj) : CitaRepository {
    private val mutex = Mutex()
    private val semilla = CitasSimuladas.crear(reloj)
    private val citas = semilla.citas.toMutableList()

    override suspend fun obtenerPaciente(): Paciente { delay(800); return semilla.paciente }
    override suspend fun obtenerCatalogo(): CatalogoCitas { delay(250); return semilla.catalogo }
    override suspend fun obtenerCitas(): List<Cita> { delay(800); return mutex.withLock { citas.toList() } }
    override suspend fun obtenerCita(id: Long): Cita? { delay(800); return mutex.withLock { citas.firstOrNull { it.id == id } } }
    override suspend fun guardarCita(cita: Cita): Cita { delay(350); return mutex.withLock { citas += cita; cita } }
    override suspend fun actualizarCita(cita: Cita): Cita {
        delay(350)
        return mutex.withLock {
            val i = citas.indexOfFirst { it.id == cita.id }
            require(i >= 0) { "Cita inexistente" }
            citas[i] = cita
            cita
        }
    }
}
