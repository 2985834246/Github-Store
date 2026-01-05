package zed.rainxch.githubstore.feature.favourites

data class FavouritesState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)