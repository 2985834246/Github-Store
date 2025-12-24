package zed.rainxch.githubstore.feature.details.data.mappers

import zed.rainxch.githubstore.core.domain.model.GithubAsset
import zed.rainxch.githubstore.core.domain.model.GithubUser
import zed.rainxch.githubstore.feature.details.data.dto.AssetNetwork

fun AssetNetwork.toDomain(): GithubAsset = GithubAsset(
    id = id,
    name = name,
    contentType = contentType,
    size = size,
    downloadUrl = downloadUrl,
    uploader = GithubUser(
        id = uploader.id,
        login = uploader.login,
        avatarUrl = uploader.avatarUrl,
        htmlUrl = uploader.htmlUrl
    )
)