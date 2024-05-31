package com.example.demo.dto;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class RetrieveDocumentsRequest {
    private String bucketname;
    private List<DocumentKey> documents;

    public String getBucketname() {
        return bucketname;
    }

    public void setBucketname(String bucketname) {
        this.bucketname = bucketname;
    }

    public List<DocumentKey> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentKey> documents) {
        this.documents = documents;
    }


}
