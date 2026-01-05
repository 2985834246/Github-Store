package zed.rainxch.githubstore.feature.favourites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import zed.rainxch.githubstore.core.presentation.theme.GithubStoreTheme

@Composable
fun FavouritesRoot(
    viewModel: FavouritesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    FavouritesScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun FavouritesScreen(
    state: FavouritesState,
    onAction: (FavouritesAction) -> Unit,
) {

}

@Preview
@Composable
private fun Preview() {
    GithubStoreTheme {
        FavouritesScreen(
            state = FavouritesState(),
            onAction = {}
        )
    }
}