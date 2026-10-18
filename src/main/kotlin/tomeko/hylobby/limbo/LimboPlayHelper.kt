package tomeko.hylobby.limbo

//? if 1.8.9 {
/*import net.minecraft.client.Minecraft
import tomeko.hylobby.event.ClientSendMessageEvents
import tomeko.hylobby.event.ClientTickEvents
*///?} else {
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents
import net.minecraft.client.Minecraft
//?}
import tomeko.hylobby.config.HyLobbyConfig

object LimboPlayHelper {
    private var queued: String? = null
    private var ticks = 0
    private var sending = false

    fun register() {
        ClientSendMessageEvents.ALLOW_COMMAND.register(::onCommand)
        ClientTickEvents.END_CLIENT_TICK.register { onTick() }
    }

    private fun onCommand(command: String): Boolean {
        if (sending || !HyLobbyConfig.limboPlayCommandHelper || !LimboLimiter.inLimbo || !command.startsWith("play ", true)) return true
        send("l")
        queued = command
        ticks = 0
        return false
    }

    private fun onTick() {
        val command = queued ?: return
        if (++ticks < 20) return
        queued = null
        send(command)
    }

    private fun send(command: String) {
        sending = true
        //? if 1.8.9
        //Minecraft.getMinecraft().thePlayer?.sendChatMessage("/$command")
        //? else
        Minecraft.getInstance().player?.connection?.sendCommand(command)
        sending = false
    }
}
