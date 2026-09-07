package mihon.core.migration.migrations

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import logcat.LogPriority
import mihon.core.migration.Migration
import mihon.core.migration.MigrationContext
import mihon.domain.extension.repository.ExtensionStoreRepository
import tachiyomi.core.common.util.lang.withIOContext
import tachiyomi.core.common.util.system.logcat

@Inject
@ContributesIntoSet(AppScope::class)
class DefaultExtensionStoreMigration(
    private val repository: ExtensionStoreRepository,
) : Migration {
    override val version: Float = Migration.ALWAYS

    override suspend fun invoke(migrationContext: MigrationContext): Boolean = withIOContext {
        try {
            val stores = repository.getAll()
            if (stores.none { it.indexUrl.contains("keiyoushi") }) {
                repository.insert("https://raw.githubusercontent.com/keiyoushi/extensions/repo/repo.json")
            }
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e) { "Error inserting default Keiyoushi repo" }
        }
        return@withIOContext true
    }
}
