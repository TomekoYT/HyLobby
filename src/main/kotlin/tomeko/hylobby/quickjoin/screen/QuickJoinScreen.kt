package tomeko.hylobby.quickjoin.screen

//? if 1.8.9 {
/*import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import tomeko.hylobby.commands.HyLobbyCommand
import tomeko.hylobby.config.HyLobbyConfig
import tomeko.hylobby.quickjoin.QuickJoinCompat
import tomeko.hylobby.quickjoin.QuickJoinLogic
import tomeko.hylobby.quickjoin.QuickJoinTiles
import tomeko.hylobby.quickjoin.favorites.FavoritesManager
import tomeko.hylobby.utils.Debug

private val OVERLAY = 0x80000000.toInt()
private val TILE = 0x90181818.toInt()
private val TILE_HOVER = 0xB0505050.toInt()
private val PLACEHOLDER_BORDER = 0xFF5A5A5A.toInt()
private val PLACEHOLDER_FILL = 0xFF262626.toInt()

class QuickJoinScreen(private val guiName: String) : GuiScreen() {
    private var grid = QuickJoinTiles.Grid()

    override fun initGui() {
        buttonList.clear()
        grid = QuickJoinTiles.build(guiName, width, height)

        if (guiName == QuickJoinLogic.MAIN_GUI) {
            var x = width - 90
            if (HyLobbyConfig.quickJoinEnableFavorites) {
                buttonList.add(GuiButton(QuickJoinLogic.ID_FAVORITES, x, 8, 80, 20, "§6§lFavorites"))
                x -= 90
            }
            buttonList.add(GuiButton(QuickJoinLogic.ID_SEARCH, x, 8, 80, 20, "§a§lSearch"))
            x -= 90
            buttonList.add(GuiButton(QuickJoinLogic.ID_CONFIG, x, 8, 80, 20, "§b§lConfig"))
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        Gui.drawRect(0, 0, width, height, OVERLAY)
        drawCenteredString(fontRendererObj, grid.title, grid.centerX, grid.titleY, 0xFFFFFF)

        val favorites = HyLobbyConfig.quickJoinEnableFavorites
        for (tile in grid.tiles) {
            drawTile(tile, tile.contains(mouseX, mouseY), favorites && FavoritesManager.isFavorite(guiName, tile.id))
        }
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    private fun drawTile(tile: QuickJoinTiles.Tile, hovered: Boolean, favorite: Boolean) {
        val x = tile.x
        val y = tile.y
        val right = x + tile.width
        val bottom = y + QuickJoinTiles.TILE_HEIGHT

        // Rounded corners: three rows that do not overlap.
        val background = if (hovered) TILE_HOVER else TILE
        Gui.drawRect(x + 1, y, right - 1, y + 1, background)
        Gui.drawRect(x, y + 1, right, bottom - 1, background)
        Gui.drawRect(x + 1, bottom - 1, right - 1, bottom, background)

        if (tile.hasIcon) {
            mc.getTextureManager().bindTexture(ResourceLocation("hylobby", "quickjoin/tiles${tile.sheet}.png"))
            GlStateManager.enableBlend()
            GlStateManager.color(1f, 1f, 1f, 1f)
            Gui.drawModalRectWithCustomSizedTexture(
                x, y, tile.u.toFloat(), tile.v.toFloat(), QuickJoinTiles.ICON_SIZE, QuickJoinTiles.ICON_SIZE,
                QuickJoinTiles.ICON_SHEET_SIZE.toFloat(), QuickJoinTiles.ICON_SHEET_SIZE.toFloat()
            )
        } else {
            Gui.drawRect(x, y, x + 22, y + 22, PLACEHOLDER_BORDER)
            Gui.drawRect(x + 1, y + 1, x + 21, y + 21, PLACEHOLDER_FILL)
            drawCenteredString(fontRendererObj, tile.initial(), x + 11, y + 7, QuickJoinTiles.colorOf(tile.colorCode) and 0xFFFFFF)
        }

        val textX = (x + 28).toFloat()
        val maxWidth = tile.width - 32
        val name = fontRendererObj.trimStringToWidth(QuickJoinTiles.nameWithStyle(tile, favorite), maxWidth)
        fontRendererObj.drawStringWithShadow(name, textX, (y + 4).toFloat(), 0xFFFFFF)
        val subtitle = fontRendererObj.trimStringToWidth(tile.subtitle, maxWidth)
        fontRendererObj.drawStringWithShadow(subtitle, textX, (y + 13).toFloat(), 0xAAAAAA)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        val tile = grid.tileAt(mouseX, mouseY)
        if (tile != null) {
            if (mouseButton == 0) {
                QuickJoinActions.execute(QuickJoinLogic.func(guiName, tile.id))
                return
            }
            // Right click toggles the favorite state of a playable entry.
            if (mouseButton == 1 && HyLobbyConfig.quickJoinEnableFavorites && QuickJoinLogic.canFavorite(guiName, tile.id)) {
                val added = FavoritesManager.toggleFavorite(guiName, tile.id)
                Debug.log("QuickJoin favorite ${if (added) "added" else "removed"}: $guiName#${tile.id}")
                return
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            QuickJoinLogic.ID_FAVORITES -> QuickJoinCompat.setScreen(Minecraft.getMinecraft(), FavoritesScreen())
            QuickJoinLogic.ID_SEARCH -> QuickJoinCompat.setScreen(Minecraft.getMinecraft(), SearchScreen())
            QuickJoinLogic.ID_CONFIG -> HyLobbyCommand.openConfig()
        }
    }
}
*///?} else {
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import tomeko.hylobby.commands.HyLobbyCommand
import tomeko.hylobby.config.HyLobbyConfig
import tomeko.hylobby.quickjoin.QuickJoinCompat
import tomeko.hylobby.quickjoin.QuickJoinLogic
import tomeko.hylobby.quickjoin.QuickJoinTiles
import tomeko.hylobby.quickjoin.favorites.FavoritesManager
import tomeko.hylobby.utils.LegacyText
import tomeko.hylobby.utils.Debug

private val TILE = 0x90181818.toInt()
private val TILE_HOVER = 0xB0505050.toInt()
private val PLACEHOLDER_BORDER = 0xFF5A5A5A.toInt()
private val PLACEHOLDER_FILL = 0xFF262626.toInt()
private val WHITE = 0xFFFFFFFF.toInt()
private val GRAY = 0xFFAAAAAA.toInt()

class QuickJoinScreen(private val guiName: String) : Screen(Component.literal("QuickJoin")) {
    private var grid = QuickJoinTiles.Grid()

    override fun init() {
        super.init()
        clearWidgets()
        grid = QuickJoinTiles.build(guiName, width, height)

        if (guiName == QuickJoinLogic.MAIN_GUI) {
            var x = width - 90
            if (HyLobbyConfig.quickJoinEnableFavorites) {
                addRenderableWidget(
                    Button.builder(Component.literal("§6§lFavorites")) {
                        QuickJoinCompat.setScreen(Minecraft.getInstance(), FavoritesScreen())
                    }.bounds(x, 8, 80, 20).build()
                )
                x -= 90
            }
            addRenderableWidget(
                Button.builder(Component.literal("§a§lSearch")) {
                    QuickJoinCompat.setScreen(Minecraft.getInstance(), SearchScreen())
                }.bounds(x, 8, 80, 20).build()
            )
            x -= 90
            addRenderableWidget(
                Button.builder(Component.literal("§b§lConfig")) {
                    HyLobbyCommand.openConfig()
                }.bounds(x, 8, 80, 20).build()
            )
        }
    }

    override fun mouseClicked(click: MouseButtonEvent, doubled: Boolean): Boolean {
        val tile = grid.tileAt(click.x().toInt(), click.y().toInt())
        if (tile != null) {
            if (click.button() == 0) {
                QuickJoinActions.execute(QuickJoinLogic.func(guiName, tile.id))
                return true
            }
            // Right click toggles the favorite state of a playable entry.
            if (click.button() == 1 && HyLobbyConfig.quickJoinEnableFavorites && QuickJoinLogic.canFavorite(guiName, tile.id)) {
                val added = FavoritesManager.toggleFavorite(guiName, tile.id)
                Debug.log("QuickJoin favorite ${if (added) "added" else "removed"}: $guiName#${tile.id}")
                return true
            }
        }
        return super.mouseClicked(click, doubled)
    }

    override fun extractRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        super.extractRenderState(context, mouseX, mouseY, delta)

        context.centeredText(font, Component.literal(grid.title), grid.centerX, grid.titleY, WHITE)

        val favorites = HyLobbyConfig.quickJoinEnableFavorites
        for (tile in grid.tiles) {
            drawTile(context, tile, tile.contains(mouseX, mouseY), favorites && FavoritesManager.isFavorite(guiName, tile.id))
        }
    }

    private fun drawTile(context: GuiGraphicsExtractor, tile: QuickJoinTiles.Tile, hovered: Boolean, favorite: Boolean) {
        val x = tile.x
        val y = tile.y
        val right = x + tile.width
        val bottom = y + QuickJoinTiles.TILE_HEIGHT

        // Rounded corners: three rows that do not overlap.
        val background = if (hovered) TILE_HOVER else TILE
        context.fill(x + 1, y, right - 1, y + 1, background)
        context.fill(x, y + 1, right, bottom - 1, background)
        context.fill(x + 1, bottom - 1, right - 1, bottom, background)

        if (tile.hasIcon) {
            val icon = Identifier.fromNamespaceAndPath("hylobby", "quickjoin/tiles${tile.sheet}.png")
            context.blit(
                RenderPipelines.GUI_TEXTURED, icon, x, y, tile.u.toFloat(), tile.v.toFloat(),
                QuickJoinTiles.ICON_SIZE, QuickJoinTiles.ICON_SIZE,
                QuickJoinTiles.ICON_SHEET_SIZE, QuickJoinTiles.ICON_SHEET_SIZE
            )
        } else {
            context.fill(x, y, x + 22, y + 22, PLACEHOLDER_BORDER)
            context.fill(x + 1, y + 1, x + 21, y + 21, PLACEHOLDER_FILL)
            context.centeredText(font, Component.literal(tile.initial()), x + 11, y + 7, QuickJoinTiles.colorOf(tile.colorCode))
        }

        val textX = x + 28
        val maxWidth = tile.width - 32
        drawLeft(context, fit(QuickJoinTiles.nameWithStyle(tile, favorite), maxWidth), textX, y + 4, WHITE)
        drawLeft(context, fit("§7" + tile.subtitle, maxWidth), textX, y + 13, GRAY)
    }

    private fun drawLeft(context: GuiGraphicsExtractor, text: Component, x: Int, y: Int, color: Int) {
        context.centeredText(font, text, x + font.width(text) / 2, y, color)
    }

    // Shortens the text until it fits, the formatting codes at the start are kept.
    private fun fit(legacy: String, maxWidth: Int): Component {
        var parsed: Component = LegacyText.parse(legacy)
        if (font.width(parsed) <= maxWidth) return parsed
        var text = legacy
        while (text.length > 1) {
            text = text.substring(0, text.length - 1)
            if (text.endsWith("§")) continue
            parsed = LegacyText.parse("$text...")
            if (font.width(parsed) <= maxWidth) break
        }
        return parsed
    }
}
//?}
