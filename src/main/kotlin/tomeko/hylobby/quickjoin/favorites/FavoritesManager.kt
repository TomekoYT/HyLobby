package tomeko.hylobby.quickjoin.favorites

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

data class FavoriteEntry(var guiName: String, var buttonId: Int)

object FavoritesManager {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val favorites = LinkedHashSet<FavoriteEntry>()
    private var favoritesFile: Path? = null

    fun initialize(configDirectory: Path?) {
        if (configDirectory == null) return
        val dir = configDirectory.resolve("HyLobby")
        try {
            Files.createDirectories(dir)
        } catch (e: IOException) {
            favoritesFile = null
            return
        }
        favoritesFile = dir.resolve("quickjoin-favorites.json")
        load()
    }

    @Synchronized
    fun isFavorite(guiName: String, buttonId: Int): Boolean = FavoriteEntry(guiName, buttonId) in favorites

    // Returns true when the entry was added and false when it was removed.
    @Synchronized
    fun toggleFavorite(guiName: String, buttonId: Int): Boolean {
        val entry = FavoriteEntry(guiName, buttonId)
        val added = favorites.add(entry)
        if (!added) favorites.remove(entry)
        save()
        return added
    }

    @Synchronized
    fun getFavorites(): List<FavoriteEntry> = ArrayList(favorites)

    private fun load() {
        val file = favoritesFile ?: return
        if (!Files.exists(file)) return
        try {
            Files.newBufferedReader(file, StandardCharsets.UTF_8).use { reader ->
                val type = object : TypeToken<List<FavoriteEntry>>() {}.type
                val loaded: List<FavoriteEntry>? = gson.fromJson(reader, type)
                favorites.clear()
                loaded?.forEach { entry ->
                    @Suppress("SENSELESS_COMPARISON")
                    if (entry != null && entry.guiName != null) favorites.add(entry)
                }
            }
        } catch (e: Exception) {
            favorites.clear()
        }
    }

    private fun save() {
        val file = favoritesFile ?: return
        try {
            Files.newBufferedWriter(file, StandardCharsets.UTF_8).use { writer ->
                gson.toJson(ArrayList(favorites), writer)
            }
        } catch (ignored: Exception) {
        }
    }
}
