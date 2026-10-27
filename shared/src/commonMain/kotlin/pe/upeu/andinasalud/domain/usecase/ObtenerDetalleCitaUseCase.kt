package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.repository.CitaRepository

class ObtenerDetalleCitaUseCase(private val repository: CitaRepository) {
    suspend operator fun invoke(id: Long): Result<Cita> = runCatching {
        repository.obtenerCita(id) ?: error("No se encontró la cita")
    }
}
