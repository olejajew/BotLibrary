package com.doblack.bot_library.core

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.*
import java.io.*
import java.util.*
import kotlin.Exception

class AwsProvider(
    private val url: String,
    accessKey: String,
    secretKey: String,
    region: String,
    private val rootBucketName: String
) {

    private val awsCreds: BasicAWSCredentials? = try {
        BasicAWSCredentials(accessKey, secretKey)
    } catch (e: Exception) {
        System.err.println("Aws not initialized")
        e.printStackTrace()
        null
    }
    private val s3Client: AmazonS3? = try {
        AmazonS3ClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(awsCreds))
            .withRegion(region)
            .build()
    } catch (e: Exception) {
        System.err.println("Aws not initialized")
        e.printStackTrace()
        null
    }

    fun saveTextFile(content: String, fileName: String, bucket: String, botId: String): Boolean {
        if (s3Client == null) return false
        val bytes = content.toByteArray()
        val inputStream = ByteArrayInputStream(bytes)
        val metadata = ObjectMetadata()
        metadata.contentType = "text/plain"
        metadata.contentLength = bytes.size.toLong()
        val request = PutObjectRequest(
            "$rootBucketName/$botId/$bucket",
            fileName,
            inputStream,
            metadata
        )
        request.withCannedAcl(CannedAccessControlList.PublicRead)
        s3Client.putObject(request)
        return true
    }

    fun getTextFileContent(botId: String, bucket: String, fileName: String): String? {
        if (s3Client == null) return null
        return try {
            val request = GetObjectRequest("$rootBucketName/$botId/$bucket", fileName)
            val result = s3Client.getObject(request)
            val inputStream = InputStreamReader(result.objectContent)
            val bufferReader = BufferedReader(inputStream)
            bufferReader.readText()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveImage(imageInBase64: String, fileName: String, bucket: String, botId: String): Boolean {
        if (s3Client == null) return false
        val bytes = imageInBase64.base64ToByteArray() ?: return false
        val inputStream = ByteArrayInputStream(bytes)
        val metadata = ObjectMetadata()
        metadata.contentType = "image/png"
        metadata.contentLength = bytes.size.toLong()
        val request = PutObjectRequest(
            "$rootBucketName/$botId/$bucket",
            fileName,
            inputStream,
            metadata
        )
        request.withCannedAcl(CannedAccessControlList.PublicRead)
        s3Client.putObject(request)
        return true
    }

    fun deleteFiles(images: Array<String>, bucket: String, botId: String) {
        if (s3Client == null) return

        val toDelete = images.map {
            "$botId/$bucket/$it"
        }.toTypedArray()
        val request = DeleteObjectsRequest(rootBucketName)
            .withKeys(*toDelete)
            .withQuiet(false)
        try {
            s3Client.deleteObjects(request)
        } catch (e: Exception) {
        }
    }

    fun getImageLink(botId: String, bucket: String, imageId: String): String {
        return "$url/$botId/$bucket/$imageId.png"
    }

    fun getFilesList(botId: String, bucket: String): List<String> {
        if (s3Client == null) return emptyList()

        val request = ListObjectsV2Request()
            .withBucketName(rootBucketName)
            .withPrefix("$botId/$bucket")
        return s3Client.listObjectsV2(request).objectSummaries.filter { it.key.contains("png") }
            .map { it.key.split("/")[1] }
    }

    fun getImageInputStream(imageName: String, bucket: String, botId: String): InputStream {
        val request = GetObjectRequest("$rootBucketName/$botId/$bucket", "$imageName.png")
        val result = s3Client!!.getObject(request)
        return result.objectContent
    }

    private fun String.base64ToByteArray(): ByteArray? {
        return Base64.getDecoder().decode(
            this.substring(this.indexOf(",") + 1).toByteArray()
        )
    }

}