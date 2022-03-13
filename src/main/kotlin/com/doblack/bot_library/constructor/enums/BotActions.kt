package com.doblack.bot_library.constructor.enums

enum class BotActions(val id: Int) {
    doAction(2000),
    sendMessage(2001),
    sendInlineButtons(2002),
    sendKeyboardButtons(2003),
    sendImage(2004),
    sendImageGroup(2005),
    ;

    companion object{
        fun findById(id: Int): BotActions? {
            return values().firstOrNull { it.id == id }
        }
    }
}