package com.aladdin.ingest;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class CertStreamClient {


    private final String url;

    public CertStreamClient(@Value("${certstream.url}") String url) {
        this.url = url;
    }
}
