package com.example.demo.service;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Service
public class DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    private final Properties properties;
    private final Storage storage;

    public DocumentService() throws IOException {
        // Load configuration from properties file
        properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/p4.properties")) {
            properties.load(input);
        }

        // Initialize Storage client
        storage = initializeStorage(properties.getProperty("service_account_key_file_path"),
                                    properties.getProperty("project_id"));
    }

    private Storage initializeStorage(String serviceAccountKeyFilePath, String projectId) {
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(serviceAccountKeyFilePath));
            return StorageOptions.newBuilder().setCredentials(credentials).setProjectId(projectId).build().getService();
        } catch (IOException e) {
            logger.error("Error initializing Storage: {}", e.getMessage());
            return null;
        }
    }

    public String retrieveDocument(Map<String, String> metadata) {
        String blobName = getBlobNameFromMetadata(storage, properties.getProperty("bucket_name"), metadata);
        if (blobName != null) {
            return downloadDocument(storage, properties.getProperty("bucket_name"), blobName);
        } else {
            logger.info("No document found for the given metadata.");
            return "No document found for the given metadata.";
        }
    }

    private String getBlobNameFromMetadata(Storage storage, String bucketName, Map<String, String> metadata) {
        Page<Blob> blobs = storage.list(bucketName);
        for (Blob blob : blobs.iterateAll()) {
            Map<String, String> blobMetadata = blob.getMetadata();
            if (blobMetadata != null) {
                boolean match = true;
                for (Map.Entry<String, String> entry : metadata.entrySet()) {
                    if (!blobMetadata.containsKey(entry.getKey()) || !blobMetadata.get(entry.getKey()).equals(entry.getValue())) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return blob.getName();
                }
            }
        }
        return null;
    }

    private String downloadDocument(Storage storage, String bucketName, String blobName) {
        Blob blob = storage.get(bucketName, blobName);
        if (blob != null) {
            return new String(blob.getContent());
        } else {
            logger.error("Error: Unable to retrieve document from GCS.");
            return "Error: Unable to retrieve document from GCS.";
        }
    }
}
