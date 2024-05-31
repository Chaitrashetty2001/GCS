package com.example.demo;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auth.oauth2.GoogleCredentials;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class MYSQLToGCS {
	    private static final Logger logger = LoggerFactory.getLogger(MYSQLToGCS.class);
	    private static final String CONFIG_FILE_PATH = "src/p3.properties";

	    public static void main(String[] args) throws Exception {
	        Properties properties = new Properties();
	        try (FileInputStream input = new FileInputStream(CONFIG_FILE_PATH)) {
	            properties.load(input);
	        } catch (IOException e) {
	            logger.error("Error reading configuration file: " + e.getMessage());
	            System.exit(1);
	        }

	        String serviceAccountKeyFilePath = properties.getProperty("service_account_key_file_path");
	        if (serviceAccountKeyFilePath == null || serviceAccountKeyFilePath.isEmpty()) {
	            logger.error("Service account key file path is not specified in the configuration file.");
	            System.exit(1);
	        }

	        String projectId = properties.getProperty("project_id");
	        String bucketName = properties.getProperty("bucket_name");

	        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(serviceAccountKeyFilePath));
	        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId(projectId).build().getService();

	        String dbUrl = properties.getProperty("db_url");
	        String username = properties.getProperty("db_username");
	        String password = properties.getProperty("db_password");

	        String tableName = properties.getProperty("table_name");
	        String blobColumnName = properties.getProperty("blob_column_name");
	        String fileNameColumnName = properties.getProperty("file_name_column_name");

	        try (Connection connection = DriverManager.getConnection(dbUrl, username, password)) {
	            String sql = "SELECT " + blobColumnName + ", " + fileNameColumnName + " FROM " + tableName;
	            try (PreparedStatement statement = connection.prepareStatement(sql)) {
	                try (ResultSet resultSet = statement.executeQuery()) {
	                    while (resultSet.next()) {
	                        InputStream inputStream = resultSet.getBinaryStream(blobColumnName);
	                        String fileName = resultSet.getString(fileNameColumnName);
	                        BlobId blobId = BlobId.of(bucketName, fileName);
	                        BlobInfo documentMetadata = BlobInfo.newBuilder(blobId).build();
	                        storage.create(documentMetadata, inputStream.readAllBytes());
	                        inputStream.close();
	                    }
	                    logger.info("Files uploaded to GCS successfully.");
	                }
	            }
	        } catch (Exception e) {
	            logger.error("Error: " + e.getMessage());
	        }
	    }
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
//	private static final Logger logger = LoggerFactory.getLogger(MYSQLToGCS.class);
//	 public static void main(String[] args) throws Exception {
//		 Properties properties = new Properties();
//	        try (FileInputStream input = new FileInputStream("src/config.properties")) {
//	            properties.load(input);
//	        } catch (IOException e) {
//	            logger.error("Error reading configuration file: " + e.getMessage());
//	            System.exit(1);
//	        }
//
//	        String serviceAccountKeyFilePath = properties.getProperty("service_account_key_file_path");
//	        if (serviceAccountKeyFilePath == null || serviceAccountKeyFilePath.isEmpty()) {
//	            logger.error("Service account key file path is not specified in the configuration file.");
//	            System.exit(1);
//	        }
//		 
//		 //String serviceAccountKeyFilePath = System.getenv("SERVICE_ACCOUNT_KEY_FILE_PATH");
//	        // GCS details
//	        String projectId = "lyrical-bolt-421906";
//	        String bucketName = "blobfiletogcs";
//	        // Service account key file
//	        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(serviceAccountKeyFilePath));
//	        
//	        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId(projectId).build().getService();
//
//	        // Database connection details
//	        String url = "jdbc:mysql://localhost:3306/blobtomsql";
//	        String username = "root";
//	        String password = "root";
//
//	        // Table and column names
//	        String tableName = "files";
//	        String blobColumnName = "blob_column";
//	        String fileNameColumnName = "file_name";
//
//	        try (Connection connection = DriverManager.getConnection(url, username, password)) 
//	        {
//	            String sql = "SELECT " + blobColumnName + ", " + fileNameColumnName + " FROM " + tableName;
//	            
//	            try (PreparedStatement statement = connection.prepareStatement(sql)) 
//	            {
//	                try (ResultSet resultSet = statement.executeQuery())
//	                {
//	                    while (resultSet.next()) 
//	                    {
//	                        // Get blob data and file name from result set
//	                        InputStream inputStream = resultSet.getBinaryStream(blobColumnName);
//	                        String fileName = resultSet.getString(fileNameColumnName);
//
//	                        // Upload file to GCS
//	                        BlobId blobId = BlobId.of(bucketName, fileName);
//	                        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
//	                        Blob blob = storage.create(blobInfo, inputStream.readAllBytes());
//
//	                        inputStream.close();
//	                    }
//	                    //System.out.println("File uploaded to GCS successfully.");
//	                    logger.info("File uploaded to GCS successfully.");
//	                }
//	             }
//	         }
//	    }
//	 }
//	            
	        

	
	
	
	


