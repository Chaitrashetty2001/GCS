package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.DocumentService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/documents")
@Api(tags = "Document Management")
public class DocumentController {
	/**
//	 * Retrieves a document(single document) from Google Cloud Storage (GCS) based on provided metadata.
//	 * 
//	 * @param metadata A map containing metadata used to identify the document in GCS.
//	 * @return The content of the retrieved document as a string if found; otherwise, a message indicating no document was found.
//	 */
    @Autowired
    private DocumentService documentService;

    @PostMapping("/retrieve")
    @ApiOperation(value = "Retrieve document based on metadata")
    public String retrieveDocument(@RequestBody Map<String, String> metadata) {
        String blobName = documentService.getBlobNameFromMetadata(metadata);
        if (blobName != null) {
            return documentService.downloadDocumentContent(blobName);
        } else {
            return "No document found for the given metadata.";
        }
    }
}

