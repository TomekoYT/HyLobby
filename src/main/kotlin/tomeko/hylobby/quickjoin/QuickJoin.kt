package tomeko.hylobby.quickjoin

//? if 1.8.9 {
/*import tomeko.hylobby.event.ClientSendMessageEvents
import tomeko.hylobby.event.ClientTickEvents
*///?} else {
import com.mojang.brigadier.Command
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.ClientCommands
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents
//?}
import net.minecraft.client.Minecraft
import tomeko.hylobby.config.HyLobbyConfig
import tomeko.hylobby.location.HypixelPackets
import tomeko.hylobby.quickjoin.favorites.FavoritesManager
import tomeko.hylobby.quickjoin.screen.FavoritesScreen
import tomeko.hylobby.quickjoin.screen.QuickJoinScreen
import tomeko.hylobby.quickjoin.screen.SearchScreen
import java.nio.file.Path

object QuickJoin {
    private const val PENDING_TIMEOUT_TICKS = 40

    private var pending: (() -> Unit)? = null
    private var pendingTicks = 0

    fun register() {
        QuickJoinLayout.load()
        FavoritesManager.initialize(configDir())
        QuickJoinKeys.register()
        registerCommand()

        ClientTickEvents.END_CLIENT_TICK.register { onTick(it) }
        ClientSendMessageEvents.ALLOW_COMMAND.register { allowCommand(it) }
    }

    // Brigadier command with tab completion. On 1.8.9 the command is handled in allowCommand instead.
    private fun registerCommand() {
        //? if !1.8.9 {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                ClientCommands.literal("quickjoin").executes { _ ->
                    openMain(mc())
                    Command.SINGLE_SUCCESS
                }
            )
        }
        //?}
    }

    private fun onTick(mc: Minecraft) {
        QuickJoinKeys.init(mc)
        if (!QuickJoinCompat.hasPlayer(mc)) {
            pending = null
            return
        }

        pending?.let { open ->
            if (!QuickJoinCompat.hasScreen(mc)) {
                pending = null
                open()
            } else if (++pendingTicks > PENDING_TIMEOUT_TICKS) {
                pending = null
            }
        }

        QuickJoinKeys.tick(mc)
    }

    // Runs for every command the player sends, before it reaches the server.
    private fun allowCommand(command: String): Boolean {
        val parts = command.trim().split(Regex("\\s+"))
        if (parts.size != 1) return true
        val name = parts[0].lowercase()

        //? if 1.8.9 {
        /*if (name == "quickjoin") {
            openMain(mc())
            return false
        }
        *///?}

        val alias = alias()
        if (alias.isNotEmpty() && alias != "quickjoin" && name == alias &&
            HyLobbyConfig.quickJoinEnabled && HyLobbyConfig.quickJoinEnableAlias && onHypixel()
        ) {
            openMain(mc())
            return false
        }
        return true
    }

    private fun alias(): String {
        var alias = HyLobbyConfig.quickJoinCommandAlias.trim()
        if (alias.startsWith("/")) alias = alias.substring(1)
        return alias.replace(Regex("\\s+"), "").lowercase()
    }

    fun onOpenKey(mc: Minecraft) {
        if (HyLobbyConfig.quickJoinEnableKeybind) openMain(mc)
    }

    fun onFavoritesKey(mc: Minecraft) {
        openFavorites(mc)
    }

    fun onSearchKey(mc: Minecraft) {
        openSearch(mc)
    }

    fun openMain(mc: Minecraft) {
        if (!isAvailable(mc)) return
        schedule { QuickJoinCompat.setScreen(mc, QuickJoinScreen(QuickJoinLogic.MAIN_GUI)) }
    }

    fun openFavorites(mc: Minecraft) {
        if (!HyLobbyConfig.quickJoinEnableFavorites) return
        if (!isAvailable(mc)) return
        schedule { QuickJoinCompat.setScreen(mc, FavoritesScreen()) }
    }

    fun openSearch(mc: Minecraft) {
        if (!isAvailable(mc)) return
        schedule { QuickJoinCompat.setScreen(mc, SearchScreen()) }
    }

    private fun isAvailable(mc: Minecraft): Boolean {
        if (!HyLobbyConfig.quickJoinEnabled) {
            QuickJoinCompat.addChat(mc, QuickJoinLogic.PREFIX + "QuickJoin is disabled in the config.")
            return false
        }
        if (!onHypixel()) {
            QuickJoinCompat.addChat(mc, QuickJoinLogic.PREFIX + "You are not on Hypixel.")
            return false
        }
        return true
    }

    private fun onHypixel(): Boolean = HypixelPackets.onHypixel

    // Screens are opened on the next tick, because the chat screen closes itself after a command was sent.
    private fun schedule(open: () -> Unit) {
        pending = open
        pendingTicks = 0
    }

    private fun mc(): Minecraft {
        //? if 1.8.9
        //return Minecraft.getMinecraft()
        //? else
        return Minecraft.getInstance()
    }

    private fun configDir(): Path {
        //? if forge {
        /*return java.io.File(Minecraft.getMinecraft().mcDataDir, "config").toPath()
        *///?} else {
        return net.fabricmc.loader.api.FabricLoader.getInstance().configDir
        //?}
    }
}
