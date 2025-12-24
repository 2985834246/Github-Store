package zed.rainxch.githubstore.core.data

import zed.rainxch.githubstore.core.data.services.ApkInfoExtractor
import zed.rainxch.githubstore.core.domain.model.ApkPackageInfo

class DesktopApkInfoExtractor : ApkInfoExtractor {
    override suspend fun extractPackageInfo(filePath: String): ApkPackageInfo? {
        return null
    }
}