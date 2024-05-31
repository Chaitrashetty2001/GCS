package com.example.demo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

	
		public class NFSToGCS {
		
		    private static final Logger logger = LoggerFactory.getLogger(NFSToGCS.class);

		  
		    private static final String CONFIG_FILE_PATH = "src/p1.properties";

		    public static void main(String[] args) {
		       
		        Properties properties = new Properties();
		        try (FileInputStream input = new FileInputStream(CONFIG_FILE_PATH)) {
		            properties.load(input);
		        } catch (IOException e) {
		            logger.error("Error reading configuration file: " + e.getMessage());
		            System.exit(1);
		        }

		        // Database connection from properties file
		        String dbUrl = properties.getProperty("db_url");
		        String dbUser = properties.getProperty("db_user");
		        String dbPassword = properties.getProperty("db_password");

		        // Get GCS parameters from properties file
		        String bucketName = properties.getProperty("bucket_name");
		        String objectName = properties.getProperty("object_name");

		        // Get service account key file path from properties file
		        String serviceAccountKeyFilePath = properties.getProperty("service_account_key_file_path");

		        try {
		            // Load GCS credentials
		            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(serviceAccountKeyFilePath));
		            Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

		            // 1: Write file into MySQL Workbench as a BLOB
		            insertFileIntoDatabase(dbUrl, dbUser, dbPassword, properties.getProperty("file_path"));

		            // 2: Retrieve the BLOB from MySQL
		            byte[] blobData = retrieveBlobFromDatabase(dbUrl, dbUser, dbPassword);

		            // 3: Move the BLOB to Google Cloud Storage
		            BlobId blobId = BlobId.of(bucketName, objectName);
		            BlobInfo documentMetadata = BlobInfo.newBuilder(blobId).build();
		            storage.create(documentMetadata, blobData);

		            logger.info("File successfully uploaded to GCS.");
		        } catch (Exception e) {
		            logger.error("Error: " + e.getMessage());
		        }
		    }

		    // S1: Write file into MySQL Workbench as a BLOB
		    private static void insertFileIntoDatabase(String url, String user, String password, String filePath) throws SQLException, IOException {
		        try (Connection conn = DriverManager.getConnection(url, user, password);
		                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO files (file_data) VALUES (?)")) {
		            File file = new File(filePath);
		            FileInputStream fis = new FileInputStream(file);
		            pstmt.setBinaryStream(1, fis, (int) file.length());
		            pstmt.executeUpdate();
		        }
		    }

		    // S2: Retrieve the BLOB from MySQL
		    private static byte[] retrieveBlobFromDatabase(String url, String user, String password) throws SQLException {
		        try (Connection conn = DriverManager.getConnection(url, user, password);
		                Statement stmt = conn.createStatement();
		                ResultSet rs = stmt.executeQuery("SELECT file_data FROM files")) {
		            if (rs.next()) {
		                Blob blob = rs.getBlob("file_data");
		                return blob.getBytes(1, (int) blob.length());
		            }
		            return null;
		        }
		    }
		}

		
		
		
		
		
		
		
		
		
		
		
	
