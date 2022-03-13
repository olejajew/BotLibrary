package com.doblack.bot_library.constructor

import com.doblack.bot_library.base.ChatBot
import com.doblack.bot_library.constructor.models.InstructionsModel

interface BotConstructor {

    fun getConstructorModule(): ConstructorModule

    fun updateInstructions(instructionsModel: InstructionsModel)

    fun getBotId(): String

    fun getChatBot(): ChatBot

}