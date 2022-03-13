package com.botlibrary.core

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.CollectionReference
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import java.io.InputStream

object FirestoreProvider {

    private fun getFirebaseFile(): InputStream? {
        return javaClass.getResource("/FirebaseAppKey.json").openStream()
    }

    init {
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(getFirebaseFile()))
            .build()

        FirebaseApp.initializeApp(options)
    }

    fun getDatabaseInstance() = FirestoreClient.getFirestore()

    fun getCollection(collectionName: String) = getDatabaseInstance().collection(collectionName)

    fun getSubCollection(
        baseCollectionName: String,
        documentName: String,
        subCollectionName: String
    ): CollectionReference {
        return getDatabaseInstance()
            .collection(baseCollectionName)
            .document(documentName)
            .collection(subCollectionName)
    }

}