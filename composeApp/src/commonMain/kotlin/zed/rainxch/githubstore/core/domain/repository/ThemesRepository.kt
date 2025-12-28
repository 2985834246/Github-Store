package zed.rainxch.githubstore.core.domain.repository

import kotlinx.coroutines.flow.Flow
import zed.rainxch.githubstore.core.presentation.model.AppTheme
import zed.rainxch.githubstore.core.presentation.model.FontTheme

interface ThemesRepository {
    fun getThemeColor(): Flow<AppTheme>
    suspend fun setThemeColor(theme: AppTheme)
    fun getAmoledTheme(): Flow<Boolean>
    suspend fun setAmoledTheme(enabled: Boolean)
    fun getFontTheme(): Flow<FontTheme>
    suspend fun setFontTheme(fontTheme: FontTheme)
}