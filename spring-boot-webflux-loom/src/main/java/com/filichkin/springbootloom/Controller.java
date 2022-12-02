package com.filichkin.springbootloom;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@RestController
public class Controller {

    private final WebClient webClient = init();
    private final String host = "http://test:7000/address/";

    private WebClient init() {
        String connectionProviderName = "myConnectionProvider";
        HttpClient httpClient = HttpClient.create(ConnectionProvider.builder(connectionProviderName)
                .maxConnections(10_000)
                .pendingAcquireMaxCount(10_000)
                .pendingAcquireTimeout(Duration.of(100, ChronoUnit.SECONDS))
                .build()
        );
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }


    @GetMapping("/address-reactive/{timeout}")
    Mono<String> getAddress(@PathVariable long timeout) {
        return getAddressInternal(timeout);
    }

    @GetMapping("/address-loom/{timeout}")
    Mono<String> getAddressWithLoom(@PathVariable long timeout) {
        return Mono.fromFuture(CompletableFuture.supplyAsync(() ->
                getAddressInternal(timeout).block(), Executors.newVirtualThreadPerTaskExecutor()));
    }

    private Mono<String> getAddressInternal(long timeout) {
        return webClient.get()
                .uri(host + timeout)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class))
                .timeout(Duration.ofSeconds(200));
    }

}
