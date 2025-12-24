package zed.rainxch.githubstore.core.data.services

interface FileLocationsProvider {
    fun appDownloadsDir(): String
    fun userDownloadsDir(): String
    fun setExecutableIfNeeded(path: String)
}
