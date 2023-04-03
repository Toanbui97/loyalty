package vn.com.loyalty.core.service.internal;

import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.util.List;

public interface WebClientService {

    <T> Mono<T> getAsync(String baseUrl, String uri, MultiValueMap<String, String> params, Class<T> clazz);

    <T, R> Mono<T> postAsync(String baseUrl, String uri, R requestBody, Class<T> clazz);

    <T> T getSync(String baseUrl, String uri, MultiValueMap<String, String> params, Class<T> clazz);

    <T> List<T> getListSync(String baseUrl, String uri, MultiValueMap<String, String> params, Class<T> clazz);

    <T, R> T postSync(String baseUrl, String uri, R requestBody, Class<T> clazz);

    <T, R> List<T> postListSync(String baseUrl, String uri, R requestBody, Class<T> clazz);

    <R> void deleteSync(String baseUrl, String uri, MultiValueMap<String, String> params, R requestBody);

    <T> List<T> deleteListSync(String baseUrl, String uri, MultiValueMap<String, String> params, Class<T> clazz);

    <T, R> T updateSync(String baseUrl, String uri, R requestBody, Class<T> clazz);

    <T, R> List<T> updateListSync(String baseUrl, String uri, R requestBody, Class<T> clazz);
}
