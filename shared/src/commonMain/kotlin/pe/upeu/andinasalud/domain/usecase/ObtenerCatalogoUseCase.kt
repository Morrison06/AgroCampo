package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.CatalogoCitas
import pe.upeu.andinasalud.domain.repository.CitaRepository

class ObtenerCatalogoUseCase(private val repository: CitaRepository) {
    suspend operator fun invoke(): Result<CatalogoCitas> = runCatching { repository.obtenerCatalogo() }
}
