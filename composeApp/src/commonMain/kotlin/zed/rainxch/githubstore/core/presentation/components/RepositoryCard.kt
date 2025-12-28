package zed.rainxch.githubstore.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import githubstore.composeapp.generated.resources.Res
import githubstore.composeapp.generated.resources.home_view_details
import githubstore.composeapp.generated.resources.installed
import githubstore.composeapp.generated.resources.update_available
import githubstore.composeapp.generated.resources.updated_yesterday
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import zed.rainxch.githubstore.core.domain.model.GithubRepoSummary
import zed.rainxch.githubstore.core.domain.model.GithubUser
import zed.rainxch.githubstore.core.presentation.theme.GithubStoreTheme
import zed.rainxch.githubstore.core.presentation.utils.formatUpdatedAt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RepositoryCard(
    repository: GithubRepoSummary,
    isInstalled: Boolean,
    isUpdateAvailable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = repository.owner.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = repository.owner.login,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outline
                )

                Text(
                    text = "/ ${repository.name}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outline,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = repository.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            repository.description?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "⭐ ${repository.stargazersCount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "• 🌴 ${repository.forksCount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                repository.language?.let {
                    Text(
                        text = "• $it",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isInstalled) {
                Spacer(Modifier.height(12.dp))

                InstallStatusBadge(
                    isUpdateAvailable = isUpdateAvailable
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = formatUpdatedAt(repository.updatedAt),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(Modifier.height(24.dp))

            GithubStoreButton(
                text = stringResource(Res.string.home_view_details),
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun InstallStatusBadge(
    isUpdateAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isUpdateAvailable) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    val textColor = if (isUpdateAvailable) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    val icon = if (isUpdateAvailable) {
        Icons.Default.Update
    } else {
        Icons.Default.CheckCircle
    }

    val text = if (isUpdateAvailable) {
        stringResource(Res.string.update_available)
    } else {
        stringResource(Res.string.installed)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = textColor
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview
@Composable
fun RepositoryCardPreview() {
    GithubStoreTheme {
        RepositoryCard(
            isInstalled = false,
            repository = GithubRepoSummary(
                id = 0L,
                name = "Hello",
                fullName = "JIFEOJEF",
                owner = GithubUser(
                    id = 0L,
                    login = "Skydoves",
                    avatarUrl = "ewfew",
                    htmlUrl = "grgrre"
                ),
                description = "Hello wolrd Hello wolrd Hello wolrd Hello wolrd Hello wolrd",
                htmlUrl = "",
                stargazersCount = 20,
                forksCount = 4,
                language = "Kotlin",
                topics = null,
                releasesUrl = "",
                updatedAt = "",
                defaultBranch = ""
            ),
            onClick = { },
            isUpdateAvailable = true
        )
    }
}