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

private const val MAX_VISIBLE = 7

private const val ID_BACK = 8700
private const val ID_CLOSE = 8701
private const val ID_UP = 8702
private const val ID_DOWN = 8703
private const val ID_CLEAR = 8704
private const val ID_ENTRY = 8800

class SearchScreen : GuiScreen() {
    private var entries: List<QuickJoinLogic.SearchEntry> = emptyList()
    private var filtered: List<QuickJoinLogic.SearchEntry> = emptyList()
    private var searchField: GuiTextField? = null
    private var scrollOffset = 0

    override fun initGui() {
        buttonList.clear()
        Keyboard.enableRepeatEvents(true)

        if (entries.isEmpty()) entries = QuickJoinLogic.collectSearchEntries()

        val availableWidth = 260
        val startX = width / 2 - availableWidth / 2
        val topY = height / 2 - 90

        val field = searchField ?: GuiTextField(8898, fontRendererObj, startX, topY - 22, availableWidth, 18).also {
            it.setMaxStringLength(64)
            it.setFocused(true)
            searchField = it
        }
        field.xPosition = startX
        field.yPosition = topY - 22

        filtered = QuickJoinLogic.filterSearch(entries, field.getText())
        scrollOffset = maxOf(0, minOf(scrollOffset, filtered.size - MAX_VISIBLE))

        val visible = minOf(MAX_VISIBLE, maxOf(0, filtered.size - scrollOffset))
        var y = topY
        for (i in 0 until visible) {
            buttonList.add(GuiButton(ID_ENTRY + i, startX, y, availableWidth, 20, filtered[scrollOffset + i].formattedLabel()))
            y += 22
        }

        buttonList.add(GuiButton(ID_BACK, startX, height / 2 + 90, 128, 20, "Back"))
        buttonList.add(GuiButton(ID_CLOSE, startX + 132, height / 2 + 90, 128, 20, "Close"))
        buttonList.add(GuiButton(ID_UP, startX, height / 2 + 66, 62, 20, "▲"))
        buttonList.add(GuiButton(ID_DOWN, startX + 66, height / 2 + 66, 62, 20, "▼"))
        buttonList.add(GuiButton(ID_CLEAR, startX + 132, height / 2 + 66, 128, 20, "Clear Search"))
    }

    override fun onGuiClosed() {
        Keyboard.enableRepeatEvents(false)
    }

    override fun updateScreen() {
        searchField?.updateCursorCounter()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        Gui.drawRect(0, 0, width, height, 0xB0000000.toInt())
        drawCenteredString(fontRendererObj, "§aSearch", width / 2, height / 2 - 110, 0xFFFFFF)
        searchField?.drawTextBox()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
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
        val max = maxOf(0, filtered.size - MAX_VISIBLE)
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
                if (index < 0 || index >= filtered.size) return
                val entry = filtered[index]
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
import net.minecraft.network.chat.Component
import tomeko.hylobby.quickjoin.QuickJoinCompat
import tomeko.hylobby.quickjoin.QuickJoinLogic
import tomeko.hylobby.utils.LegacyText

private const val MAX_VISIBLE = 7

class SearchScreen : Screen(Component.literal("Search")) {
    private var entries: List<QuickJoinLogic.SearchEntry> = emptyList()
    private var filtered: List<QuickJoinLogic.SearchEntry> = emptyList()

    private var searchField: EditBox? = null
    private var scrollOffset = 0
    private val entryButtons = ArrayList<Button>()

    override fun init() {
        super.init()
        clearWidgets()
        entryButtons.clear()

        if (entries.isEmpty()) entries = QuickJoinLogic.collectSearchEntries()

        val availableWidth = 260
        val startX = width / 2 - availableWidth / 2
        val topY = height / 2 - 90

        val field = searchField ?: EditBox(font, startX, topY - 22, availableWidth, 18, Component.empty()).also {
            it.setCanLoseFocus(true)
            it.setResponder {
                scrollOffset = 0
                rebuildEntries()
            }
            searchField = it
        }
        field.setX(startX)
        field.setY(topY - 22)
        field.setFocused(true)
        addRenderableWidget(field)

        rebuildEntries()

        addRenderableWidget(
            Button.builder(Component.literal("Back")) {
                QuickJoinCompat.setScreen(Minecraft.getInstance(), QuickJoinScreen(QuickJoinLogic.MAIN_GUI))
            }.bounds(startX, height / 2 + 90, 128, 20).build()
        )
        addRenderableWidget(
            Button.builder(Component.literal("Close")) {
                QuickJoinCompat.closeScreen(Minecraft.getInstance())
            }.bounds(startX + 132, height / 2 + 90, 128, 20).build()
        )
        addRenderableWidget(
            Button.builder(Component.literal("▲")) { scroll(-1) }.bounds(startX, height / 2 + 66, 62, 20).build()
        )
        addRenderableWidget(
            Button.builder(Component.literal("▼")) { scroll(1) }.bounds(startX + 66, height / 2 + 66, 62, 20).build()
        )
        addRenderableWidget(
            Button.builder(Component.literal("Clear Search")) {
                field.setValue("")
                scrollOffset = 0
                rebuildEntries()
            }.bounds(startX + 132, height / 2 + 66, 128, 20).build()
        )
    }

    private fun rebuildEntries() {
        filtered = QuickJoinLogic.filterSearch(entries, searchField?.getValue() ?: "")
        scrollOffset = maxOf(0, minOf(scrollOffset, filtered.size - MAX_VISIBLE))

        for (widget in entryButtons) removeWidget(widget)
        entryButtons.clear()

        val availableWidth = 260
        val startX = width / 2 - availableWidth / 2
        var y = height / 2 - 90

        val visible = minOf(MAX_VISIBLE, maxOf(0, filtered.size - scrollOffset))
        for (i in 0 until visible) {
            val entry = filtered[scrollOffset + i]
            val widget = Button.builder(LegacyText.parse(entry.formattedLabel())) {
                QuickJoinActions.execute(QuickJoinLogic.func(entry.guiName, entry.buttonId))
            }.bounds(startX, y, availableWidth, 20).build()
            entryButtons.add(widget)
            addRenderableWidget(widget)
            y += 22
        }
    }

    private fun scroll(direction: Int) {
        val max = maxOf(0, filtered.size - MAX_VISIBLE)
        val next = maxOf(0, minOf(scrollOffset + direction, max))
        if (next != scrollOffset) {
            scrollOffset = next
            rebuildEntries()
        }
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
        context.centeredText(font, Component.literal("§aSearch"), width / 2, height / 2 - 110, 0xFFFFFF)
        super.extractRenderState(context, mouseX, mouseY, delta)
    }
}
//?}
