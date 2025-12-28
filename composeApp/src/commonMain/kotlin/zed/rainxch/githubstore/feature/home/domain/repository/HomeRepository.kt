package zed.rainxch.githubstore.feature.home.domain.repository

import kotlinx.coroutines.flow.Flow
import zed.rainxch.githubstore.feature.home.domain.model.PaginatedRepos

interface HomeRepository {
    fun getTrendingRepositories(page: Int): Flow<PaginatedRepos>
    fun getNew(page: Int): Flow<PaginatedRepos>
    fun getRecentlyUpdated(page: Int): Flow<PaginatedRepos>
}
