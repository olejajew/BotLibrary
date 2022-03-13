package com.doblack.bot_library.constructor.enums

enum class UserActions(val id: Int) {
    onCommand(1001),
    onMessage(1002),
    ;
    companion object{
        fun findById(id: Int): UserActions? {
            return values().firstOrNull { it.id == id }
        }
    }
}