package com.aladddin.ctwatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;


@Component
public class CertStreamClient {

    private static final Logger log = LoggerFactory.getLogger(CertStreamClient.class);

    private final String url;

    private final ObjectMapper mapper;

    public CertStreamClient(@Value("${certstream.url}") String url, ObjectMapper mapper) {
        this.url = url;
        this.mapper = mapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void connect() {
        log.info("Connecting to {}", url);
        HttpClient client = HttpClient.newHttpClient();
        client.newWebSocketBuilder()
                .buildAsync(URI.create(url), new CertStreamListener(mapper))
                .join();}

    private static class CertStreamListener implements WebSocket.Listener {
        private final ObjectMapper mapper;
        private final StringBuilder buffer = new StringBuilder();

        private CertStreamListener(ObjectMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            log.info("Successfully connected to CertStream!");
            webSocket.request(1);
            WebSocket.Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            JsonNode root = mapper.readTree(data.toString());
            buffer.append(data);
            if (last) {
                for (JsonNode d : root.path("data")){
                    log.info("Domain: {}", buffer.toString());
                }
                buffer.setLength(0);
            }
            webSocket.request(1);
            return null;
        }
        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            log.error("A WebSocket error occurred", error);
            WebSocket.Listener.super.onError(webSocket, error);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            log.info("Closed: {} {}", statusCode, reason);
            return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
        }
    }
}
