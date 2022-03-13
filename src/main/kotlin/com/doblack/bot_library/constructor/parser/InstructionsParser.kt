package com.doblack.bot_library.constructor.parser

import com.doblack.bot_library.constructor.enums.UserActions
import com.doblack.bot_library.constructor.models.BotScriptModel
import com.doblack.bot_library.constructor.models.ButtonModel
import com.doblack.bot_library.constructor.models.InstructionsModel
import com.doblack.bot_library.constructor.models.UserActionBlock

class InstructionsParser(private var instructions: InstructionsModel?) {

    fun getCommandAction(command: String): UserActionBlock? {
        return instructions?.userActions?.firstOrNull {
            it.actionTag == UserActions.onCommand.id
                    && it.data.trigger == command
        }
    }

    fun getBotScript(botActionId: String): BotScriptModel? {
        return instructions?.botScripts?.firstOrNull { it.scriptId == botActionId }
    }

    fun getMessageAction(text: String): UserActionBlock? {
        return instructions?.userActions?.firstOrNull {
            it.actionTag == UserActions.onMessage.id && it.data.trigger.equals(
                text,
                true
            )
        }
    }


    fun getButton(buttonId: String, type: Int) = instructions?.buttons?.firstOrNull { it.type == type && it.id == buttonId }

    fun getButtonActionText(text: String): ButtonModel? {
        return instructions?.buttons?.firstOrNull {
            val buttonText = it.text
            buttonText == text
        }
    }

    fun setInstructions(instructionsModel: InstructionsModel) {
        this.instructions = instructionsModel
    }

}