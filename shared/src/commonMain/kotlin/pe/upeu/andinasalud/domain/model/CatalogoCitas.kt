package pe.upeu.andinasalud.domain.model

data class CatalogoCitas(
    val sedes: List<Sede>,
    val especialidades: List<String>,
    val medicos: List<Medico>
)
