package tomeko.hylobby.quickjoin.screen

//? if 1.8.9 {
/*import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import tomeko.hylobby.quickjoin.QuickJoinCompat
import tomeko.hylobby.quickjoin.QuickJoinLogic
import tomeko.hylobby.quickjoin.favorites.FavoriteEntry
import tomeko.hylobby.quickjoin.favorites.FavoritesManager

private const val MAX_VISIBLE = 7

private const val ID_BACK = 8000
private const val ID_CLOSE = 8001
private const val ID_UP = 8002
private const val ID_DOWN = 8003
private const val ID_CLEAR = 8004
private const val ID_ENTRY = 9000

class FavoritesScreen : GuiScreen() {
    private var favorites: List<FavoriteEntry> = FavoritesManager.getFavorites()
    private var searchField: GuiTextField? = null
    private var scrollOffset = 0

    override fun initGui() {
        buttonList.clear()
        Keyboard.enableRepeatEvents(true)

        val availableWidth = 220
        val startX = width / 2 - availableWidth / 2
        val topY = height / 2 - 90

        val field = searchField ?: GuiTextField(9998, fontRendererObj, startX, topY - 22, availableWidth, 18).also {
            it.setMaxStringLength(64)
            it.setFocused(false)
            searchField = it
        }
        field.xPosition = startX
        field.yPosition = topY - 22

        favorites = QuickJoinLogic.filterFavorites(field.getText())
        scrollOffset = maxOf(0, minOf(scrollOffset, favorites.size - MAX_VISIBLE))

        val visible = minOf(MAX_VISIBLE, maxOf(0, favorites.size - scrollOffset))
        var y = topY
        for (i in 0 until visible) {
            buttonList.add(GuiButton(ID_ENTRY + i, startX, y, availableWidth, 20, QuickJoinLogic.favoriteLabel(favorites[scrollOffset + i])))
            y += 22
        }

        buttonList.add(GuiButton(ID_BACK, startX, height / 2 + 90, 108, 20, "Back"))
        buttonList.add(GuiButton(ID_CLOSE, startX + 112, height / 2 + 90, 108, 20, "Close"))
        buttonList.add(GuiButton(ID_UP, startX, height / 2 + 66, 52, 20, "▲"))
        buttonList.add(GuiButton(ID_DOWN, startX + 56, height / 2 + 66, 52, 20, "▼"))
        buttonList.add(GuiButton(ID_CLEAR, startX + 112, height / 2 + 66, 108, 20, "Clear Search"))
    }

    override fun onGuiClosed() {
        Keyboard.enableRepeatEvents(false)
    }

    override fun updateScreen() {
        searchField?.updateCursorCounter()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        Gui.drawRect(0, 0, width, height, 0xB0000000.toInt())
        drawCenteredString(fontRendererObj, "§eFavorites", width / 2, height / 2 - 110, 0xFFFFFF)
        searchField?.drawTextBox()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        // Right click removes an entry from the favorites.
        if (mouseButton == 1) {
            for (button in buttonList) {
                val index = button.id - ID_ENTRY + scrollOffset
                if (button.id >= ID_ENTRY && index >= 0 && index < favorites.size && button.mousePressed(mc, mouseX, mouseY)) {
                    val entry = favorites[index]
                    FavoritesManager.toggleFavorite(entry.guiName, entry.buttonId)
                    initGui()
                    return
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton)
        searchField?.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun handleMouseInput() {
        super.handleMouseInput()
        val wheel = Mouse.getEventDWheel()
        if (wheel != 0) scroll(if (wheel < 0) 1 else -1)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        val field = searchField
        if (field != null && field.isFocused() && keyCode != Keyboard.KEY_ESCAPE) {
            field.textboxKeyTyped(typedChar, keyCode)
            scrollOffset = 0
            initGui()
        } else {
            super.keyTyped(typedChar, keyCode)
        }
    }

    private fun scroll(direction: Int) {
        val max = maxOf(0, favorites.size - MAX_VISIBLE)
        scrollOffset = maxOf(0, minOf(scrollOffset + direction, max))
        initGui()
    }

    override fun actionPerformed(button: GuiButton) {
        val mc = Minecraft.getMinecraft()
        when (button.id) {
            ID_BACK -> QuickJoinCompat.setScreen(mc, QuickJoinScreen(QuickJoinLogic.MAIN_GUI))
            ID_CLOSE -> QuickJoinCompat.closeScreen(mc)
            ID_UP -> scroll(-1)
            ID_DOWN -> scroll(1)
            ID_CLEAR -> {
                searchField?.setText("")
                scrollOffset = 0
                initGui()
            }
            else -> {
                val index = button.id - ID_ENTRY + scrollOffset
                if (index < 0 || index >= favorites.size) return
                val entry = favorites[index]
                QuickJoinActions.execute(QuickJoinLogic.func(entry.guiName, entry.buttonId))
            }
        }
    }
}
*///?} else {
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import tomeko.hylobby.quickjoin.QuickJoinCompat
import tomeko.hylobby.quickjoin.QuickJoinLogic
import tomeko.hylobby.quickjoin.favorites.FavoriteEntry
import tomeko.hylobby.quickjoin.favorites.FavoritesManager
import tomeko.hylobby.utils.LegacyText

private const val MAX_VISIBLE = 7

class FavoritesScreen : Screen(Component.literal("Favorites")) {
    private var searchField: EditBox? = null
    private var scrollOffset = 0
    private var favorites: List<FavoriteEntry> = emptyList()

    private val entryButtons = ArrayList<Button>()
    private val entryMap = HashMap<Button, FavoriteEntry>()

    override fun init() {
        super.init()
        clearWidgets()
        entryButtons.clear()
        entryMap.clear()

        val availableWidth = 220
        val startX = width / 2 - availableWidth / 2
        val topY = height / 2 - 90

        val field = searchField ?: EditBox(font, startX, topY - 22, availableWidth, 18, Component.empty()).also {
            it.setMaxLength(64)
            it.setResponder {
                scrollOffset = 0
                rebuildEntries()
            }
            searchField = it
        }
        field.setX(startX)
        field.setY(topY - 22)
        addRenderableWidget(field)

        rebuildEntries()

        addRenderableWidget(
            Button.builder(Component.literal("Back")) {
                QuickJoinCompat.setScreen(Minecraft.getInstance(), QuickJoinScreen(QuickJoinLogic.MAIN_GUI))
            }.bounds(startX, height / 2 + 90, 108, 20).build()
        )
        addRenderableWidget(
            Button.builder(Component.literal("Close")) {
                QuickJoinCompat.closeScreen(Minecraft.getInstance())
            }.bounds(startX + 112, height / 2 + 90, 108, 20).build()
        )
        addRenderableWidget(
            Button.builder(Component.literal("▲")) { scroll(-1) }.bounds(startX, height / 2 + 66, 52, 20).build()
        )
        addRenderableWidget(
            Button.builder(Component.literal("▼")) { scroll(1) }.bounds(startX + 56, height / 2 + 66, 52, 20).build()
        )
        addRenderableWidget(
            Button.builder(Component.literal("Clear Search")) {
                field.setValue("")
                scrollOffset = 0
                rebuildEntries()
            }.bounds(startX + 112, height / 2 + 66, 108, 20).build()
        )
    }

    private fun rebuildEntries() {
        for (widget in entryButtons) removeWidget(widget)
        entryButtons.clear()
        entryMap.clear()

        favorites = QuickJoinLogic.filterFavorites(searchField?.getValue() ?: "")
        scrollOffset = maxOf(0, minOf(scrollOffset, favorites.size - MAX_VISIBLE))

        val availableWidth = 220
        val startX = width / 2 - availableWidth / 2
        var y = height / 2 - 90

        val visible = minOf(MAX_VISIBLE, maxOf(0, favorites.size - scrollOffset))
        for (i in 0 until visible) {
            val entry = favorites[scrollOffset + i]
            val widget = Button.builder(LegacyText.parse(QuickJoinLogic.favoriteLabel(entry))) {
                QuickJoinActions.execute(QuickJoinLogic.func(entry.guiName, entry.buttonId))
            }.bounds(startX, y, availableWidth, 20).build()
            entryButtons.add(widget)
            entryMap[widget] = entry
            addRenderableWidget(widget)
            y += 22
        }
    }

    private fun scroll(direction: Int) {
        val max = maxOf(0, favorites.size - MAX_VISIBLE)
        val next = maxOf(0, minOf(scrollOffset + direction, max))
        if (next != scrollOffset) {
            scrollOffset = next
            rebuildEntries()
        }
    }

    override fun mouseClicked(click: MouseButtonEvent, doubled: Boolean): Boolean {
        // Right click removes an entry from the favorites.
        if (click.button() == 1) {
            for (widget in entryButtons) {
                if (widget.isMouseOver(click.x(), click.y())) {
                    val entry = entryMap[widget]
                    if (entry != null) {
                        FavoritesManager.toggleFavorite(entry.guiName, entry.buttonId)
                        rebuildEntries()
                        return true
                    }
                }
            }
        }
        return super.mouseClicked(click, doubled)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        if (verticalAmount != 0.0) {
            scroll(if (verticalAmount > 0) -1 else 1)
            return true
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    override fun extractRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        context.fill(0, 0, width, height, 0xB0000000.toInt())
        context.centeredText(font, Component.literal("§eFavorites"), width / 2, height / 2 - 110, 0xFFFFFF)
        super.extractRenderState(context, mouseX, mouseY, delta)
    }
}
//?}
