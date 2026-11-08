package tomeko.hylobby.quickjoin

import tomeko.hylobby.quickjoin.data.ButtonConfig
import tomeko.hylobby.quickjoin.data.Func
import tomeko.hylobby.quickjoin.data.GuiData
import tomeko.hylobby.quickjoin.favorites.FavoriteEntry
import tomeko.hylobby.quickjoin.favorites.FavoritesManager
import kotlin.math.abs

// Version independent helpers shared by every QuickJoin screen.
object QuickJoinLogic {
    const val MAIN_GUI = "QuickJoinGui"
    const val PREFIX = "§4[§6§lQUICKJOIN§4]§a: "

    const val ID_FAVORITES = 7000
    const val ID_SEARCH = 7001
    const val ID_CONFIG = 7002

    fun evaluateExpression(expression: String?, width: Int, height: Int): Int {
        if (expression.isNullOrEmpty()) return 0
        val replaced = expression
            .replace("centerx", (width / 2).toString())
            .replace("centery", (height / 2).toString())
        val parts = replaced.trim().split(" ")
        try {
            if (parts.size == 1) return parts[0].toInt()
            if (parts.size >= 3) {
                val left = parts[0].toInt()
                val right = parts[2].toInt()
                if (parts[1] == "+") return left + right
                if (parts[1] == "-") return left - right
            }
        } catch (e: NumberFormatException) {
        }
        return 0
    }

    fun gui(guiName: String?): GuiData? = if (guiName == null) null else QuickJoinLayout.get().guis[guiName]

    fun func(guiName: String?, buttonId: Int): Func? = gui(guiName)?.funcs?.get(buttonId.toString())

    fun button(gui: GuiData?, buttonId: Int): ButtonConfig? = gui?.buttons?.firstOrNull { it.id == buttonId }

    // Only playable entries can be favorited, categories that just open another menu cannot.
    fun canFavorite(guiName: String, buttonId: Int): Boolean {
        if (buttonId >= ID_FAVORITES) return false
        val func = func(guiName, buttonId)
        return func != null && func.type != "opengui"
    }

    fun parentTitle(guiName: String): String {
        val name = gui(guiName)?.name
        return if (guiName == MAIN_GUI || name == null) "Quickjoin" else name
    }

    fun childName(guiName: String, buttonId: Int): String = button(gui(guiName), buttonId)?.label ?: "$guiName#$buttonId"

    fun childColor(guiName: String, buttonId: Int): String = button(gui(guiName), buttonId)?.colorCode ?: "§f"

    // Search

    class SearchEntry(
        val guiName: String,
        val buttonId: Int,
        val parentTitle: String,
        val childName: String,
        val childColor: String
    ) {
        fun formattedLabel(): String = "§b§l$parentTitle §r§7>> $childColor$childName"
    }

    fun collectSearchEntries(): List<SearchEntry> {
        val entries = ArrayList<SearchEntry>()
        for ((guiName, gui) in QuickJoinLayout.get().guis) {
            val funcs = gui.funcs ?: continue
            for ((key, func) in funcs) {
                if (func.type == "opengui") continue
                val id = key.toIntOrNull() ?: continue
                entries.add(SearchEntry(guiName, id, parentTitle(guiName), childName(guiName, id), childColor(guiName, id)))
            }
        }
        return entries.sortedWith(compareBy({ it.parentTitle }, { it.childName }))
    }

    fun filterSearch(entries: List<SearchEntry>, query: String?): List<SearchEntry> {
        if (query == null || query.trim().isEmpty()) return ArrayList(entries)
        val q = query.trim().lowercase()
        return entries.filter { entry ->
            val parent = entry.parentTitle.lowercase()
            val child = entry.childName.lowercase()
            val combined = "${entry.parentTitle} >> ${entry.childName}".lowercase()
            containsOrFuzzy(parent, q) || containsOrFuzzy(child, q) || containsOrFuzzy(combined, q)
        }
    }

    private fun containsOrFuzzy(text: String, query: String): Boolean {
        if (text.contains(query)) return true
        if (abs(text.length - query.length) > 1) return false
        return levenshteinLeq1(text, query)
    }

    internal fun levenshteinLeq1(a: String, b: String): Boolean {
        if (a == b) return true
        val la = a.length
        val lb = b.length
        if (abs(la - lb) > 1) return false
        var i = 0
        var j = 0
        var edits = 0
        while (i < la && j < lb) {
            if (a[i] == b[j]) {
                i++
                j++
            } else {
                if (edits == 1) return false
                edits++
                if (la > lb) {
                    i++
                } else if (lb > la) {
                    j++
                } else {
                    i++
                    j++
                }
            }
        }
        if (i < la || j < lb) edits++
        return edits <= 1
    }

    // Favorites

    fun filterFavorites(query: String?): List<FavoriteEntry> {
        val favorites = FavoritesManager.getFavorites()
        if (query == null || query.trim().isEmpty()) return favorites
        val lower = query.lowercase()
        return favorites.filter { entry ->
            "${parentTitle(entry.guiName)} >> ${childName(entry.guiName, entry.buttonId)}".lowercase().contains(lower)
        }
    }

    fun favoriteLabel(entry: FavoriteEntry): String =
        "✮ §b§l${parentTitle(entry.guiName)} §r§7>> ${childColor(entry.guiName, entry.buttonId)}${childName(entry.guiName, entry.buttonId)}"
}
