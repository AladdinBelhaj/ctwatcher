package com.aladddin.ctwatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class CertStreamClient {

    private static final Logger log = LoggerFactory.getLogger(CertStreamClient.class);

    private final String url;

    public CertStreamClient(@Value("${certstream.url}") String url) {
        this.url = url;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void connect() {
        log.info("Connecting to {}", url);
    }
}
