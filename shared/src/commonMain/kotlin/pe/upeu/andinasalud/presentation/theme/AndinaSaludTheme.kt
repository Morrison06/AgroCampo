package pe.upeu.andinasalud.presentation.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = SaludPrimary, onPrimary = SaludOnPrimary, primaryContainer = SaludPrimaryContainer, onPrimaryContainer = SaludOnPrimaryContainer,
    secondary = SaludSecondary, secondaryContainer = SaludSecondaryContainer, tertiary = SaludTertiary,
    background = SaludBackground, surface = SaludSurface
)
private val DarkColors = darkColorScheme(
    primary = SaludDarkPrimary, primaryContainer = SaludPrimary, secondary = SaludSecondaryContainer,
    tertiary = SaludTertiary, background = SaludDarkBackground, surface = SaludDarkSurface
)

@Composable
fun AndinaSaludTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, typography = AppTypography, content = content)
}
