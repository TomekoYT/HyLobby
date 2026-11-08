package tomeko.hylobby.quickjoin.screen

import net.minecraft.client.Minecraft
import tomeko.hylobby.quickjoin.QuickJoinCompat
import tomeko.hylobby.quickjoin.data.Func
import tomeko.hylobby.utils.Debug

// Runs the action behind a QuickJoin button.
object QuickJoinActions {
    fun execute(func: Func?) {
        //? if 1.8.9
        //val mc = Minecraft.getMinecraft()
        //? else
        val mc = Minecraft.getInstance()
        if (func == null || !QuickJoinCompat.hasPlayer(mc)) return

        val value = func.string ?: ""
        when (func.type) {
            "opengui" -> {
                Debug.log("QuickJoin: opening $value")
                QuickJoinCompat.setScreen(mc, QuickJoinScreen(value))
            }
            "command" -> {
                Debug.log("QuickJoin: sending $value")
                QuickJoinCompat.sendCommand(mc, value)
                QuickJoinCompat.closeScreen(mc)
            }
            "chat" -> {
                QuickJoinCompat.addChat(mc, value)
                QuickJoinCompat.closeScreen(mc)
            }
            "close" -> QuickJoinCompat.closeScreen(mc)
            "repeat" -> {
                Debug.log("QuickJoin: sending $value ${func.times} times")
                QuickJoinCompat.closeScreen(mc)
                repeat(maxOf(func.times, 1)) {
                    QuickJoinCompat.sendCommand(mc, value)
                }
            }
            else -> Debug.log("QuickJoin: unknown function type ${func.type}")
        }
    }
}
