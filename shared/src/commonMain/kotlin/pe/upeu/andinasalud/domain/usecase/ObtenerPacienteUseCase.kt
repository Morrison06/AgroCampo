package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.repository.CitaRepository

class ObtenerPacienteUseCase(private val repository: CitaRepository) {
    suspend operator fun invoke(): Result<Paciente> = runCatching { repository.obtenerPaciente() }
}
