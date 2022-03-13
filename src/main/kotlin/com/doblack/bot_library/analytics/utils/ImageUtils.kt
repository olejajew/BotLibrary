package com.doblack.bot_library.analytics.utils

import java.io.File
import java.util.*

object ImageUtils {

    fun encode(file: File): String {
        val bytes = file.readBytes()
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun decoder(base64Str: String, imageFile: File){
        val imageByteArray = Base64.getDecoder().decode(base64Str)
        imageFile.writeBytes(imageByteArray)
    }

}