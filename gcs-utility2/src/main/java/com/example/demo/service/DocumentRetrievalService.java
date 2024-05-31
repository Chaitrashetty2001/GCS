package com.example.demo.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.dto.DocumentKey;
import com.example.demo.dto.DocumentWrapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
//
//////////////////////////////////////////////Working fina1 one////////////////////////////////////////////////////
//////recent giving both file

@Service
public class DocumentRetrievalService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentRetrievalService.class);
    private final Storage storage;
    private final String bucketName;

    public DocumentRetrievalService(@Value("${service.account.key.file.path}") String serviceAccountKeyFilePath,
                                    @Value("${project.id}") String projectId,
                                    @Value("${bucket.name}") String bucketName) throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(serviceAccountKeyFilePath));
        storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId(projectId).build().getService();
        this.bucketName = bucketName;
    }

    public List<String> retrieveDocuments(DocumentWrapper documentWrapper) throws IOException {
        List<String> documentContents = new ArrayList<>();
        Map<String, Blob> metadataToBlobMap = new HashMap<>();

        Iterable<Blob> blobs = storage.list(bucketName).iterateAll();
        for (Blob blob : blobs) {
            Map<String, String> blobMetadata = blob.getMetadata();
            if (blobMetadata != null) {
                String combinedKey = generateCombinedKey(blobMetadata);
                metadataToBlobMap.put(combinedKey, blob);
            }
        }

        List<DocumentKey> documentKeysList = documentWrapper.getDocumentKeys();

        for (DocumentKey documentKey : documentKeysList) {
            String combinedKey = generateCombinedKey(documentKey);

            Blob blob = metadataToBlobMap.get(combinedKey);
            if (blob != null) {
                String documentContent = downloadDocument(blob);
                documentContents.add(documentContent);
                logger.info("Retrieved document content: " + documentContent);
            } else {
                logger.error("No document found for keys: key1={}, key2={}, key3={}", 
                              documentKey.getKey1(), documentKey.getKey2(), documentKey.getKey3());
            }
        }

        return documentContents;
    }

    private String generateCombinedKey(Map<String, String> metadata) {
        return metadata.get("key1") + "|" + metadata.get("key2") + "|" + metadata.get("key3");
    }

    private String generateCombinedKey(DocumentKey documentKey) {
        return documentKey.getKey1() + "|" + documentKey.getKey2() + "|" + documentKey.getKey3();
    }

    private String downloadDocument(Blob blob) throws IOException {
        byte[] contentBytes = blob.getContent();
        return new String(contentBytes, StandardCharsets.UTF_8);
    }
}








//@Service
//public class DocumentRetrievalService {
//	private static final Logger logger = LoggerFactory.getLogger(DocumentRetrievalService.class);
//    private final Storage storage;
//    private final String bucketName;
//
//    public DocumentRetrievalService(@Value("${service.account.key.file.path}") String serviceAccountKeyFilePath,
//                                    @Value("${project.id}") String projectId,
//                                    @Value("${bucket.name}") String bucketName) throws IOException {
//        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(serviceAccountKeyFilePath));
//        storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId(projectId).build().getService();
//        this.bucketName = bucketName;
//    }
//
//    public List<String> retrieveDocuments(DocumentWrapper documentWrapper) throws IOException {
//        List<String> documentContents = new ArrayList<>();
//
//        List<DocumentKey> documentKeysList = documentWrapper.getDocumentKeys();
//
//        for (DocumentKey documentKey : documentKeysList) {
//            String key1 = documentKey.getKey1();
//            String key2 = documentKey.getKey2();
//            String key3 = documentKey.getKey3();
//
//            Iterable<Blob> blobs = storage.list(bucketName).iterateAll();
//
//            for (Blob blob : blobs) {
//                Map<String, String> blobMetadata = blob.getMetadata();
//
//                // Check if blob's metadata matches all keys in documentKey
//                
//                if (key1.equals(blobMetadata.get("key1")) &&
//                    key2.equals(blobMetadata.get("key2")) &&
//                    key3.equals(blobMetadata.get("key3"))) {
//
//                    String documentContent = downloadDocument(blob);
//                    documentContents.add(documentContent);
//                    logger.info("Retrieved document content: " + documentContent);
//                }
//            }
//        }////put logs for error and exception
//
//        return documentContents;
//    }
//
//    private String downloadDocument(Blob blob) throws IOException {
//        byte[] contentBytes = blob.getContent();
//		return new String(contentBytes, StandardCharsets.UTF_8);
//    }
//}


