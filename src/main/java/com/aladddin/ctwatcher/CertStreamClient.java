package com.aladddin.ctwatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;


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
        HttpClient client = HttpClient.newHttpClient();
        client.newWebSocketBuilder()
                .buildAsync(URI.create(url), new CertStreamListener())
                .join();}

    private static class CertStreamListener implements WebSocket.Listener {

        @Override
        public void onOpen(WebSocket webSocket) {
            log.info("Successfully connected to CertStream!");
            webSocket.request(1);
            WebSocket.Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            log.info("Received certificate payload: {}", data);
            webSocket.request(1);
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            log.error("A WebSocket error occurred", error);
            WebSocket.Listener.super.onError(webSocket, error);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket,
                                          int statusCode,
                                          String reason) {
            log.info("Closed: {} {}", statusCode, reason);
            return null;
        }
    }
}
