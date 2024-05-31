package com.example.demo.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.DocumentWrapper;
import com.example.demo.service.DocumentRetrievalService;


/**
//	 * Retrieves a document(multiple document) from Google Cloud Storage (GCS) based on provided metadata.
//	 * 
//	 * @param metadata A map containing metadata used to identify the document in GCS.
//	 * @return The content of the retrieved document as a string if found; otherwise, a message indicating no document was found.
//	 */
@RestController
public class DocumentRetrievalController {

  private final DocumentRetrievalService dservice;

  @Autowired
  public DocumentRetrievalController(DocumentRetrievalService dservice) {
      this.dservice = dservice;
  }
  @PostMapping("/retrieve")
  public ResponseEntity<List<String>> fetchDocuments(@RequestBody DocumentWrapper documentWrapper) {
      try {
          List<String> documentContents = dservice.retrieveDocuments(documentWrapper);
          return ResponseEntity.ok(documentContents);
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
      }
  }
  
 

 
// @PostMapping("/fetchDocuments")
//  public ResponseEntity<List<String>> fetchDocuments(@RequestBody List<DocumentWrapper> documentWrapperList) throws IOException {
//      List<String> documentContents = dservice.retrieveDocuments(documentWrapperList);
//      return new ResponseEntity<>(documentContents, HttpStatus.OK);
//  }
}






