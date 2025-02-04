package com.adolfo.test.gs.bussineslogic.config;

import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.Getter;
import reactor.core.publisher.Mono;

@Component
@Getter
public class RestClient {
    private String responseBody;
    private HttpStatus httpStatus;

    private final WebClient webClient;

    public RestClient(WebClient.Builder getWebClientBuilderRest) {
        this.webClient = getWebClientBuilderRest.build();
    }

    public JsonObject getJsonBody() {
        return new Gson().fromJson(this.responseBody, JsonObject.class);
    }

    // METODO QUE REALIZA UN CONSUMO GET
    public void callGet(Headers[] headers, @NonNull String urlService, @NonNull String endpoint, long timeoutInMillis) {
        RequestHeadersSpec<?> requestHeaderSpec = this.webClient.get()
                .uri(urlService, uri -> uri.path(endpoint).build());

        executeRequest(requestHeaderSpec, headers, timeoutInMillis);
    }

    // METODO QUE REALIZA UN CONSUMO POST
    public void callPost(Headers[] headers, @NonNull String urlService, @NonNull String endpoint,
            @NonNull String parametros, long timeoutInMillis) {
        RequestHeadersSpec<?> requestHeaderSpec = this.webClient.post()
                .uri(urlService, uri -> uri.path(endpoint).build())
                .body(BodyInserters.fromValue(parametros));

        executeRequest(requestHeaderSpec, headers, timeoutInMillis);
    }

    // METODO QUE REALIZA UN CONSUMO PUT
    public void callPut(Headers[] headers, @NonNull String urlService, @NonNull String endpoint,
            @NonNull String parametros, long timeoutInMillis) {
        RequestHeadersSpec<?> requestHeaderSpec = this.webClient.put()
                .uri(urlService, uri -> uri.path(endpoint).build())
                .body(BodyInserters.fromValue(parametros));

        executeRequest(requestHeaderSpec, headers, timeoutInMillis);
    }

    // METODO QUE REALIZA UN CONSUMO DELETE
    public void callDelete(Headers[] headers, @NonNull String urlService, @NonNull String endpoint,
            long timeoutInMillis) {
        RequestHeadersSpec<?> requestHeaderSpec = this.webClient.delete()
                .uri(urlService, uri -> uri.path(endpoint).build());

        executeRequest(requestHeaderSpec, headers, timeoutInMillis);
    }

    // METODO QUE EJECUTA UNA PETICION
    private void executeRequest(RequestHeadersSpec<?> requestHeaderSpec, Headers[] headers, long timeoutInMillis) {
        requestHeaderSpec.headers(httpHeaders -> {
            for (Headers header : headers) {
                String headerName = header.getName();

                if (headerName != null) {
                    httpHeaders.add(headerName, header.getValue());
                }
            }
        })
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(timeoutInMillis))
                .doOnSuccess(clientResponse -> {
                    this.responseBody = clientResponse;
                    this.httpStatus = HttpStatus.OK;
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    this.httpStatus = HttpStatus.valueOf(ex.getStatusCode().value());
                    this.responseBody = ex.getResponseBodyAsString();
                    return Mono.empty(); // Resume with empty Mono to continue processing
                })
                .doOnError(Throwable.class, error -> {
                    this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    this.responseBody = error.getMessage();
                })
                .block();
    }

}