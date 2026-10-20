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
import java.util.List;
import java.util.concurrent.CompletionStage;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
public class CertStreamClient {

    private static final Logger log = LoggerFactory.getLogger(CertStreamClient.class);

    private final String url;
    private final ObjectMapper mapper;
    private final LookalikeDetector detector;
    private final List<String> watched;

    public CertStreamClient(@Value("${certstream.url}") String url,
                            ObjectMapper mapper,
                            LookalikeDetector detector,
                            @Value("${watcher.domains}") List<String> watched) {
        this.url = url;
        this.mapper = mapper;
        this.detector = detector;
        this.watched = watched;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void connect() {
        log.info("Connecting to {}", url);
        HttpClient client = HttpClient.newHttpClient();
        client.newWebSocketBuilder()
                .buildAsync(URI.create(url), new CertStreamListener(mapper, this::scheduleReconnect, this::handleDomain))
                .exceptionally(ex -> {
                    log.error("Connect failed", ex);
                    scheduleReconnect();
                    return null;
                });
    }

    private void scheduleReconnect() {
        log.warn("Reconnecting in 5 seconds");
        CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS).execute(this::connect);
    }
    private void handleDomain(String domain) {
        for (String w : watched) {
            if (detector.isLookalike(domain, w)) {
                log.warn("LOOKALIKE: {} resembles {}", domain, w);
            }
        }
    }
    private static String normalize(String s){
        s = s.toLowerCase();
        if (s.startsWith("*.")){
            return s.substring(2);
        }
        return s;
    }

    private static class CertStreamListener implements WebSocket.Listener {
        private final ObjectMapper mapper;
        private final StringBuilder buffer = new StringBuilder();
        private final Runnable onDisconnect;
        private final Consumer<String> onDomain;

        private CertStreamListener(ObjectMapper mapper, Runnable onDisconnect, Consumer<String> onDomain) {
            this.mapper = mapper;
            this.onDisconnect = onDisconnect;
            this.onDomain = onDomain;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            log.info("Successfully connected to CertStream!");
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            buffer.append(data);
            if (last) {
                JsonNode root = mapper.readTree(buffer.toString());
                for (JsonNode d : root.path("data")){
                    onDomain.accept(normalize(d.asString()));
                }
                buffer.setLength(0);
            }
            webSocket.request(1);
            return null;
        }
        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            log.error("A WebSocket error occurred", error);
            onDisconnect.run();
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            log.info("Closed: {} {}", statusCode, reason);
            onDisconnect.run();
            return null;
        }
    }
}
