package zed.rainxch.githubstore.feature.details.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import zed.rainxch.githubstore.core.domain.model.Architecture
import zed.rainxch.githubstore.core.domain.model.GithubAsset
import zed.rainxch.githubstore.feature.details.presentation.DetailsState
import zed.rainxch.githubstore.feature.details.presentation.DownloadStage
import zed.rainxch.githubstore.feature.details.presentation.utils.extractArchitectureFromName
import zed.rainxch.githubstore.feature.details.presentation.utils.isExactArchitectureMatch

@Composable
fun SmartInstallButton(
    isDownloading: Boolean,
    isInstalling: Boolean,
    progress: Int?,
    primaryAsset: GithubAsset?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: DetailsState
) {
    val enabled = primaryAsset != null && !isDownloading && !isInstalling

    val animatedProgress by animateFloatAsState(
        targetValue = (progress ?: 0) / 100f,
        animationSpec = tween(durationMillis = 500)
    )

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(CircleShape)
            .background(
                color = if (enabled) {
                    MaterialTheme.colorScheme.primary
                } else MaterialTheme.colorScheme.surfaceContainer,
                shape = CircleShape
            )
            .clickable(enabled = enabled, onClick = onClick),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (enabled) {
                MaterialTheme.colorScheme.primary
            } else MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (state.isDownloading || state.downloadStage != DownloadStage.IDLE) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (state.downloadStage) {
                        DownloadStage.DOWNLOADING -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                LinearProgressIndicator(
                                    progress = { animatedProgress },
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                    modifier = Modifier.fillMaxSize(),
                                )

                                Text(
                                    text = "Downloading... ${progress ?: 0}%",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        DownloadStage.VERIFYING -> {
                            CircularProgressIndicator()

                            Text(
                                text = "Verifying app...",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        DownloadStage.INSTALLING -> {
                            CircularProgressIndicator()

                            Text(
                                text = "Installing...",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        DownloadStage.IDLE -> {}
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (primaryAsset != null) {
                            "Install latest"
                        } else "Not Available",
                        color = if (enabled) {
                            MaterialTheme.colorScheme.onPrimary
                        } else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Show architecture info
                    if (primaryAsset != null) {
                        val assetArch = extractArchitectureFromName(primaryAsset.name)
                        val systemArch = state.systemArchitecture

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = assetArch ?: systemArch.name.lowercase(),
                                color = if (enabled) {
                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                },
                                style = MaterialTheme.typography.bodySmall
                            )

                            if (assetArch != null && isExactArchitectureMatch(primaryAsset.name.lowercase(), systemArch)) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Architecture compatible",
                                    tint = if (enabled) {
                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                    } else {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                    },
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}