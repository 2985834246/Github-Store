package zed.rainxch.githubstore.feature.details.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import zed.rainxch.githubstore.core.domain.model.GithubRelease
import zed.rainxch.githubstore.core.domain.model.GithubRepoSummary
import zed.rainxch.githubstore.core.domain.model.GithubUserProfile
import zed.rainxch.githubstore.feature.details.presentation.DownloadStage

@Composable
fun AppHeader(
    author: GithubUserProfile?,
    repository: GithubRepoSummary,
    release: GithubRelease?,
    modifier: Modifier = Modifier,
    downloadStage: DownloadStage = DownloadStage.IDLE,
    downloadProgress: Int? = null
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (downloadProgress ?: 0) / 100f,
        animationSpec = tween(durationMillis = 500),
        label = "avatar_progress_animation"
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            // Avatar with circular progress
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(100.dp)
            ) {
                // The avatar image
                AsyncImage(
                    model = author?.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )

                // Progress ring overlay - only show when downloading
                if (downloadStage != DownloadStage.IDLE) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(100.dp)
                    ) {
                        when (downloadStage) {
                            DownloadStage.DOWNLOADING -> {
                                // Background circle (track)
                                CircularProgressIndicator(
                                    progress = { 1f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    strokeWidth = 4.dp,
                                )

                                // Progress circle
                                CircularProgressIndicator(
                                    progress = { animatedProgress },
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 4.dp,
                                    strokeCap = StrokeCap.Round
                                )
                            }

                            DownloadStage.VERIFYING, DownloadStage.INSTALLING -> {
                                // Indeterminate spinner for verifying/installing
                                CircularProgressIndicator(
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 4.dp,
                                    strokeCap = StrokeCap.Round
                                )
                            }

                            else -> { }
                        }
                    }
                }
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = repository.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "by ${author?.login}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "${release?.tagName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = repository.description ?: "No description provided.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}