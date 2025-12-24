package zed.rainxch.githubstore.core.data.services

import zed.rainxch.githubstore.core.domain.model.ApkPackageInfo

interface ApkInfoExtractor {
    suspend fun extractPackageInfo(filePath: String): ApkPackageInfo?
}