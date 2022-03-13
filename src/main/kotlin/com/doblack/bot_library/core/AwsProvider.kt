package com.botlibrary.core

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.*
import java.io.*
import java.lang.Exception
import java.util.*

object AwsProvider {

    private val awsCreds = BasicAWSCredentials("AKIART4NNV7IFG3JUIJQ", "7a952VNFz7Jifjdbkl3Qme84o6ueG9SKPLwWchJy")
    private val s3Client: AmazonS3 = AmazonS3ClientBuilder.standard()
        .withCredentials(AWSStaticCredentialsProvider(awsCreds))
        .withRegion("us-west-1")
        .build()
    private const val BUCKET_NAME = "bot-admin-project"

    fun saveTextFile(content: String, fileName: String, bucket: String, botId: String): Boolean {
        val bytes = content.toByteArray()
        val inputStream = ByteArrayInputStream(bytes)
        val metadata = ObjectMetadata()
        metadata.contentType = "text/plain"
        metadata.contentLength = bytes.size.toLong()
        val request = PutObjectRequest(
            "$BUCKET_NAME/$botId/$bucket",
            fileName,
            inputStream,
            metadata
        )
        request.withCannedAcl(CannedAccessControlList.PublicRead)
        s3Client.putObject(request)
        return true
    }

    fun getTextFileContent(botId: String, bucket: String, fileName: String): String? {
        return try {
            val request = GetObjectRequest("$BUCKET_NAME/$botId/$bucket", fileName)
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
        val bytes = imageInBase64.base64ToByteArray() ?: return false
        val inputStream = ByteArrayInputStream(bytes)
        val metadata = ObjectMetadata()
        metadata.contentType = "image/png"
        metadata.contentLength = bytes.size.toLong()
        val request = PutObjectRequest(
            "$BUCKET_NAME/$botId/$bucket",
            fileName,
            inputStream,
            metadata
        )
        request.withCannedAcl(CannedAccessControlList.PublicRead)
        s3Client.putObject(request)
        return true
    }

    fun deleteFiles(images: Array<String>, bucket: String, botId: String) {
        val toDelete = images.map {
            "$botId/$bucket/$it"
        }.toTypedArray()
        val request = DeleteObjectsRequest(BUCKET_NAME)
            .withKeys(*toDelete)
            .withQuiet(false)
        try {
            s3Client.deleteObjects(request)
        } catch (e: Exception){}
    }

    fun getImageLink(botId: String, bucket: String, imageId: String): String {
        return "https://bot-admin-project.s3.us-west-1.amazonaws.com/$botId/$bucket/$imageId.png"
    }

    fun getFilesList(botId: String, bucket: String): List<String> {
        val request = ListObjectsV2Request()
            .withBucketName(BUCKET_NAME)
            .withPrefix("$botId/$bucket")
        return s3Client.listObjectsV2(request).objectSummaries.filter { it.key.contains("png") }
            .map { it.key.split("/")[1] }
    }

    fun getImageInputStream(imageName: String, bucket: String, botId: String): InputStream {
        val request = GetObjectRequest("$BUCKET_NAME/$botId/$bucket", "$imageName.png")
        val result = s3Client.getObject(request)
        return result.objectContent
    }

    private fun String.base64ToByteArray(): ByteArray? {
        return Base64.getDecoder().decode(
            this.substring(this.indexOf(",") + 1).toByteArray()
        )
    }

}