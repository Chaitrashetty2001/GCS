package com.example.demo;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class UploadObject1 {
	public static void main(String[] args) throws IOException {
        String projectId = "lyrical-bolt-421906";
        String bucketName = "initialbucketsample";
        String filePath = "C:/Users/Chaitra Shetty/Documents/CHAITRA_P_SHETTY_RESUME.pdf";
        
//        String filePath = "C:/Users/Chaitra Shetty/Documents/MOVIES/Rajath & Niveditha Highlights HD.mp4";
        String objectName = "myfile4.txt";
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("C:/Users/Chaitra Shetty/Downloads/lyrical-bolt-421906-cc62be7f803d.json"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId(projectId).build().getService();
	    BlobId blobId = BlobId.of(bucketName, objectName);
	    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

	    // Optional: set a generation-match precondition to avoid potential race
	    // conditions and data corruptions. The request returns a 412 error if the
	    // preconditions are not met.
	    Storage.BlobWriteOption precondition;
	    if (storage.get(bucketName, objectName) == null) {
	      // For a target object that does not yet exist, set the DoesNotExist precondition.
	      // This will cause the request to fail if the object is created before the request runs.
	      precondition = Storage.BlobWriteOption.doesNotExist();
	    } else {
	      // If the destination already exists in your bucket, instead set a generation-match
	      // precondition. This will cause the request to fail if the existing object's generation
	      // changes before the request runs.
	      precondition =
	          Storage.BlobWriteOption.generationMatch(
	              storage.get(bucketName, objectName).getGeneration());
	    }
	    storage.createFrom(blobInfo, Paths.get(filePath), precondition);

	    System.out.println(
	        "File " + filePath + " uploaded to bucket " + bucketName + " as " + objectName);
	  }
	}


