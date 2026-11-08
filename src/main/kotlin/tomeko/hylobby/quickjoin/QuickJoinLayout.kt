package tomeko.hylobby.quickjoin

import com.google.gson.Gson
import tomeko.hylobby.quickjoin.data.LayoutConfig
import tomeko.hylobby.utils.Debug
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread

object QuickJoinLayout {
    private const val REMOTE_URL = "https://raw.githubusercontent.com/QWERTZexe/Quickjoin/main/common/src/main/resources/assets/quickjoin/guis.json"
    private const val FALLBACK_PATH = "/assets/hylobby/quickjoin/guis.json"

    private val gson = Gson()

    @Volatile
    private var layout: LayoutConfig? = null

    // Loads the bundled layout right away and then refreshes it from the Quickjoin repository in the background.
    fun load() {
        layout = readFallback()
        thread(isDaemon = true, name = "HyLobby-QuickJoin-Layout") {
            val remote = readRemote()
            if (remote != null) {
                layout = remote
                Debug.log("QuickJoin layout updated from remote")
            }
        }
    }

    fun get(): LayoutConfig {
        layout?.let { return it }
        val fallback = readFallback()
        layout = fallback
        return fallback
    }

    private fun readRemote(): LayoutConfig? {
        return try {
            val connection = URL(REMOTE_URL).openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            InputStreamReader(connection.inputStream, StandardCharsets.UTF_8).use { reader ->
                validate(gson.fromJson(reader, LayoutConfig::class.java))
            }
        } catch (e: Exception) {
            Debug.log("QuickJoin remote layout unavailable: ${e.message}")
            null
        }
    }

    private fun readFallback(): LayoutConfig {
        try {
            QuickJoinLayout::class.java.getResourceAsStream(FALLBACK_PATH)?.use { stream ->
                InputStreamReader(stream, StandardCharsets.UTF_8).use { reader ->
                    val parsed = validate(gson.fromJson(reader, LayoutConfig::class.java))
                    if (parsed != null) return parsed
                }
            }
        } catch (e: Exception) {
            Debug.log("QuickJoin fallback layout failed: ${e.message}")
        }
        return LayoutConfig()
    }

    private fun validate(parsed: LayoutConfig?): LayoutConfig? {
        if (parsed == null || !parsed.guis.containsKey(QuickJoinLogic.MAIN_GUI)) return null
        return parsed
    }
}
