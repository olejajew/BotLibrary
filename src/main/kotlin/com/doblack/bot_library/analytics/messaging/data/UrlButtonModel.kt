package com.doblack.bot_library.analytics.messaging.data

class UrlButtonModel {

    lateinit var buttonText: String
    lateinit var buttonUrl: String

    companion object{
        const val URL_BUTTON_SEPARATOR = "%&_"
    }

    constructor(buttonText: String, buttonUrl: String) {
        this.buttonText = buttonText
        this.buttonUrl = buttonUrl
    }

    constructor(string: String) {
        val items = string.split(URL_BUTTON_SEPARATOR)
        println("HERE $string. Item = $items")
        this.buttonText = items[0]
        this.buttonUrl = items[1]
    }

    override fun toString(): String {
        return "$buttonText$URL_BUTTON_SEPARATOR$buttonUrl"
    }

}
