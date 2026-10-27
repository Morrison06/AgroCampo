package pe.upeu.andinasalud.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.time.Reloj
import pe.upeu.andinasalud.domain.time.RelojSistema
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerDetalleCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerPacienteUseCase
import pe.upeu.andinasalud.domain.usecase.ReprogramarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.upeu.andinasalud.presentation.citas.CitasViewModel
import pe.upeu.andinasalud.presentation.detalle.DetalleCitaViewModel
import pe.upeu.andinasalud.presentation.inicio.InicioViewModel
import pe.upeu.andinasalud.presentation.perfil.PerfilViewModel
import pe.upeu.andinasalud.presentation.solicitud.SolicitudViewModel

val dataModule = module {

    single<Reloj> {
        RelojSistema()
    }

    single<CitaRepository> {
        CitaRepositoryFake(get())
    }
}

val domainModule = module {

    factory {
        ObtenerCitasUseCase(get())
    }

    factory {
        ObtenerDetalleCitaUseCase(get())
    }

    factory {
        ObtenerPacienteUseCase(get())
    }

    factory {
        ObtenerCatalogoUseCase(get())
    }

    factory {
        SolicitarCitaUseCase(
            get(),
            get()
        )
    }

    factory {
        CancelarCitaUseCase(
            get(),
            get()
        )
    }

    factory {
        ReprogramarCitaUseCase(
            get(),
            get()
        )
    }
}

val presentationModule = module {

    viewModel {
        InicioViewModel(
            get(),
            get()
        )
    }

    viewModel {
        CitasViewModel(
            get(),
            get()
        )
    }

    viewModel {
        DetalleCitaViewModel(
            get(),
            get(),
            get()
        )
    }

    viewModel {
        SolicitudViewModel(
            get(),
            get()
        )
    }

    viewModel {
        PerfilViewModel(
            get()
        )
    }
}

expect val platformModule: Module

fun initKoin(
    configuracionAdicional: KoinApplication.() -> Unit = {}
) {
    startKoin {

        configuracionAdicional()

        modules(
            dataModule,
            domainModule,
            presentationModule,
            platformModule
        )
    }
}