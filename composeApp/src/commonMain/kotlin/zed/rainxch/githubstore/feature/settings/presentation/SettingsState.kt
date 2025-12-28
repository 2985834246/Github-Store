package zed.rainxch.githubstore.feature.settings.presentation

import zed.rainxch.githubstore.core.presentation.model.AppTheme
import zed.rainxch.githubstore.core.presentation.model.FontTheme

data class SettingsState(
    val selectedThemeColor: AppTheme = AppTheme.OCEAN,
    val selectedFontTheme: FontTheme = FontTheme.CUSTOM,
    val isLogoutDialogVisible: Boolean = false,
    val isUserLoggedIn: Boolean = false,
    val isAmoledThemeEnabled: Boolean = false,
)