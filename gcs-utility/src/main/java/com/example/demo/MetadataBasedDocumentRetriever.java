package com.example.demo;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class MetadataBasedDocumentRetriever {

	    private static final Logger logger = LoggerFactory.getLogger(MetadataBasedDocumentRetriever.class);
	 

	    public static void main(String[] args) {
	        // Load configuration from properties file
	        Properties properties = new Properties();
	        try (FileInputStream input = new FileInputStream("src/p4.properties")) {
	            properties.load(input);
	        } catch (IOException e) {
	            logger.error("Error reading configuration file: {}", e.getMessage());
	            return;
	        }

	        // Get service account key file path and project ID
	        String serviceAccountKeyFilePath = properties.getProperty("service_account_key_file_path");
	        String projectId = properties.getProperty("project_id");

	        // Initialize Storage client
	        Storage storage = initializeStorage(serviceAccountKeyFilePath, projectId);

	        // Get metadata from user input
	        Map<String, String> metadata = getMetadataFromUser();

	        // Retrieve GCS document based on metadata
	        String blobName = getBlobNameFromMetadata(storage, properties.getProperty("bucket_name"), metadata);
	        if (blobName != null) {
	            // Download and display the document
	            downloadAndDisplayDocument(storage, properties.getProperty("bucket_name"), blobName);
	        } else {
	            logger.info("No document found for the given metadata.");
	        }
	    }

	    private static Storage initializeStorage(String serviceAccountKeyFilePath, String projectId) {
	        try {
	            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(serviceAccountKeyFilePath));
	            return StorageOptions.newBuilder().setCredentials(credentials).setProjectId(projectId).build().getService();
	        } catch (IOException e) {
	            logger.error("Error initializing Storage: {}", e.getMessage());
	            return null;
	        }
	    }

	    private static Map<String, String> getMetadataFromUser() {
	        Scanner scanner = new Scanner(System.in);
	       
	        logger.info("Enter key 1:");
	        String key1 = scanner.nextLine();
	        logger.info("Enter value 1:");
	        String value1 = scanner.nextLine();
	       
	        scanner.close();

	        // Create and return metadata map
	        return Map.of(key1, value1);
	    }
	    private static String getBlobNameFromMetadata(Storage storage, String bucketName, Map<String, String> metadata) {
	        Page<Blob> blobs = storage.list(bucketName);
	        for (Blob blob : blobs.iterateAll()) {
	            Map<String, String> blobMetadata = blob.getMetadata();
	            if (blobMetadata != null) { // Check if blobMetadata is not null
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


	    private static void downloadAndDisplayDocument(Storage storage, String bucketName, String blobName) {
	        Blob blob = storage.get(bucketName, blobName);
	        if (blob != null) {
	            // Print the document content
	            logger.info("Document Content:");
	            logger.info(new String(blob.getContent()));
	        } else {
	            logger.error("Error: Unable to retrieve document from GCS.");
	        }
	    }
	}
	


