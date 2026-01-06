package zed.rainxch.githubstore.core.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import zed.rainxch.githubstore.core.data.local.db.dao.FavoriteRepoDao
import zed.rainxch.githubstore.core.data.local.db.dao.InstalledAppDao
import zed.rainxch.githubstore.core.data.local.db.dao.UpdateHistoryDao
import zed.rainxch.githubstore.core.data.local.db.entities.FavoriteRepo
import zed.rainxch.githubstore.core.data.local.db.entities.InstalledApp
import zed.rainxch.githubstore.core.data.local.db.entities.UpdateHistory

@Database(
    entities = [
        InstalledApp::class,
        FavoriteRepo::class,
        UpdateHistory::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract val installedAppDao: InstalledAppDao
    abstract val favoriteRepoDao: FavoriteRepoDao
    abstract val updateHistoryDao: UpdateHistoryDao
}