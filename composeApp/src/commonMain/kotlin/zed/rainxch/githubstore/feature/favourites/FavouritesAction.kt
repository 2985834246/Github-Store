package zed.rainxch.githubstore.feature.favourites

import zed.rainxch.githubstore.feature.favourites.model.FavouriteRepository

sealed interface FavouritesAction {
    data object OnNavigateBackClick : FavouritesAction
    data class OnToggleFavorite(val favouriteRepository: FavouriteRepository) : FavouritesAction
    data class OnRepositoryClick(val favouriteRepository: FavouriteRepository) : FavouritesAction
}