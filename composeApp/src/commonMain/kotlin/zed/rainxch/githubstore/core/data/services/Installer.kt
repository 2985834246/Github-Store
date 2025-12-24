package zed.rainxch.githubstore.core.data.services

import zed.rainxch.githubstore.core.domain.model.Architecture
import zed.rainxch.githubstore.core.domain.model.GithubAsset

interface Installer {
    suspend fun isSupported(extOrMime: String): Boolean

    suspend fun ensurePermissionsOrThrow(extOrMime: String)

    suspend fun install(filePath: String, extOrMime: String)

    fun isAssetInstallable(assetName: String): Boolean
    fun choosePrimaryAsset(assets: List<GithubAsset>): GithubAsset?
    fun detectSystemArchitecture(): Architecture
    fun isObtainiumInstalled(): Boolean
    fun openInObtainium(
        repoOwner: String,
        repoName: String,
        onOpenInstaller: () -> Unit
    )

    fun isAppManagerInstalled(): Boolean
    fun openInAppManager(
        filePath: String,
        onOpenInstaller: () -> Unit
    )

    fun getApkInfoExtractor(): ApkInfoExtractor
}