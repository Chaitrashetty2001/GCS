package com.example.demo;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
public class MultipleNFSToGCS {
	

	
	    private static final Logger logger = LoggerFactory.getLogger(NFSToGSCWithMetaData.class);

	    public static void main(String[] args) {
	        Properties properties = new Properties();
	        try (FileInputStream input = new FileInputStream("src/config.properties")) {
	            properties.load(input);
	        } catch (IOException e) {
	            logger.error("Error reading configuration file: " + e.getMessage());
	            System.exit(1);
	        }

	        String serviceAccountKeyFilePath = properties.getProperty("service_account_key_file_path");
	        if (serviceAccountKeyFilePath == null || serviceAccountKeyFilePath.isEmpty()) {
	            logger.error("Service account key file path is not specified.");
	            System.exit(1);
	        }

	        try {
	            // Load credentials from the service account key file
	            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(serviceAccountKeyFilePath));
	            // Create storage client with the loaded credentials
	            Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

	            // Bucket name
	            String bucketName = "nfstogcs";

	            // List of file paths to upload
	            List<String> filePaths = List.of("C:/Users/Chaitra Shetty/Desktop/sample1.xml",
	                                              "C:/Users/Chaitra Shetty/Desktop/sample2.xml",
	                                              "C:/Users/Chaitra Shetty/Desktop/sample3.xml");

	            // Iterate through each file path and upload to GCS
	            for (String filePath : filePaths) {
	                String fileName = Paths.get(filePath).getFileName().toString();
	                String fileContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);

	                // Define blob info with metadata
	                BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
	                        .setContentType("application/xml")
	                        .setMetadata(ImmutableMap.of(
	                                "key1", "abc",
	                                "key2", "456"
	                        ))
	                        .build();

	                // Upload file to GCS
	                Blob blob = storage.create(blobInfo, fileContent.getBytes(StandardCharsets.UTF_8));
	                logger.info("File " + fileName + " uploaded to " + bucketName + " with metadata.");
	            }
	        } catch (IOException e) {
	            logger.error("Error uploading files to GCS: " + e.getMessage());
	        }
	    }
	}

	
	
	


