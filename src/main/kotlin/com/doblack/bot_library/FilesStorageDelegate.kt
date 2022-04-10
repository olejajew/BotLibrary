package com.doblack.bot_library

import java.io.*

interface FilesStorageDelegate {

    fun getTextFileContent(fileName: String): String?

    fun getImageLink(imageId: String): String

    fun getImageInputStream(imageName: String): InputStream

}