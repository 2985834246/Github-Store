package zed.rainxch.githubstore.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import zed.rainxch.githubstore.core.domain.repository.ThemesRepository
import zed.rainxch.githubstore.core.presentation.model.AppTheme
import zed.rainxch.githubstore.core.presentation.model.FontTheme

class ThemesRepositoryImpl(
    private val preferences: DataStore<Preferences>
) : ThemesRepository {
    private val THEME_KEY = stringPreferencesKey("app_theme")
    private val AMOLED_KEY = booleanPreferencesKey("amoled_theme")
    private val FONT_KEY = stringPreferencesKey("font_theme")

    override fun getThemeColor(): Flow<AppTheme> {
        return preferences.data.map { prefs ->
            val themeName = prefs[THEME_KEY]
            AppTheme.fromName(themeName)
        }
    }

    override suspend fun setThemeColor(theme: AppTheme) {
        preferences.edit { prefs ->
            prefs[THEME_KEY] = theme.name
        }
    }

    override fun getAmoledTheme(): Flow<Boolean> {
        return preferences.data.map { prefs ->
            prefs[AMOLED_KEY] ?: false
        }
    }

    override suspend fun setAmoledTheme(enabled: Boolean) {
        preferences.edit { prefs ->
            prefs[AMOLED_KEY] = enabled
        }
    }

    override fun getFontTheme(): Flow<FontTheme> {
        return preferences.data.map { prefs ->
            val fontName = prefs[FONT_KEY]
            FontTheme.fromName(fontName)
        }
    }

    override suspend fun setFontTheme(fontTheme: FontTheme) {
        preferences.edit { prefs ->
            prefs[FONT_KEY] = fontTheme.name
        }
    }
}