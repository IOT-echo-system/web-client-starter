package com.shiviraj.iot.webClient

import com.shiviraj.iot.loggingstarter.logOnError
import com.shiviraj.iot.loggingstarter.logOnSuccess
import com.shiviraj.iot.loggingstarter.serializer.DefaultSerializer
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Component
class WebClientWrapper(private val webClient: WebClient) {
    fun <T> get(
        baseUrl: String,
        path: String,
        returnType: Class<T>,
        queryParams: MultiValueMap<String, String> = LinkedMultiValueMap(),
        uriVariables: Map<String, Any> = emptyMap(),
        headers: Map<String, String> = emptyMap(),
        skipLoggingAdditionalDetails: Boolean = false,
        skipLoggingResponseBody: Boolean = true
    ): Mono<T> {
        val url = createUrlForRequest(baseUrl, path, uriVariables, queryParams)

        return webClient.get()
            .uri(url)
            .headers { h ->
                headers.map {
                    h.set(it.key, it.value)
                }
            }
            .retrieve()
            .bodyToMono(String::class.java)
            .map {
                DefaultSerializer.deserialize(it, returnType)
            }
            .logOnSuccess(
                message = "GET request to Service successful",
                skipAdditionalDetails = skipLoggingAdditionalDetails,
                skipResponseBody = skipLoggingResponseBody,
            )
            .logOnError(
                errorCode = "API_FAILURE",
                errorMessage = "GET request to Service failed",
                skipAdditionalDetails = skipLoggingAdditionalDetails
            )
            .contextWrite { it.put("startTime", LocalDateTime.now()) }
    }

    fun <T> post(
        baseUrl: String,
        path: String,
        body: Any,
        returnType: Class<T>,
        queryParams: MultiValueMap<String, String> = LinkedMultiValueMap(),
        uriVariables: Map<String, Any> = emptyMap(),
        headers: Map<String, String> = emptyMap(),
        skipLoggingAdditionalDetails: Boolean = false,
        skipLoggingResponseBody: Boolean = true
    ): Mono<T> {

        val url = createUrlForRequest(baseUrl, path, uriVariables, queryParams)

        return webClient
            .post().uri(url)
            .headers { h ->
                headers.map {
                    h.set(it.key, it.value)
                }
            }.bodyValue(body)
            .retrieve()
            .bodyToMono(String::class.java)
            .map {
                DefaultSerializer.deserialize(it, returnType)
            }
            .logOnSuccess(
                message = "POST request to Service successful",
                skipAdditionalDetails = skipLoggingAdditionalDetails,
                skipResponseBody = skipLoggingResponseBody,
            )
            .logOnError(
                errorCode = "API_FAILURE",
                errorMessage = "POST request to Service failed",
                skipAdditionalDetails = skipLoggingAdditionalDetails,
            )
            .contextWrite { it.put("startTime", LocalDateTime.now()) }
    }

    private fun createUrlForRequest(
        baseUrl: String,
        path: String,
        uriVariables: Map<String, Any>,
        queryParams: MultiValueMap<String, String>
    ): String {
        return baseUrl + UriComponentsBuilder
            .fromPath(path)
            .uriVariables(uriVariables)
            .queryParams(queryParams)
            .build()
            .toUriString()
    }
}
