package com.doblack.bot_library.analytics.files

import com.botlibrary.core.AwsProvider
import com.doblack.bot_library.analytics.AnalyticsModule
import java.io.InputStream
import java.util.*

open class FilesProvider(private val analyticsModule: AnalyticsModule) {

    companion object {
        private const val BUCKET_NAME = "messaging"
    }

    fun saveImageFromBase64(base64: String): String {
        val imageName = UUID.randomUUID().toString()
        AwsProvider.saveImage(base64, addExtension(imageName), BUCKET_NAME, analyticsModule.getBotId())
        return imageName
    }

    fun getImageLink(imageName: String): String {
        return AwsProvider.getImageLink(analyticsModule.getBotId(), BUCKET_NAME, imageName)
    }

    private fun addExtension(fileName: String): String {
        return "$fileName.png"
    }

    fun getImage(imageName: String): InputStream {
        return AwsProvider.getImageInputStream(imageName, BUCKET_NAME, analyticsModule.getBotId())
    }

    fun deleteImages(images: List<String>) {
        AwsProvider.deleteFiles(images.toTypedArray(), BUCKET_NAME, analyticsModule.getBotId())
    }


}