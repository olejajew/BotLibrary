package com.doblack.bot_library.constructor

import com.botlibrary.core.AwsProvider
import com.doblack.bot_library.constructor.models.InstructionsModel
import com.google.gson.Gson
import java.io.InputStream

class FilesProvider(private val botId: String) {

    private val gson = Gson()
    private var instructions: String? = null

    companion object {
        private const val INSTRUCTIONS_FILE_NAME = "instructions.txt"
        private const val BUCKET_NAME = "constructor"
    }

    init {
        getInstructionsString()
        println(instructions)
    }

    fun updateInstructions(instructions: InstructionsModel) {
        AwsProvider.saveTextFile(gson.toJson(instructions), INSTRUCTIONS_FILE_NAME, BUCKET_NAME, botId)
        this.instructions = gson.toJson(instructions)
        deleteUselessImages(instructions)
    }

    fun getInstructionsString(): String {
        if (instructions == null) {
            instructions = AwsProvider.getTextFileContent(botId, BUCKET_NAME, INSTRUCTIONS_FILE_NAME)
        }
        return instructions ?: "{}"
    }

    fun getInstructionsModel(): InstructionsModel? {
        return try {
            Gson().fromJson(instructions, InstructionsModel::class.java)
        } catch (e: Exception) {
            return null
        }
    }

    fun saveImage(imageId: String, imageBase64: String) {
        AwsProvider.saveImage(imageBase64, "$imageId.png", BUCKET_NAME, botId)
    }

    private fun getImagesList(): List<String> {
        return AwsProvider.getFilesList(botId, BUCKET_NAME)
    }

    fun getImage(imageName: String): InputStream {
        return AwsProvider.getImageInputStream(imageName, BUCKET_NAME, botId)
    }

    private fun deleteUselessImages(instructions: InstructionsModel) {
        val imagesFromInstructions = getImagesListFromInstructions(instructions)
        val getCurrentImages = getImagesList()
        val toDelete = arrayListOf<String>()
        getCurrentImages.forEach {
            if (!imagesFromInstructions.contains(it)) {
                toDelete.add(it)
            }
        }

        println(imagesFromInstructions)
        println(getCurrentImages)
        println(toDelete)
        AwsProvider.deleteFiles(toDelete.toTypedArray(), BUCKET_NAME, botId)
    }

    private fun getImagesListFromInstructions(instructions: InstructionsModel): ArrayList<String> {
        val imagesList = arrayListOf<String>()
        if (instructions.botScripts.isNullOrEmpty()) {
            return ArrayList()
        }
        instructions.botScripts.forEach {
            it.actions.forEach {
                val images = it.data.images
                if (images != null) {
                    imagesList.addAll(images)
                }
            }
        }
        return imagesList
    }

    fun getImageUrl(imageId: String): String {
        return AwsProvider.getImageLink(botId, BUCKET_NAME, imageId)
    }

}