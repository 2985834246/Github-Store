package zed.rainxch.githubstore.feature.search.presentation

import zed.rainxch.githubstore.core.domain.model.GithubRepoSummary
import zed.rainxch.githubstore.feature.search.domain.model.ProgrammingLanguage
import zed.rainxch.githubstore.feature.search.domain.model.SearchPlatformType
import zed.rainxch.githubstore.feature.search.domain.model.SortBy

data class SearchState(
    val query: String = "",
    val repositories: List<SearchRepo> = emptyList(),
    val selectedSearchPlatformType: SearchPlatformType = SearchPlatformType.All,
    val selectedSortBy: SortBy = SortBy.BestMatch,
    val selectedLanguage: ProgrammingLanguage = ProgrammingLanguage.All,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val hasMorePages: Boolean = true,
    val totalCount: Int? = null,
    val isLanguageSheetVisible: Boolean = false
)

data class SearchRepo(
    val isInstalled: Boolean,
    val isUpdateAvailable: Boolean,
    val repo: GithubRepoSummary
)
