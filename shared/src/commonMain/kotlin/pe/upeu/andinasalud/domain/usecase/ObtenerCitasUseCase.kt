package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.repository.CitaRepository

class ObtenerCitasUseCase(private val repository: CitaRepository) {
    suspend operator fun invoke(): Result<List<Cita>> = runCatching {
        repository.obtenerCitas().sortedWith(compareBy<Cita> { it.fecha }.thenBy { it.hora })
    }
}
