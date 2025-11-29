package zed.rainxch.githubstore.feature.details.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import zed.rainxch.githubstore.core.domain.getPlatform
import zed.rainxch.githubstore.core.domain.model.Architecture
import zed.rainxch.githubstore.core.domain.model.GithubAsset
import zed.rainxch.githubstore.core.domain.model.PlatformType
import zed.rainxch.githubstore.core.domain.model.getSystemArchitecture
import zed.rainxch.githubstore.core.presentation.utils.openBrowser
import zed.rainxch.githubstore.feature.details.data.Downloader
import zed.rainxch.githubstore.feature.details.data.Installer
import zed.rainxch.githubstore.feature.details.domain.repository.DetailsRepository
import kotlin.time.Clock.System
import kotlin.time.ExperimentalTime

class DetailsViewModel(
    private val repositoryId: Int,
    private val detailsRepository: DetailsRepository,
    private val downloader: Downloader,
    private val installer: Installer,
) : ViewModel() {

    private var hasLoadedInitialData = false
    private var currentDownloadJob: Job? = null

    private val _state = MutableStateFlow(DetailsState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadInitial()

                hasLoadedInitialData = true
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            DetailsState()
        )

    private fun loadInitial() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, errorMessage = null)

                val repo = detailsRepository.getRepositoryById(repositoryId.toLong())
                val owner = repo.owner.login
                val name = repo.name

                _state.value = _state.value.copy(repository = repo)

                val latestReleaseDeferred = async {
                    try {
                        detailsRepository.getLatestPublishedRelease(owner, name)
                    } catch (t: Throwable) {
                        Logger.w { "Failed to load latest release: ${t.message}" }
                        null
                    }
                }

                val statsDeferred = async {
                    try {
                        detailsRepository.getRepoStats(owner, name)
                    } catch (t: Throwable) {
                        null
                    }
                }

                val readmeDeferred = async {
                    try {
                        detailsRepository.getReadme(owner, name)
                    } catch (t: Throwable) {
                        null
                    }
                }

                val userProfileDeferred = async {
                    try {
                        detailsRepository.getUserProfile(owner)
                    } catch (t: Throwable) {
                        Logger.w { "Failed to load user profile: ${t.message}" }
                        null
                    }
                }

                val latestRelease = latestReleaseDeferred.await()
                val stats = statsDeferred.await()
                val readme = readmeDeferred.await()
                val userProfile = userProfileDeferred.await()

                val platformType = getPlatform().type
                val installable = latestRelease?.assets?.filter { asset ->
                    isAssetInstallableForPlatform(asset.name, platformType)
                }.orEmpty()

                val primary = choosePrimaryAsset(installable, platformType)

                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = null,
                    repository = repo,
                    latestRelease = latestRelease,
                    stats = stats,
                    readmeMarkdown = readme,
                    installableAssets = installable,
                    primaryAsset = primary,
                    userProfile = userProfile,
                    systemArchitecture = getSystemArchitecture()
                )
            } catch (t: Throwable) {
                Logger.e { "Details load failed: ${t.message}" }
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = t.message ?: "Failed to load details"
                )
            }
        }
    }

    private fun isAssetInstallableForPlatform(nameRaw: String, platform: PlatformType): Boolean {
        val name = nameRaw.lowercase()
        val architecture = getSystemArchitecture()

        // First check if it's a valid file type for the platform
        val hasValidExtension = when (platform) {
            PlatformType.ANDROID -> name.endsWith(".apk")
            PlatformType.WINDOWS -> name.endsWith(".msi") || name.endsWith(".exe")
            PlatformType.MACOS -> name.endsWith(".dmg") || name.endsWith(".pkg")
            PlatformType.LINUX -> name.endsWith(".appimage") || name.endsWith(".deb") || name.endsWith(".rpm")
        }

        if (!hasValidExtension) return false

        // Then check architecture compatibility
        return isArchitectureCompatible(name, architecture)
    }

    private fun isArchitectureCompatible(assetName: String, systemArch: Architecture): Boolean {
        val name = assetName.lowercase()

        // If no architecture is specified in the filename, assume it's compatible
        val hasArchInName = listOf(
            "x86_64", "amd64", "x64",
            "aarch64", "arm64",
            "i386", "i686", "x86",
            "armv7", "arm"
        ).any { name.contains(it) }

        if (!hasArchInName) return true

        // Check if the asset architecture matches system architecture
        return when (systemArch) {
            Architecture.X86_64 -> {
                name.contains("x86_64") || name.contains("amd64") || name.contains("x64")
            }
            Architecture.AARCH64 -> {
                name.contains("aarch64") || name.contains("arm64")
            }
            Architecture.X86 -> {
                name.contains("i386") || name.contains("i686") || name.contains("x86")
            }
            Architecture.ARM -> {
                name.contains("armv7") || name.contains("arm")
            }
            Architecture.UNKNOWN -> true
        }
    }

    private fun choosePrimaryAsset(
        assets: List<GithubAsset>,
        platform: PlatformType
    ): GithubAsset? {
        if (assets.isEmpty()) return null

        val architecture = getSystemArchitecture()
        val priority = when (platform) {
            PlatformType.ANDROID -> listOf(".apk")
            PlatformType.WINDOWS -> listOf(".msi", ".exe")
            PlatformType.MACOS -> listOf(".dmg", ".pkg")
            PlatformType.LINUX -> listOf(".appimage", ".deb", ".rpm")
        }

        // First, prefer assets that match the system architecture
        val compatibleAssets = assets.filter { asset ->
            isArchitectureCompatible(asset.name.lowercase(), architecture)
        }

        // If we found architecture-specific assets, use those; otherwise use all assets
        val assetsToConsider = compatibleAssets.ifEmpty { assets }

        return assetsToConsider.maxByOrNull { asset ->
            val name = asset.name.lowercase()
            val idx = priority.indexOfFirst { name.endsWith(it) }
                .let { if (it == -1) 999 else it }

            // Boost score if architecture matches exactly
            val archBoost = if (isExactArchitectureMatch(name, architecture)) 10000 else 0

            archBoost + (-1000 * (priority.size - idx)) + asset.size
        }
    }

    private fun isExactArchitectureMatch(assetName: String, systemArch: Architecture): Boolean {
        val name = assetName.lowercase()
        return when (systemArch) {
            Architecture.X86_64 -> name.contains("x86_64") || name.contains("amd64") || name.contains("x64")
            Architecture.AARCH64 -> name.contains("aarch64") || name.contains("arm64")
            Architecture.X86 -> name.contains("i386") || name.contains("i686")
            Architecture.ARM -> name.contains("armv7") || name.contains("arm")
            Architecture.UNKNOWN -> false
        }
    }

    fun onAction(action: DetailsAction) {
        when (action) {
            DetailsAction.Retry -> {
                hasLoadedInitialData = false
                loadInitial()
            }

            DetailsAction.InstallPrimary -> {
                val primary = _state.value.primaryAsset
                val release = _state.value.latestRelease
                if (primary != null && release != null) {
                    installAsset(
                        downloadUrl = primary.downloadUrl,
                        assetName = primary.name,
                        sizeBytes = primary.size,
                        releaseTag = release.tagName
                    )
                }
            }

            is DetailsAction.DownloadAsset -> {
                val release = _state.value.latestRelease
                downloadAsset(
                    downloadUrl = action.downloadUrl,
                    assetName = action.assetName,
                    sizeBytes = action.sizeBytes,
                    releaseTag = release?.tagName ?: ""
                )
            }

            DetailsAction.CancelCurrentDownload -> {
                currentDownloadJob?.cancel()
                currentDownloadJob = null
                _state.value = _state.value.copy(
                    isDownloading = false,
                    downloadProgressPercent = null
                )
            }

            DetailsAction.OpenRepoInBrowser -> {
                _state.value.repository?.htmlUrl?.let { openBrowser(it) }
            }

            DetailsAction.OpenAuthorInBrowser -> {
                _state.value.userProfile?.htmlUrl?.let { openBrowser(it) }
            }

            DetailsAction.OnNavigateBackClick -> { /* handled in UI host */ }

            is DetailsAction.OpenAuthorInApp -> { /* handled in UI host */ }
        }
    }

    private fun installAsset(
        downloadUrl: String,
        assetName: String,
        sizeBytes: Long,
        releaseTag: String
    ) {
        currentDownloadJob?.cancel()
        currentDownloadJob = viewModelScope.launch {
            try {
                appendLog(assetName, sizeBytes, releaseTag, "DownloadStarted")
                _state.value = _state.value.copy(
                    downloadError = null,
                    installError = null,
                    downloadProgressPercent = null
                )

                installer.ensurePermissionsOrThrow(assetName.substringAfterLast('.', "").lowercase())

                _state.value = _state.value.copy(downloadStage = DownloadStage.DOWNLOADING)

                downloader.download(downloadUrl, assetName).collect { p ->
                    _state.value = _state.value.copy(downloadProgressPercent = p.percent)
                    if (p.percent == 100) {
                        _state.value = _state.value.copy(downloadStage = DownloadStage.VERIFYING)
                    }
                }

                val filePath = downloader.saveToFile(downloadUrl, assetName)
                appendLog(assetName, sizeBytes, releaseTag, "Downloaded")

                _state.value = _state.value.copy(downloadStage = DownloadStage.INSTALLING)
                val ext = assetName.substringAfterLast('.', "").lowercase()

                if (!installer.isSupported(ext)) {
                    throw IllegalStateException("Asset type .$ext not supported")
                }

                installer.install(filePath, ext)

                _state.value = _state.value.copy(downloadStage = DownloadStage.IDLE)
                appendLog(assetName, sizeBytes, releaseTag, "Installed")

            } catch (t: Throwable) {
                Logger.e { "Install failed: ${t.message}" }
                _state.value = _state.value.copy(
                    downloadStage = DownloadStage.IDLE,
                    installError = t.message
                )
                appendLog(assetName, sizeBytes, releaseTag, "Error: ${t.message}")
            }
        }
    }

    private fun downloadAsset(
        downloadUrl: String,
        assetName: String,
        sizeBytes: Long,
        releaseTag: String
    ) {
        currentDownloadJob?.cancel()
        currentDownloadJob = viewModelScope.launch {
            try {
                appendLog(assetName, sizeBytes, releaseTag, "DownloadStarted")
                _state.value = _state.value.copy(
                    isDownloading = true,
                    downloadError = null,
                    installError = null,
                    downloadProgressPercent = null
                )

                try {
                    downloader.download(downloadUrl, assetName).collect { p ->
                        _state.value = _state.value.copy(downloadProgressPercent = p.percent)
                    }
                } catch (_: Throwable) { }

                downloader.saveToFile(downloadUrl, assetName)
                _state.value = _state.value.copy(isDownloading = false)
                appendLog(assetName, sizeBytes, releaseTag, "Downloaded")

            } catch (t: Throwable) {
                _state.value = _state.value.copy(
                    isDownloading = false,
                    downloadError = t.message
                )
                appendLog(assetName, sizeBytes, releaseTag, "Error: ${t.message}")
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun appendLog(assetName: String, size: Long, tag: String, result: String) {
        val now = System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            .format(LocalDateTime.Format {
                year()
                char('-')
                monthNumber()
                char('-')
                day()
                char(' ')
                hour()
                char(':')
                minute()
                char(':')
                second()
            })
        val newItem = InstallLogItem(
            timeIso = now,
            assetName = assetName,
            assetSizeBytes = size,
            releaseTag = tag,
            result = result
        )
        _state.value = _state.value.copy(
            installLogs = listOf(newItem) + _state.value.installLogs
        )
    }

    override fun onCleared() {
        super.onCleared()
        currentDownloadJob?.cancel()
    }


}