package zed.rainxch.githubstore.feature.details.presentation

import zed.rainxch.githubstore.core.domain.model.Architecture
import zed.rainxch.githubstore.core.domain.model.GithubRepoSummary
import zed.rainxch.githubstore.core.domain.model.GithubRelease
import zed.rainxch.githubstore.core.domain.model.GithubAsset
import zed.rainxch.githubstore.core.domain.model.GithubUserProfile
import zed.rainxch.githubstore.feature.details.domain.model.RepoStats

data class DetailsState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,

    val repository: GithubRepoSummary? = null,
    val latestRelease: GithubRelease? = null,
    val installableAssets: List<GithubAsset> = emptyList(),
    val primaryAsset: GithubAsset? = null,
    val userProfile: GithubUserProfile? = null,

    val stats: RepoStats? = null,
    val readmeMarkdown: String? = null,

    val installLogs: List<InstallLogItem> = emptyList(),

    val isDownloading: Boolean = false,
    val downloadProgressPercent: Int? = null,
    val isInstalling: Boolean = false,
    val downloadError: String? = null,
    val installError: String? = null,

    val downloadStage: DownloadStage = DownloadStage.IDLE,
    val systemArchitecture: Architecture = Architecture.UNKNOWN
)

data class InstallLogItem(
    val timeIso: String,
    val assetName: String,
    val assetSizeBytes: Long,
    val releaseTag: String,
    val result: String
)

enum class DownloadStage {
    IDLE, DOWNLOADING, VERIFYING, INSTALLING
}