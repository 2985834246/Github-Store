package zed.rainxch.githubstore.feature.details.presentation.components.sections

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.coil3.Coil3ImageTransformerImpl
import com.mikepenz.markdown.compose.Markdown
import io.github.fletchmckee.liquid.liquefiable
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import zed.rainxch.githubstore.feature.details.presentation.utils.LocalTopbarLiquidState
import zed.rainxch.githubstore.feature.details.presentation.utils.rememberMarkdownColors
import zed.rainxch.githubstore.feature.details.presentation.utils.rememberMarkdownTypography

fun LazyListScope.about(readmeMarkdown: String) {
    item {
        val liquidState = LocalTopbarLiquidState.current

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Spacer(Modifier.height(16.dp))

        Text(
            text = "About this app",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .liquefiable(liquidState)
        )
    }

    item {
        val liquidState = LocalTopbarLiquidState.current

        Surface(
            color = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            val colors = rememberMarkdownColors()
            val typography = rememberMarkdownTypography()
            val flavour = remember { GFMFlavourDescriptor() }

            Markdown(
                content = readmeMarkdown,
                colors = colors,
                typography = typography,
                flavour = flavour,
                imageTransformer = Coil3ImageTransformerImpl,
                modifier = Modifier
                    .fillMaxWidth()
                    .liquefiable(liquidState),
            )
        }
    }
}