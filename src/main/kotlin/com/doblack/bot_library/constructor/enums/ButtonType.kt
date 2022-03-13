package com.doblack.bot_library.constructor.enums

enum class ButtonType(val id: Int){
    inlineButton(0),
    keyboardButton(1),
    ;

    companion object{
        fun getById(id: Int): ButtonType {
            return values().firstOrNull { it.id == id } ?: inlineButton
        }
    }
}