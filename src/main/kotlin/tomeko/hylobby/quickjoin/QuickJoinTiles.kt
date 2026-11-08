package tomeko.hylobby.quickjoin

import tomeko.hylobby.config.HyLobbyConfig
import tomeko.hylobby.quickjoin.data.ButtonConfig
import tomeko.hylobby.quickjoin.data.Func

// Builds the grid of tiles ("Choose a Game" style) for a QuickJoin menu. Only positions and texts are computed
// here, the screens of each Minecraft version draw the tiles.
object QuickJoinTiles {
    const val TILE_HEIGHT = 22
    const val ICON_SIZE = 22
    const val ICON_SHEET_SIZE = 66
    const val GAP = 6
    const val MAX_TILE_WIDTH = 100
    const val MAX_COLUMNS = 4
    const val TITLE_SPACE = 20

    private val NAMES = mapOf(
        "BEDWARS" to "Bed Wars",
        "SKYWARS" to "SkyWars",
        "SKYBLOCK" to "SkyBlock",
        "CVC" to "Cops and Crims",
        "BUILDBATTLE" to "Build Battle",
        "TNT" to "TNT Games",
        "CLASSIC" to "Classic Games",
        "SMASH" to "Smash Heroes",
        "MURDER" to "Murder Mystery",
        "PIT" to "The Pit",
        "MAIN LOBBY" to "Lobby"
    )
    private val ACRONYMS = setOf("UHC", "TNT", "SG", "OP", "CTF", "PVP")

    class Tile(
        val id: Int,
        val name: String,
        val subtitle: String,
        val colorCode: String,
        val hasIcon: Boolean,
        val sheet: Int,
        val u: Int,
        val v: Int
    ) {
        var x = 0
        var y = 0
        var width = 0

        fun contains(mouseX: Int, mouseY: Int): Boolean =
            mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + TILE_HEIGHT

        fun initial(): String {
            val plain = stripCodes(name).trim()
            return if (plain.isEmpty()) "?" else plain.substring(0, 1).uppercase()
        }
    }

    class Grid {
        var title = ""
        var titleY = 0
        var centerX = 0
        val tiles = ArrayList<Tile>()

        fun tileAt(mouseX: Int, mouseY: Int): Tile? = tiles.firstOrNull { it.contains(mouseX, mouseY) }
    }

    fun build(guiName: String, screenWidth: Int, screenHeight: Int): Grid {
        val grid = Grid()
        grid.centerX = screenWidth / 2
        val gui = QuickJoinLogic.gui(guiName) ?: return grid
        val guiButtons = gui.buttons ?: return grid

        val main = guiName == QuickJoinLogic.MAIN_GUI
        grid.title = if (main) "Choose a Game" else displayName(gui.name)

        // Entries are grouped by kind and keep the reading order of the original layout inside a group.
        val buttons = guiButtons.sortedWith(Comparator { a, b ->
            val rankA = rank(guiName, a, main)
            val rankB = rank(guiName, b, main)
            if (rankA != rankB) return@Comparator rankA.compareTo(rankB)
            val ay = QuickJoinLogic.evaluateExpression(a.y, screenWidth, screenHeight)
            val by = QuickJoinLogic.evaluateExpression(b.y, screenWidth, screenHeight)
            if (ay != by) return@Comparator ay.compareTo(by)
            QuickJoinLogic.evaluateExpression(a.x, screenWidth, screenHeight)
                .compareTo(QuickJoinLogic.evaluateExpression(b.x, screenWidth, screenHeight))
        })

        for (button in buttons) {
            val func = QuickJoinLogic.func(guiName, button.id)
            val iconSource = if (main) {
                if (func != null && func.type == "opengui") QuickJoinLogic.gui(func.string) else null
            } else {
                gui
            }
            val icon = iconSource?.icon
            val hasIcon = icon != null && !isBack(func)
            grid.tiles.add(
                Tile(
                    button.id,
                    displayName(button.label),
                    subtitle(func, main),
                    button.colorCode ?: "§f",
                    hasIcon,
                    if (hasIcon) icon!!.sheet else 0,
                    if (hasIcon) icon!!.textureX * ICON_SIZE / 76 else 0,
                    if (hasIcon) icon!!.textureY * ICON_SIZE / 76 else 0
                )
            )
        }

        val count = grid.tiles.size
        if (count == 0) return grid

        var columns = minOf(MAX_COLUMNS, count)
        var tileWidth = MAX_TILE_WIDTH
        while (columns > 1 && columns * tileWidth + (columns - 1) * GAP > screenWidth - 16) {
            if (tileWidth > 70) {
                tileWidth -= 5
            } else {
                columns--
            }
        }
        val rows = (count + columns - 1) / columns
        val gridWidth = columns * tileWidth + (columns - 1) * GAP
        val gridHeight = rows * TILE_HEIGHT + (rows - 1) * GAP

        val top = (screenHeight - (gridHeight + TITLE_SPACE)) / 2
        grid.titleY = maxOf(top, 4)
        val startX = (screenWidth - gridWidth) / 2
        val startY = grid.titleY + TITLE_SPACE

        for ((i, tile) in grid.tiles.withIndex()) {
            tile.x = startX + (i % columns) * (tileWidth + GAP)
            tile.y = startY + (i / columns) * (TILE_HEIGHT + GAP)
            tile.width = tileWidth
        }
        return grid
    }

    private fun isBack(func: Func?): Boolean = func != null && func.type == "opengui" && func.string == QuickJoinLogic.MAIN_GUI

    // Main menu: lobby first, then the games, then single commands, Limbo and exit. Sub menus: back entry last.
    private fun rank(guiName: String, button: ButtonConfig, main: Boolean): Int {
        val func = QuickJoinLogic.func(guiName, button.id)
        if (!main) return if (isBack(func)) 1 else 0
        if (button.label?.trim().equals("MAIN LOBBY", ignoreCase = true)) return 0
        return when (func?.type) {
            null -> 2
            "opengui" -> 1
            "command" -> 2
            "repeat" -> 3
            else -> 4
        }
    }

    private fun subtitle(func: Func?, main: Boolean): String {
        val type = func?.type ?: return ""
        if (isBack(func)) return "> Go Back"
        return when (type) {
            "opengui" -> "Select Mode"
            "repeat" -> "> Go To Limbo"
            "close" -> "> Close"
            "chat" -> "> Info"
            else -> {
                val command = func.string
                if (main || (command != null && (command.startsWith("/lobby") || command.startsWith("/home")))) {
                    "> Go To Lobby"
                } else {
                    "> Click To Play"
                }
            }
        }
    }

    // "BLITZ SG" -> "Blitz SG", "BEDWARS" -> "Bed Wars"
    fun displayName(raw: String?): String {
        if (raw == null) return ""
        val trimmed = raw.trim()
        NAMES[trimmed.uppercase()]?.let { return it }

        val out = StringBuilder()
        for (word in trimmed.split(" ")) {
            if (word.isEmpty()) continue
            if (out.isNotEmpty()) out.append(' ')
            val upper = word.uppercase()
            if (upper in ACRONYMS) {
                out.append(upper)
            } else if (word.any { it.isDigit() } || word == "-") {
                out.append(word.lowercase())
            } else {
                out.append(upper[0]).append(upper.substring(1).lowercase())
            }
        }
        return out.toString()
    }

    fun stripCodes(text: String): String = text.replace(Regex("§."), "")

    // ARGB color of a legacy color code, used for the placeholder of entries without artwork.
    fun colorOf(code: String?): Int {
        if (code == null || code.length < 2) return 0xFFFFFFFF.toInt()
        return when (code[1].lowercaseChar()) {
            '0' -> 0xFF555555
            '1' -> 0xFF5555FF
            '2' -> 0xFF00AA00
            '3' -> 0xFF00AAAA
            '4' -> 0xFFAA0000
            '5' -> 0xFFAA00AA
            '6' -> 0xFFFFAA00
            '7' -> 0xFFAAAAAA
            '8' -> 0xFF777777
            '9' -> 0xFF5555FF
            'a' -> 0xFF55FF55
            'b' -> 0xFF55FFFF
            'c' -> 0xFFFF5555
            'd' -> 0xFFFF55FF
            'e' -> 0xFFFFFF55
            else -> 0xFFFFFFFF
        }.toInt()
    }

    fun nameWithStyle(tile: Tile, favorite: Boolean): String {
        val color = if (HyLobbyConfig.quickJoinColoredButtons) tile.colorCode else "§f"
        val bold = if (HyLobbyConfig.quickJoinBoldButtons) "§l" else ""
        return (if (favorite) "§6✮ §r" else "") + color + bold + tile.name
    }
}
