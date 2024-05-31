package com.example.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.FileMetadataDTO;
import com.example.demo.service.GCSService;




@RestController
public class GCSController {

    @Autowired
    private GCSService gcsService;

    @GetMapping("/files")
    public List<FileMetadataDTO> getFilesAndMetadata() throws IOException {
        return gcsService.getFilesAndMetadata();
    }
}






























//@RestController
//public class GCSController {
//
//    @Value("${bucket.name}")
//    private String bucketName;
//
//    @Value("${service.account.key.file.path}")
//    private String credentialsLocation;
//    
//    private static final Logger logger = LoggerFactory.getLogger(GCSController.class);
//
//    @GetMapping("/files")
//    public List<FileMetadataDTO> getFilesAndMetadata() throws IOException {
//        List<FileMetadataDTO> results = new ArrayList<>();
//
//        // Initialize Google Cloud Storage client
//        Storage storage = StorageOptions.newBuilder()
//                .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(credentialsLocation)))
//                .build()
//                .getService();
//
//        Page<Blob> blobs = storage.list(bucketName);
//        for (Blob blob : blobs.iterateAll()) {
//           
//            String metadata = blob.getMetadata() != null ? blob.getMetadata().toString() : "";
//
//            String documentUrl = "https://storage.cloud.google.com/" + bucketName + "/" + blob.getName();
//
//            //document content
//            String documentContent = new String(blob.getContent());
//
//       
//            FileMetadataDTO dto = new FileMetadataDTO(
//                    documentUrl,         
//                    metadata,           
//                    documentContent  
//            );
//
//            results.add(dto);
//
//           
//            logger.info("Document URL: {}", documentUrl);
//            logger.info("Metadata: {}", metadata);
//            logger.info("Document Content: {}", documentContent);
//        }
//
//        return results;
//    }}

    
    
    
//    @GetMapping("/files")
//    public List<FileMetadataDTO> getFilesAndMetadata() throws FileNotFoundException, IOException {
//        List<FileMetadataDTO> results = new ArrayList<>();
//
//        // Initialize Google Cloud Storage client
//        Storage storage = StorageOptions.newBuilder()
//                .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(credentialsLocation)))
//                .build()
//                .getService();
//
//        // List objects in the bucket
//        Page<Blob> blobs = storage.list(bucketName);
//        for (Blob blob : blobs.iterateAll()) {
//            // Get metadata
//            String metadata = blob.getMetadata() != null ? blob.getMetadata().toString() : "";
//
//            // Construct DTO
//            FileMetadataDTO dto = new FileMetadataDTO(
//                    blob.getMediaLink(),  // URL of the document
//                    metadata,             // Metadata
//                    new String(blob.getContent())  // Document itself
//            );
//
//            results.add(dto);
//        }
//
//        return results;
//    }
//
//
