package com.example.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NFSToMYSQL {
	
	
	    private static final Logger logger = LoggerFactory.getLogger(NFSToMYSQL.class);

	    public static void main(String[] args) {
	        Properties properties = new Properties();
	        try (FileInputStream input = new FileInputStream("src/p2.properties")) {
	            properties.load(input);
	        } catch (Exception e) {
	            logger.error("Error reading configuration file: " + e.getMessage());
	            return;
	        }

	        String url = properties.getProperty("database_url");
	        String username = properties.getProperty("database_username");
	        String password = properties.getProperty("database_password");
	        String filePath = properties.getProperty("file_path");
	        String tableName = properties.getProperty("table_name");
	        String blobColumnName = properties.getProperty("blob_column_name");
	        String fileNameColumnName = properties.getProperty("file_name_column_name");
	        String fileTypeColumnName = properties.getProperty("file_type_column_name");

	        try {
	            Connection connection = DriverManager.getConnection(url, username, password);

	            String sql = "INSERT INTO " + tableName + " (" + blobColumnName + ", " + fileNameColumnName + ", " + fileTypeColumnName + ") "
	                    + "VALUES (?, ?, ?)";
	            PreparedStatement statement = connection.prepareStatement(sql);

	            File file = new File(filePath);
	            FileInputStream inputStream = new FileInputStream(file);

	            String fileName = file.getName();
	            String fileType = URLConnection.guessContentTypeFromName(filePath);

	            statement.setBinaryStream(1, inputStream);
	            statement.setString(2, fileName);
	            statement.setString(3, fileType);

	            statement.executeUpdate();

	            logger.info("File saved/updated successfully!");

	            ResultSet resultSet = statement.executeQuery("SELECT " + blobColumnName + " FROM " + tableName);

	            if (resultSet.next()) {
	                InputStream retrievedInputStream = resultSet.getBinaryStream(blobColumnName);
	                File retrievedFile = new File(properties.getProperty("retrieved_file_path"));
	                FileOutputStream outputStream = new FileOutputStream(retrievedFile);

	                byte[] buffer = new byte[4096];
	                int bytesRead;

	                while ((bytesRead = retrievedInputStream.read(buffer)) != -1) {
	                    outputStream.write(buffer, 0, bytesRead);
	                }

	                logger.info("File retrieved successfully!");

	                retrievedInputStream.close();
	                outputStream.close();
	            } else {
	                logger.info("No file found in the database.");
	            }

	            inputStream.close();
	            statement.close();
	            connection.close();
	        } catch (Exception e) {
	            logger.error("Error accessing database: " + e.getMessage());
	        }
	    }
	}

	
	
	
	
	
	
	
	
	
	


