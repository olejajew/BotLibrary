package com.doblack.bot_library.constructor

import com.doblack.bot_library.constructor.models.InstructionsModel
import com.doblack.bot_library.constructor.parser.InstructionsParser
import com.doblack.bot_library.FilesStorageDelegate

class ConstructorModule(
    constructorModule: BotConstructor,
    private val filesStorageDelegate: FilesStorageDelegate
) {

    //todo Валидация + подбивка айдишников
    //todo Не допускать рекурсию
    //todo Предписанные методы
    //todo Предписанные переменные

    val chatBot = constructorModule.getChatBot()
    private var instructionsParser = InstructionsParser(constructorModule.getInstructions())
    private val scriptExecutor = ScriptExecutor(this)

    private fun validate() {
        //todo проверять, что все userAction содержат существующие id
    }

    fun onCommand(command: String, chatId: Long) {
        val userAction = instructionsParser.getCommandAction(command) ?: return
        val botScript = instructionsParser.getBotScript(userAction.data.botScriptId) ?: return
        scriptExecutor.execute(botScript, chatId)
    }

    fun onMessage(text: String, chatId: Long) {
        val botScriptId = instructionsParser.getMessageAction(text)?.data?.botScriptId
            ?: instructionsParser.getButtonActionText(text)?.botScriptId ?: return
        val botScript = instructionsParser.getBotScript(botScriptId) ?: return
        scriptExecutor.execute(botScript, chatId)
    }

    fun callbackReceived(data: String, inlineMessageId: String?, chatId: Long) {
        val id = data
        val botScript = instructionsParser.getBotScript(id) ?: return
        scriptExecutor.execute(botScript, chatId)
    }

    fun getScript(scriptId: String) = instructionsParser.getBotScript(scriptId)
    fun getButton(buttonId: String, type: Int) = instructionsParser.getButton(buttonId, type)

    fun updateInstructions(instructions: InstructionsModel) {
        instructionsParser.setInstructions(instructions)
    }

    fun getFilesProvider() = filesStorageDelegate


}