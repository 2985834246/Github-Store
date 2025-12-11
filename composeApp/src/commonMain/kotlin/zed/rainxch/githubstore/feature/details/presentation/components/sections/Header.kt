package zed.rainxch.githubstore.feature.details.presentation.components.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import zed.rainxch.githubstore.feature.details.presentation.DetailsAction
import zed.rainxch.githubstore.feature.details.presentation.DetailsState
import zed.rainxch.githubstore.feature.details.presentation.components.AppHeader
import zed.rainxch.githubstore.feature.details.presentation.components.SmartInstallButton
import zed.rainxch.githubstore.feature.details.presentation.utils.LocalTopbarLiquidState

fun LazyListScope.header(
    state: DetailsState,
    onAction: (DetailsAction) -> Unit,
) {
    item {
        val liquidState = LocalTopbarLiquidState.current

        if (state.repository != null) {
            AppHeader(
                author = state.userProfile,
                release = state.latestRelease,
                repository = state.repository,
                modifier = Modifier.liquefiable(liquidState)
            )
        }
    }

    item {
        val liquidState = LocalTopbarLiquidState.current

        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            SmartInstallButton(
                isDownloading = state.isDownloading,
                isInstalling = state.isInstalling,
                progress = state.downloadProgressPercent,
                primaryAsset = state.primaryAsset,
                state = state,
                onAction = onAction,
            )

            DropdownMenu(
                expanded = state.isInstallDropdownExpanded,
                onDismissRequest = {
                    onAction(DetailsAction.OnToggleInstallDropdown)
                },
                offset = DpOffset(x = 0.dp, y = 20.dp)
            ) {
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = "Open in Obtainium",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Manage updates automatically",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onAction(DetailsAction.OpenInObtainium)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Update,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    modifier = Modifier.liquefiable(liquidState)
                )
            }
        }
    }
}