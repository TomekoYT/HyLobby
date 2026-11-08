package tomeko.hylobby.quickjoin.data

// Plain data classes filled by Gson from guis.json, the field names have to match the json keys.

class ButtonConfig {
    var id = 0
    var x: String? = null
    var y: String? = null
    var width = 0
    var height = 0
    var label: String? = null
    var colorCode: String? = null
}

class Func {
    var type: String? = null
    var string: String? = null
    var times = 0
}

class Icon {
    var sheet = 0
    var textureX = 0
    var textureY = 0
}

class GuiData {
    var buttons: MutableList<ButtonConfig>? = null
    var funcs: MutableMap<String, Func>? = null
    var name: String? = null
    var icon: Icon? = null
}

class LayoutConfig {
    var guis: MutableMap<String, GuiData> = HashMap()
}
