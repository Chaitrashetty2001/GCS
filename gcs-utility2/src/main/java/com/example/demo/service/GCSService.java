package com.example.demo.service;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.dto.FileMetadataDTO;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Service
public class GCSService {

    @Value("${bucket.name}")
    private String bucketName;

    @Value("${service.account.key.file.path}")
    private String credentialsLocation;

    private static final Logger logger = LoggerFactory.getLogger(GCSService.class);

    public List<FileMetadataDTO> getFilesAndMetadata() throws IOException {
        List<FileMetadataDTO> results = new ArrayList<>();

        
        Storage storage = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(credentialsLocation)))
                .build()
                .getService();

	        Page<Blob> blobs = storage.list(bucketName);
        for (Blob blob : blobs.iterateAll()) {

            String metadata = blob.getMetadata() != null ? blob.getMetadata().toString() : "";

            String documentUrl = "https://storage.cloud.google.com/" + bucketName + "/" + blob.getName();
            //static vlaue need to be in one single file

            // Document content
            String documentContent = new String(blob.getContent());

            FileMetadataDTO dto = new FileMetadataDTO(
                    documentUrl,
                    metadata,
                    documentContent
            );

            results.add(dto);

            logger.info("Document URL: {}", documentUrl);
            logger.info("Metadata: {}", metadata);
            logger.info("Document Content: {}", documentContent);
        }

        return results;
    }
}






























//@Service
//public class GcsService {
//
//    private final Storage storage;
//    private final String bucketName;
//
//    public GcsService(
//            @Value("${service.account.key.file.path}") String keyFilePath,
//            @Value("${bucket.name}") String bucketName) throws IOException {
//        // Load credentials from a service account key file
//        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(keyFilePath));
//
//        // Initialize Storage service with credentials and default project ID
//        storage = StorageOptions.newBuilder()
//                .setCredentials(credentials)
//                .build()
//                .getService();
//        
//        this.bucketName = bucketName;
//    }
//
//    public List<GcsFileMetadata> fetchFilesWithMetadata(List<String> documentKeys) {
//        List<GcsFileMetadata> result = new ArrayList<>();
//
//        for (String key : documentKeys) {
//            BlobId blobId = BlobId.of(bucketName, key);
//            Blob blob = storage.get(blobId);
//
//            if (blob != null) {
//                String url = blob.getMediaLink();
//                String metadata = blob.getMetadata() != null ? blob.getMetadata().toString() : "";
//                byte[] content = blob.getContent();
//
//                result.add(new GcsFileMetadata(url, metadata, content));
//            }
//        }
//
//        return result;
//    }
//}
