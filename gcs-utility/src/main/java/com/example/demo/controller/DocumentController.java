package com.example.demo.controller;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.DocumentService;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/retrieve")
    public String retrieveDocument(@RequestBody Map<String, String> metadata) {
        return documentService.retrieveDocument(metadata);
    }
}

