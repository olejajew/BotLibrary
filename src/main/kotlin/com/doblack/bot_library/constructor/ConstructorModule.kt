package com.doblack.bot_library.constructor

import com.doblack.bot_library.constructor.models.InstructionsModel
import com.doblack.bot_library.constructor.parser.InstructionsParser
import java.io.InputStream
import java.util.*

class ConstructorModule(
    val constructor: BotConstructor,
) {

    //todo Валидация + подбивка айдишников
    //todo Не допускать рекурсию
    //todo Предписанные методы
    //todo Предписанные переменные

    val chatBot = constructor.getChatBot()
    private val filesProvider = FilesProvider(chatBot.botUsername)
    private var instructionsParser = InstructionsParser(filesProvider.getInstructionsModel())
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
        filesProvider.updateInstructions(instructions)
        instructionsParser.setInstructions(instructions)
    }

    fun getInstructions(): InstructionsModel? {
        return filesProvider.getInstructionsModel()
    }

    fun getInstructionsString(): String {
        return filesProvider.getInstructionsString()
    }

    fun saveImage(imageBase64: String): String {
        val imageId = UUID.randomUUID().toString()
        filesProvider.saveImage(imageId, imageBase64)
        return imageId
    }

    fun getFilesProvider() = filesProvider

    fun getImage(imageId: String): InputStream {
        return filesProvider.getImage(imageId)
    }

    fun getImageUrl(imageId: String): String {
        return filesProvider.getImageUrl(imageId)
    }

}