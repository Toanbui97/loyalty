package vn.com.loyalty.core.service.internal.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import vn.com.loyalty.core.constant.WebClientConstant;
import vn.com.loyalty.core.constant.enums.ResponseStatusCode;
import vn.com.loyalty.core.exception.BaseResponseException;
import vn.com.loyalty.core.service.internal.WebClientService;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Setter
@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = WebClientConstant.WEBCLIENT_RETRY_PROPERTIES_PREFIX)
public class WebClientServiceImpl implements WebClientService {

    private Integer attempt;
    private Integer firstBackoff;
    private WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public <T> Mono<T> getAsync(String baseUrl, String uri, MultiValueMap<String, String> params, Class<T> clazz) {
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = setUpUriAndBodyAndHeaders(baseUrl, uri, params, null, HttpMethod.GET);
        return requestHeadersSpec.retrieve().bodyToMono(clazz);
    }

    @Override
    public <T, R> Mono<T> postAsync(String baseUrl, String uri, R requestBody, Class<T> clazz) {
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = setUpUriAndBodyAndHeaders(baseUrl, uri, null, requestBody, HttpMethod.POST);
        return requestHeadersSpec.retrieve().bodyToMono(clazz);
    }

    @Override
    public <T> T getSync(String baseUrl, String uri, MultiValueMap<String, String> params, Class<T> clazz) {
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = setUpUriAndBodyAndHeaders(baseUrl, uri, params, null, HttpMethod.GET);
        return processMonoResponse(requestHeadersSpec, clazz);
    }

    @Override
    public <T> List<T> getListSync(String baseUrl, String uri, MultiValueMap<String, String> params, Class<T> clazz) {
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = setUpUriAndBodyAndHeaders(baseUrl, uri, params, null, HttpMethod.GET);
        return processFluxResponse(requestHeadersSpec, clazz);
    }

    @Override
    public <T, R> T postSync(String baseUrl, String uri, R requestBody, Class<T> clazz) {
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = setUpUriAndBodyAndHeaders(baseUrl, uri, null, requestBody, HttpMethod.POST);
        return processMonoResponse(requestHeadersSpec, clazz);
    }

    @Override
    public <T, R> List<T> postListSync(String baseUrl, String uri, R requestBody, Class<T> clazz) {
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = setUpUriAndBodyAndHeaders(baseUrl, uri, null, requestBody, HttpMethod.POST);
        return processFluxResponse(requestHeadersSpec, clazz);
    }

    @Override
    public <T, R> void deleteSync(String baseUrl, String uri, MultiValueMap<String, String> params, R requestBody, Class<T> clazz) {
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = setUpUriAndBodyAndHeaders(baseUrl, uri, params, requestBody, HttpMethod.DELETE);
        processMonoResponse(requestHeadersSpec, clazz);
    }

    @Override
    public <T> List<T> deleteListSync(String baseUrl, String uri, MultiValueMap<String, String> params, Class<T> clazz) {
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = setUpUriAndBodyAndHeaders(baseUrl, uri, params, null, HttpMethod.DELETE);
        return processFluxResponse(requestHeadersSpec, clazz);
    }

    @Override
    public <T, R> T updateSync(String baseUrl, String uri, R requestBody, Class<T> clazz) {
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = setUpUriAndBodyAndHeaders(baseUrl, uri, null, requestBody, HttpMethod.PUT);
        return processMonoResponse(requestHeadersSpec, clazz);
    }

    @Override
    public <T, R> List<T> updateListSync(String baseUrl, String uri, R requestBody, Class<T> clazz) {
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = setUpUriAndBodyAndHeaders(baseUrl, uri, null, requestBody, HttpMethod.PUT);
        return processFluxResponse(requestHeadersSpec, clazz);
    }

    private boolean is5xxServerError(Throwable throwable) {
        return throwable instanceof HttpServerErrorException;
    }

    @SneakyThrows
    private  <R> WebClient.RequestHeadersSpec<?> setUpUriAndBodyAndHeaders(String baseUrl, String uri, MultiValueMap<String, String> params, R requestBody, HttpMethod method) {

        webClient = webClient.mutate().baseUrl(baseUrl).build();
        WebClient.RequestBodyUriSpec requestBodyUriSpec = webClient.method(method);
        requestBodyUriSpec.uri(uriBuilder -> (Objects.nonNull(params) ? buildQueryParams(uriBuilder, uri, params) : uriBuilder.path(uri).build()))
                .headers(header -> {
                    header.setBearerAuth("");
                    header.setContentType(MediaType.valueOf(APPLICATION_JSON_VALUE));
                    header.setAccept(List.of(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML));
                    header.setAcceptCharset(List.of(StandardCharsets.UTF_8));
                });

        log.info("""
                 
                ===================> Web Client Request:
                {}: {}{}
                {}
                """
                , method, baseUrl, uri, requestBody != null ? objectMapper.writeValueAsString(requestBody) : "");


        return (Objects.nonNull(requestBody) ? requestBodyUriSpec.body(BodyInserters.fromValue(requestBody)) : requestBodyUriSpec);
    }

    public <T> T processMonoResponse(WebClient.RequestHeadersSpec<?> requestHeadersSpec, @Nullable Class<T> clazz) {
        return requestHeadersSpec.retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new BaseResponseException(ResponseStatusCode.INVALID_INPUT_DATA, null)))
                .bodyToMono(clazz)
                .retryWhen(Retry.backoff(attempt, Duration.ofSeconds(firstBackoff))
                        .filter(this::is5xxServerError)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new BaseResponseException(ResponseStatusCode.MAX_RETRY_ATTEMPTS_REACHED, ""))
                )
                .share()
                .block();
    }

    private <T> List<T> processFluxResponse(WebClient.RequestHeadersSpec<?> requestHeadersSpec, Class<T> clazz) {
        return requestHeadersSpec.retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    log.error("Error from Client side system");
                    return Mono.error(new BaseResponseException(ResponseStatusCode.INVALID_INPUT_DATA, ""));
                })
                .bodyToFlux(clazz)
                .retryWhen(Retry.backoff(attempt, Duration.ofSeconds(firstBackoff))
                        .filter(this::is5xxServerError)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new BaseResponseException(ResponseStatusCode.MAX_RETRY_ATTEMPTS_REACHED, ""))
                )
                .collectList()
                .share()
                .block();
    }

    private URI buildQueryParams(UriBuilder uriBuilder, String uri, MultiValueMap<String, String> params) {
        uriBuilder = uriBuilder.path(uri);
        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            uriBuilder.queryParam(entry.getKey(), entry.getValue().get(0));
        }
        return uriBuilder.build();
    }

}