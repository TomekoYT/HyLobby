package tomeko.hylobby.quickjoin

//? if 1.8.9 {
/*import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import org.lwjgl.input.Keyboard

// Vanilla key bindings, so they can be rebound in the Controls menu.
internal object QuickJoinKeys {
    private lateinit var openKey: KeyBinding
    private lateinit var favoritesKey: KeyBinding
    private lateinit var searchKey: KeyBinding
    private var registered = false

    fun register() {
    }

    // The bindings are appended to the game settings on the first tick, after the client finished loading,
    // and the options are read again so keys rebound in a previous session are restored.
    fun init(mc: Minecraft) {
        if (registered || mc.gameSettings == null) return
        registered = true

        openKey = KeyBinding("QuickJoin: Open Menu", Keyboard.KEY_NONE, "HyLobby")
        favoritesKey = KeyBinding("QuickJoin: Open Favorites", Keyboard.KEY_NONE, "HyLobby")
        searchKey = KeyBinding("QuickJoin: Open Search", Keyboard.KEY_NONE, "HyLobby")

        val old = mc.gameSettings.keyBindings
        mc.gameSettings.keyBindings = arrayOf(*old, openKey, favoritesKey, searchKey)
        mc.gameSettings.loadOptions()
    }

    fun tick(mc: Minecraft) {
        if (!registered) return
        while (openKey.isPressed) QuickJoin.onOpenKey(mc)
        while (favoritesKey.isPressed) QuickJoin.onFavoritesKey(mc)
        while (searchKey.isPressed) QuickJoin.onSearchKey(mc)
    }
}
*///?} else {
import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier

internal object QuickJoinKeys {
    private lateinit var openKey: KeyMapping
    private lateinit var favoritesKey: KeyMapping
    private lateinit var searchKey: KeyMapping

    fun register() {
        val category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("hylobby", "general"))
        openKey = KeyMappingHelper.registerKeyMapping(KeyMapping("key.hylobby.quickjoin.open", KEY_TYPE, InputConstants.UNKNOWN.value, category))
        favoritesKey = KeyMappingHelper.registerKeyMapping(KeyMapping("key.hylobby.quickjoin.favorites", KEY_TYPE, InputConstants.UNKNOWN.value, category))
        searchKey = KeyMappingHelper.registerKeyMapping(KeyMapping("key.hylobby.quickjoin.search", KEY_TYPE, InputConstants.UNKNOWN.value, category))
    }

    fun init(mc: Minecraft) {
    }

    fun tick(mc: Minecraft) {
        while (openKey.consumeClick()) QuickJoin.onOpenKey(mc)
        while (favoritesKey.consumeClick()) QuickJoin.onFavoritesKey(mc)
        while (searchKey.consumeClick()) QuickJoin.onSearchKey(mc)
    }

    private val KEY_TYPE: InputConstants.Type =
        //? if >=26.3 {
        /*InputConstants.Type.KEYBOARD
    *///?} else {
    InputConstants.Type.KEYSYM
    //?}
}
//?}
