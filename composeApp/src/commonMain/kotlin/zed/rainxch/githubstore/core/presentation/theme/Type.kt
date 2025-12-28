package zed.rainxch.githubstore.core.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import githubstore.composeapp.generated.resources.Inter_24pt_Black
import githubstore.composeapp.generated.resources.Inter_24pt_Bold
import githubstore.composeapp.generated.resources.Inter_24pt_Light
import githubstore.composeapp.generated.resources.Inter_24pt_Medium
import githubstore.composeapp.generated.resources.Inter_24pt_Regular
import githubstore.composeapp.generated.resources.Inter_24pt_SemiBold
import githubstore.composeapp.generated.resources.JetBrainsMono_Bold
import githubstore.composeapp.generated.resources.JetBrainsMono_Light
import githubstore.composeapp.generated.resources.JetBrainsMono_Medium
import githubstore.composeapp.generated.resources.JetBrainsMono_Regular
import githubstore.composeapp.generated.resources.JetBrainsMono_SemiBold
import githubstore.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font
import zed.rainxch.githubstore.core.presentation.model.FontTheme

val jetbrainsMonoFontFamily
    @Composable get() = FontFamily(
        Font(Res.font.JetBrainsMono_Light, FontWeight.Light),
        Font(Res.font.JetBrainsMono_Regular, FontWeight.Normal),
        Font(Res.font.JetBrainsMono_Medium, FontWeight.Medium),
        Font(Res.font.JetBrainsMono_SemiBold, FontWeight.SemiBold),
        Font(Res.font.JetBrainsMono_Bold, FontWeight.Bold),
    )

val interFontFamily
    @Composable get() = FontFamily(
        Font(Res.font.Inter_24pt_Light, FontWeight.Light),
        Font(Res.font.Inter_24pt_Regular, FontWeight.Normal),
        Font(Res.font.Inter_24pt_Medium, FontWeight.Medium),
        Font(Res.font.Inter_24pt_SemiBold, FontWeight.SemiBold),
        Font(Res.font.Inter_24pt_Bold, FontWeight.Bold),
        Font(Res.font.Inter_24pt_Black, FontWeight.Black),
    )

val baseline = Typography()

@Composable
fun getAppTypography(fontTheme: FontTheme = FontTheme.CUSTOM): Typography {
    return when (fontTheme) {
        FontTheme.SYSTEM -> baseline
        FontTheme.CUSTOM -> Typography(
            displayLarge = baseline.displayLarge.copy(fontFamily = jetbrainsMonoFontFamily),
            displayMedium = baseline.displayMedium.copy(fontFamily = jetbrainsMonoFontFamily),
            displaySmall = baseline.displaySmall.copy(fontFamily = jetbrainsMonoFontFamily),
            headlineLarge = baseline.headlineLarge.copy(fontFamily = jetbrainsMonoFontFamily),
            headlineMedium = baseline.headlineMedium.copy(fontFamily = jetbrainsMonoFontFamily),
            headlineSmall = baseline.headlineSmall.copy(fontFamily = jetbrainsMonoFontFamily),
            titleLarge = baseline.titleLarge.copy(fontFamily = jetbrainsMonoFontFamily),
            titleMedium = baseline.titleMedium.copy(fontFamily = jetbrainsMonoFontFamily),
            titleSmall = baseline.titleSmall.copy(fontFamily = jetbrainsMonoFontFamily),
            bodyLarge = baseline.bodyLarge.copy(fontFamily = interFontFamily),
            bodyMedium = baseline.bodyMedium.copy(fontFamily = interFontFamily),
            bodySmall = baseline.bodySmall.copy(fontFamily = interFontFamily),
            labelLarge = baseline.labelLarge.copy(fontFamily = interFontFamily),
            labelMedium = baseline.labelMedium.copy(fontFamily = interFontFamily),
            labelSmall = baseline.labelSmall.copy(fontFamily = interFontFamily),
        )
    }
}
