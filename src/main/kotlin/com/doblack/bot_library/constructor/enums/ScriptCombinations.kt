package com.doblack.bot_library.constructor.enums

enum class ScriptCombinations(val id: Int) {
    text(3001),
    text_inlineButton(3002),
    text_inlineButton_image(3003),
    text_keyboardButton(3004),
    text_keyboardButton_image(3005),
    text_image(3006),
    image(3007),
    image_inlineButton(3008),
    image_keyboardButton(3009),
    imageGroup(3010),
    doScript(3000),
    ;
    companion object{
        fun getById(id: Int): ScriptCombinations? {
            return values().firstOrNull { it.id == id }
        }
    }
}