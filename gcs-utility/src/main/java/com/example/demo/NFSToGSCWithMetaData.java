package com.example.demo;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.ImmutableMap;
public class NFSToGSCWithMetaData {
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
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(serviceAccountKeyFilePath));
            Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

            String bucketName = properties.getProperty("bucket_name");
            String fileName = properties.getProperty("file_name");
            String filePath = properties.getProperty("file_path");

            String contentType = properties.getProperty("content_type");

            String document = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);

            // metadata keys and values from properties
            ImmutableMap.Builder<String, String> metadataBuilder = ImmutableMap.builder();
            for (String key : properties.stringPropertyNames()) {
                if (key.startsWith("metadata.")) {
                    String metadataKey = key.substring("metadata.".length());
                    String metadataValue = properties.getProperty(key);
                    metadataBuilder.put(metadataKey, metadataValue);
                }
            }
            ImmutableMap<String, String> metadata = metadataBuilder.build();

            BlobInfo documentMetadata = BlobInfo.newBuilder(bucketName, fileName)
                    .setContentType(contentType)
                    .setMetadata(metadata)
                    .build();

            Blob blob = storage.create(documentMetadata, document.getBytes(StandardCharsets.UTF_8));
            logger.info("File " + fileName + " uploaded to " + bucketName + " with metadata.");
        } catch (IOException e) {
            logger.error("Error uploading file to GCS: " + e.getMessage());
        }
    }
}

	
	
	
	
	
	
	



	
	
	
	
	


