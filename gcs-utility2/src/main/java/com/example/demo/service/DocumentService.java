package com.example.demo.service;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

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
	/**
	 * Retrieves a document(single document) from Google Cloud Storage (GCS) based on provided metadata.
	 * 
	 */
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    private Storage storage;
    private String bucketName;

    @PostConstruct
    public void init() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/p5.properties")) {
            properties.load(input);
            String serviceAccountKeyFilePath = properties.getProperty("service_account_key_file_path");
            String projectId = properties.getProperty("project_id");
            this.bucketName = properties.getProperty("bucket_name");
            this.storage = initializeStorage(serviceAccountKeyFilePath, projectId);
        } catch (IOException e) {
            logger.error("Error reading configuration file: {}", e.getMessage());
        }
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

    public String getBlobNameFromMetadata(Map<String, String> metadata) {
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

    public String downloadDocumentContent(String blobName) {
        Blob blob = storage.get(bucketName, blobName);
        if (blob != null) {
            return new String(blob.getContent());
        } else {
            logger.error("Error: Unable to retrieve document from GCS.");
            return null;
        }
    }
}




