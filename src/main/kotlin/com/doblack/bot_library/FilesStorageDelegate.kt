package com.doblack.bot_library

import java.io.*

interface FilesStorageDelegate {

    fun saveTextFile(content: String, fileName: String,  botId: String)

    fun getTextFileContent(fileName: String): String?

    fun saveImage(imageInBase64: String, fileName: String, ): Boolean

    fun deleteFiles(images: Array<String>, )

    fun getImageLink( imageId: String): String

    fun getFilesList(): List<String>

    fun getImageInputStream(imageName: String): InputStream

}