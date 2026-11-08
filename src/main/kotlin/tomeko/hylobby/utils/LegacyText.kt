package tomeko.hylobby.utils

//? if !1.8.9 {
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor

// Turns text with legacy section sign formatting codes into a styled component.
object LegacyText {
    fun parse(input: String?): MutableComponent {
        val result = Component.empty()
        if (input.isNullOrEmpty()) return result

        var style = Style.EMPTY
        val current = StringBuilder()
        var i = 0
        while (i < input.length) {
            val c = input[i]
            if (c == '§' && i + 1 < input.length) {
                if (current.isNotEmpty()) {
                    result.append(Component.literal(current.toString()).setStyle(style))
                    current.setLength(0)
                }
                i++
                val code = input[i].lowercaseChar()
                if (code == 'r') {
                    style = Style.EMPTY
                } else {
                    val formatting = ChatFormatting.getByCode(code)
                    if (formatting != null) {
                        //? if >= 26.2 {
                        val color = TextColor.fromLegacyFormat(formatting)
                        style = if (color != null) Style.EMPTY.withColor(color) else applyFormat(style, formatting)
                        //?} else {
                        /*style = if (formatting.isColor()) {
                            val colorValue = formatting.getColor()
                            if (colorValue != null) Style.EMPTY.withColor(TextColor.fromRgb(colorValue)) else Style.EMPTY
                        } else {
                            applyFormat(style, formatting)
                        }
                        *///?}
                    }
                }
            } else {
                current.append(c)
            }
            i++
        }
        if (current.isNotEmpty()) {
            result.append(Component.literal(current.toString()).setStyle(style))
        }
        return result
    }

    private fun applyFormat(style: Style, formatting: ChatFormatting): Style = when (formatting) {
        ChatFormatting.BOLD -> style.withBold(true)
        ChatFormatting.ITALIC -> style.withItalic(true)
        ChatFormatting.UNDERLINE -> style.withUnderlined(true)
        ChatFormatting.STRIKETHROUGH -> style.withStrikethrough(true)
        ChatFormatting.OBFUSCATED -> style.withObfuscated(true)
        else -> style
    }
}
//?}
