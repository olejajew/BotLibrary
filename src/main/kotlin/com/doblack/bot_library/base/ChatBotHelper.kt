package com.doblack.bot_library.base

import com.doblack.bot_library.base.models.BotButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

object ChatBotHelper {

    fun prepareInlineButtons(botButtons: List<List<BotButton>>): ReplyKeyboard {
        val keyboardMarkup = InlineKeyboardMarkup()
        val buttons = ArrayList<ArrayList<InlineKeyboardButton>>()
        botButtons.forEach {
            val row = arrayListOf<InlineKeyboardButton>()
            it.forEach {
                val inlineKeyboardButton = InlineKeyboardButton()
                inlineKeyboardButton.text = it.title
                inlineKeyboardButton.callbackData = it.id
                if (it.url != null) {
                    inlineKeyboardButton.url = it.url
                }
                if (it.data != null) {
                    inlineKeyboardButton.callbackData = it.data
                }
                row.add(inlineKeyboardButton)
            }
            buttons.add(row)
        }
        keyboardMarkup.keyboard = buttons.toList()
        return keyboardMarkup
    }

    fun prepareReplyButtons(botButtons: List<List<BotButton>>, keyboardBotButtons: ArrayList<BotButton>): ReplyKeyboard {
        keyboardBotButtons.clear()

        val keyboardMarkup = ReplyKeyboardMarkup()
        val keyboardRows = ArrayList<KeyboardRow>()
        botButtons.forEach {
            val row = KeyboardRow()
            it.forEach {
                val button = KeyboardButton()
                button.text
                row.add(it.title)
                keyboardBotButtons.add(it)
            }
            keyboardRows.add(row)
        }
        keyboardMarkup.keyboard = keyboardRows

        return keyboardMarkup
    }

}