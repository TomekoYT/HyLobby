package tomeko.hylobby.limbo

//? if 1.8.9 {
/*import net.minecraft.client.Minecraft
import tomeko.hylobby.event.ClientTickEvents
*///?} else {
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
//?}
import tomeko.hylobby.config.HyLobbyConfig
import tomeko.hylobby.location.HypixelPackets

object LimboLimiter {
    private var time = 0L

    val inLimbo: Boolean
        get() = HypixelPackets.onHypixel && HypixelPackets.currentServerName == "limbo"

    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register { time = if (inLimbo) time + 1 else 0 }
    }

    @JvmStatic
    fun shouldLimitFramerate(): Boolean = inLimbo && HyLobbyConfig.limboLimiter && time * 20 >= 5 &&
            //? if 1.8.9
            //Minecraft.getMinecraft().gameSettings.limitFramerate > 15
            //? else
            Minecraft.getInstance().options.framerateLimit().get() > 15
}
