package tomeko.hylobby.quickjoin

//? if 1.8.9 {
/*import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ChatComponentText

// Thin wrapper over the parts of the client API that differ between Minecraft versions.
object QuickJoinCompat {
    fun hasPlayer(mc: Minecraft?): Boolean = mc != null && mc.thePlayer != null

    fun hasScreen(mc: Minecraft): Boolean = mc.currentScreen != null

    fun setScreen(mc: Minecraft, screen: GuiScreen?) {
        mc.displayGuiScreen(screen)
    }

    fun closeScreen(mc: Minecraft) {
        mc.displayGuiScreen(null)
    }

    fun addChat(mc: Minecraft, message: String) {
        mc.thePlayer?.addChatMessage(ChatComponentText(message))
    }

    fun sendCommand(mc: Minecraft, command: String) {
        mc.thePlayer?.sendChatMessage(if (command.startsWith("/")) command else "/$command")
    }
}
*///?} else {
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

// Thin wrapper over the parts of the client API that differ between Minecraft versions.
object QuickJoinCompat {
    fun hasPlayer(mc: Minecraft?): Boolean = mc != null && mc.player != null

    fun hasScreen(mc: Minecraft): Boolean {
        //? if >= 26.2
        return mc.gui.screen() != null
        //? else
        //return mc.screen != null
    }

    fun setScreen(mc: Minecraft, screen: Screen?) {
        //? if >= 26.2
        mc.gui.setScreen(screen)
        //? else
        //mc.setScreen(screen)
    }

    fun closeScreen(mc: Minecraft) {
        setScreen(mc, null)
    }

    fun addChat(mc: Minecraft, message: String) {
        //? if >= 26.2 {
        mc.gui?.hud?.getChat()?.addClientSystemMessage(Component.literal(message))
        //?} else {
        /*mc.gui?.getChat()?.addClientSystemMessage(Component.literal(message))
        *///?}
    }

    fun sendCommand(mc: Minecraft, command: String) {
        mc.player?.connection?.sendCommand(if (command.startsWith("/")) command.substring(1) else command)
    }
}
//?}
